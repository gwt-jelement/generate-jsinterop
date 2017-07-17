package com.tenxdev.jsinterop.generator.parsing.visitors.firstpass;

import com.tenxdev.jsinterop.generator.model.types.Type;
import com.tenxdev.jsinterop.generator.parsing.ParsingContext;
import com.tenxdev.jsinterop.generator.parsing.visitors.secondpass.ContextWebIDLBaseVisitor;
import org.antlr4.webidl.WebIDLParser;

public class TypeDefScanner extends ContextWebIDLBaseVisitor<Void> {


    TypeDefScanner(ParsingContext parsingContext) {
        super(parsingContext);
    }

    @Override
    public Void visitTypedef(WebIDLParser.TypedefContext ctx) {
        String name = ctx.IDENTIFIER_WEBIDL().getText();
        Type type = ctx.type().accept(new TypeScanner(parsingContetxt));
        parsingContetxt.getTypeFactory().registerTypeDef(name, type);
        return null;
    }
}
