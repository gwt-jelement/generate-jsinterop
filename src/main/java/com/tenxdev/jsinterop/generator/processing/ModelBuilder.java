package com.tenxdev.jsinterop.generator.processing;

import com.tenxdev.jsinterop.generator.logging.Logger;
import com.tenxdev.jsinterop.generator.model.Definition;
import com.tenxdev.jsinterop.generator.model.Model;
import com.tenxdev.jsinterop.generator.parsing.FileAwareANTLRErrorListener;
import com.tenxdev.jsinterop.generator.parsing.ParsingContext;
import com.tenxdev.jsinterop.generator.parsing.visitors.firstpass.DefinitionsScanner;
import com.tenxdev.jsinterop.generator.parsing.visitors.secondpass.DefinitionsVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr4.webidl.WebIDLLexer;
import org.antlr4.webidl.WebIDLParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ModelBuilder {
    private final Logger logger;

    public ModelBuilder(Logger logger) {
        this.logger = logger;
    }

    public Model buildFrom(String inputDirectory) throws IOException {
        ParsingContext parsingContext = performFirstPass(inputDirectory);
        return performSecondPass(inputDirectory, parsingContext);
    }

    /**
     * first pass is to locate all object types and type definitions, to
     * register them in the type factory
     *
     * @param inputDirectory the input directory
     * @return a parsing context
     */
    private ParsingContext performFirstPass(String inputDirectory) throws IOException {
        logger.info(Logger.LEVEL_INFO, () -> "Parsing pass 1-scanning object types");
        List<File> fileList = new FileListBuilder(logger).findFiles(inputDirectory);
        int offset = new File(inputDirectory).getAbsolutePath().length();
        ParsingContext parsingContext = new ParsingContext(logger);
        for (File file : fileList) {
            parsingContext.setPackageSuffix(getPackageSuffix(offset, file));
            scanFile(file, parsingContext);
        }
        parsingContext.getTypeFactory().fixUpDeferredTypeDefinitions();
        return parsingContext;
    }

    private Model performSecondPass(String inputDirectory, ParsingContext parsingContext) throws IOException {
        logger.info(Logger.LEVEL_INFO, () -> "Parsing pass 2-building model");
        Model model = new Model();
        List<File> fileList = new FileListBuilder(logger).findFiles(inputDirectory);
        int offset = new File(inputDirectory).getAbsolutePath().length();
        for (File file : fileList) {
            String packageSuffix = getPackageSuffix(offset, file);
            parsingContext.setPackageSuffix(packageSuffix);
            List<Definition> definitions = parseFile(file, parsingContext);
            for (Definition definition : definitions) {
                try {
                    model.registerDefinition(definition, packageSuffix, file.getAbsolutePath());
                } catch (Model.ConflictingNameException conflictingNameException) {
                    logger.formatError("Name collision detected:%n\t%s is defined in package %s in file %s%n" +
                                    "\t%s is also defined in package %s in file %s%n",
                            conflictingNameException.getDefinitionInfo().getDefinition().getName(),
                            conflictingNameException.getDefinitionInfo().getPackageName(),
                            conflictingNameException.getDefinitionInfo().getFilename(),
                            definition.getName(), packageSuffix, file.getAbsolutePath());
                    logger.reportError("Definition 1:");
                    logger.reportError(conflictingNameException.getDefinitionInfo().getDefinition().toString());
                    logger.reportError("Definition 2:");
                    logger.reportError(definition.toString());
                    logger.reportError("");
                }
            }
        }
        model.setTypeFactory(parsingContext.getTypeFactory());
        return model;
    }

    private void scanFile(File file, ParsingContext parsingContext) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            WebIDLLexer lexer = new WebIDLLexer(new ANTLRInputStream(reader));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            WebIDLParser parser = new WebIDLParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new FileAwareANTLRErrorListener(file, logger));
            WebIDLParser.DefinitionsContext definitions = parser.definitions();
            definitions.accept(new DefinitionsScanner(parsingContext));
        }

    }

    private List<Definition> parseFile(File file, ParsingContext parsingContext) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            WebIDLLexer lexer = new WebIDLLexer(new ANTLRInputStream(reader));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            WebIDLParser parser = new WebIDLParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new FileAwareANTLRErrorListener(file, logger));
            WebIDLParser.DefinitionsContext definitions = parser.definitions();
            return definitions.accept(new DefinitionsVisitor(parsingContext));
        }
    }

    private String getPackageSuffix(int offset, File file) {
        return file.getParentFile().getAbsolutePath().substring(offset).replace(File.separator, ".");
    }

}
