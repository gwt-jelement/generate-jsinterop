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

import com.tenxdev.jsinterop.generator.model.interfaces.InterfaceMember;
import com.tenxdev.jsinterop.generator.parsing.ParsingContext;
import org.antlr4.webidl.WebIDLParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class InterfaceMembersVisitor extends ContextWebIDLBaseVisitor<List<InterfaceMember>> {

    private final String containingType;

    InterfaceMembersVisitor(ParsingContext parsingContext, String containingType) {
        super(parsingContext);
        this.containingType = containingType;
    }

    @Override
    public List<InterfaceMember> visitInterfaceMembers(WebIDLParser.InterfaceMembersContext ctx) {
        if (ctx.isEmpty()) {
            return Collections.emptyList();
        }
        List<InterfaceMember> interfaceMembers = new ArrayList<>();
        for (WebIDLParser.InterfaceMembersContext members = ctx; members != null; members = members.interfaceMembers()) {
            InterfaceMember member = null;
            List<String> extendedAttributes = members.extendedAttributeList() != null ?
                    members.extendedAttributeList().accept(new GenericExtendedAttributeListVisitor()) :
                    null;
            if (members.interfaceMember() != null) {
                member = members.interfaceMember().accept(
                        new InterfaceMemberVisitor(parsingContext, containingType, extendedAttributes));
            }
            if (member != null) {
                interfaceMembers.add(member);
            }
        }
        return interfaceMembers;
    }
}
