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
package com.tenxdev.jsinterop.generator.generator

import com.tenxdev.jsinterop.generator.model.DefinitionInfo;
import com.tenxdev.jsinterop.generator.model.EnumDefinition;

class EnumGenerator extends Template {

    def generate(String basePackageName, DefinitionInfo definitionInfo){
        var definition=definitionInfo.getDefinition() as EnumDefinition;
        return '''
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
package «basePackageName»«definitionInfo.getPackageName()»;

public enum «definition.name»{
    «FOR value: definition.values SEPARATOR ",\n" AFTER ";\n"
        »«value.enumValueToJavaName().toUpperCase()»(«value»)«ENDFOR»

    private String internalValue;

    «definition.name»(String internalValue){
        this.internalValue = internalValue;
    }

    public String getInternalValue(){
        return this.internalValue;
    }

    public static «definition.name» of(«enumMemberType(definition)» value){
        switch(value){
            «FOR value: definition.values»
            case «value»:
                return «value.enumValueToJavaName().toUpperCase()»;
            «ENDFOR»
            default:
                return null;
        }
    }
}
    '''
    }

    def enumMemberType(EnumDefinition definition){
        if (definition.values.get(0).startsWith("\"")) "String" else "int"
    }


}