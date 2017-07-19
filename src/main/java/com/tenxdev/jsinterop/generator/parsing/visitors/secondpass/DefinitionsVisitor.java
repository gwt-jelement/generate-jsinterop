package com.tenxdev.jsinterop.generator.parsing.visitors.secondpass;

import com.tenxdev.jsinterop.generator.model.Definition;
import com.tenxdev.jsinterop.generator.model.Method;
import com.tenxdev.jsinterop.generator.parsing.ParsingContext;
import com.tenxdev.jsinterop.generator.processing.ParserUtil;
import org.antlr4.webidl.WebIDLParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefinitionsVisitor extends ContextWebIDLBaseVisitor<List<Definition>> {

    public DefinitionsVisitor(ParsingContext parsingContext) {
        super(parsingContext);
    }

    @Override
    public List<Definition> visitDefinitions(WebIDLParser.DefinitionsContext ctx) {
        List<Definition> definitionList = new ArrayList<>();

        WebIDLParser.DefinitionsContext definitions = ctx;
        while (definitions != null && definitions.definition() != null) {
            List<Method> constructors = definitions.extendedAttributeList() != null ?
                    ctx.extendedAttributeList().accept(new ExtendedAttributeListVisitor(parsingContext))
                    : Collections.emptyList();
            Definition definition = definitions.definition().accept(new DefinitionVisitor(parsingContext, constructors));
            if (definition != null) {
                definitionList.add(definition);
            } else {
                parsingContext.getlogger().reportError("Unexpected missed definition: " + ParserUtil.getText(ctx));
            }
            definitions = definitions.definitions();
        }
        return definitionList;
    }
}
