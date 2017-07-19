/*
 * Copyright 2017 Abed Tony BenBrahim <tony.benrahim@10xdev.com>
 *     and Gwt-JElement project contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tenxdev.jsinterop.generator.parsing.visitors.secondpass;

import com.tenxdev.jsinterop.generator.model.Method;
import com.tenxdev.jsinterop.generator.model.MethodArgument;
import com.tenxdev.jsinterop.generator.parsing.ParsingContext;
import org.antlr4.webidl.WebIDLParser;

import java.util.Collections;
import java.util.List;

class ConstructorVisitor extends ContextWebIDLBaseVisitor<Method> {

    public ConstructorVisitor(ParsingContext parsingContext) {
        super(parsingContext);
    }

    @Override
    public Method visitExtendedAttributeRest(WebIDLParser.ExtendedAttributeRestContext ctx) {
        if (ctx.extendedAttribute() == null) {
            return new Method("", null, Collections.emptyList(), false, false, null);
        }
        return ctx.extendedAttribute().accept(this);
    }

    @Override
    public Method visitExtendedAttribute(WebIDLParser.ExtendedAttributeContext ctx) {
        List<MethodArgument> arguments = ctx.extendedAttributeInner() != null ?
                ctx.extendedAttributeInner().accept(new ConstructorArgumentsVisitor(parsingContext)) :
                Collections.emptyList();
        return new Method("", null, arguments, false, false, null);
    }
}
