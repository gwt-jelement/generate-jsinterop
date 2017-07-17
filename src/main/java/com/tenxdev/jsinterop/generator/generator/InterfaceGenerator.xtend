package com.tenxdev.jsinterop.generator.generator

import com.tenxdev.jsinterop.generator.model.DefinitionInfo
import com.tenxdev.jsinterop.generator.model.InterfaceDefinition

class InterfaceGenerator extends Template{

    def generate(String basePackageName, DefinitionInfo definitionInfo){
        var definition=definitionInfo.getDefinition() as InterfaceDefinition
        return '''
package «basePackageName»«definitionInfo.getPackgeName()»;

«IF !definition.methods.empty»import jsinterop.annotations.JsMethod;«ENDIF»
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

«FOR importName: definitionInfo.getImportedPackages»
import «if(importName.startsWith(".")) basePackageName else ""»«importName»;
«ENDFOR»

@JsType(namespace = JsPackage.GLOBAL, isNative = true)
public class «definition.getName.adjustJavaName»{
    «FOR method: definition.methods»

    @JsMethod(name = "«method.name»")
    public native «method.returnType.displayValue» «method.name.adjustJavaName»(«
        FOR argument: method.arguments SEPARATOR ", "
        »«argument.type.displayValue» «argument.name.adjustJavaName»«ENDFOR»);
    «ENDFOR»

}


    '''
    }
}