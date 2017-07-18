package com.tenxdev.jsinterop.generator.generator

import com.tenxdev.jsinterop.generator.model.DefinitionInfo
import com.tenxdev.jsinterop.generator.model.InterfaceDefinition
import java.util.Collections

class InterfaceGenerator extends Template{

    def generate(String basePackageName, DefinitionInfo definitionInfo){
        var definition=definitionInfo.getDefinition() as InterfaceDefinition
        Collections.sort(definition.methods)
        return '''
package «basePackageName»«definitionInfo.getPackageName()»;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

«FOR importName: definitionInfo.importedPackages»
import «if(importName.startsWith(".")) basePackageName else ""»«importName»;
«ENDFOR»

@JsType(namespace = JsPackage.GLOBAL, isNative = true)
public class «definition.name.adjustJavaName»«
        IF definition.parent!==null» extends «definition.parent.displayValue»«ENDIF» {

    «FOR unionType: definition.getUnionReturnTypes»
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface «unionType.name» {
        «FOR type: unionType.types»
        @JsOverlay
        default «type.displayValue» as«type.displayValue.toFirstUpper»(){
            return Js.cast(this);
        }

        «ENDFOR»
        «FOR type: unionType.types»
        @JsOverlay
        default boolean is«type.displayValue.toFirstUpper»(){
            return (Object) this instanceof «type.displayValue»;
        }

        «ENDFOR»
    }

    «ENDFOR»
    «FOR attribute: definition.attributes»
//        @JsProperty(name="«attribute.name»")
//        public native «attribute.type.displayValue» get«attribute.name.toFirstUpper»();
        «IF !attribute.readOnly»

//        @JsProperty(name="«attribute.name»")
//        public native void set«attribute.name.toFirstUpper»(«attribute.type.displayValue» «attribute.name»);
        «ENDIF»

    «ENDFOR»
    «FOR method: definition.methods SEPARATOR "\n"»
    @JsMethod(name = "«method.name»")
    public native «method.returnType.displayValue» «method.name.adjustJavaName»(«
        FOR argument: method.arguments SEPARATOR ", "
        »«argument.type.displayValue» «argument.name.adjustJavaName»«ENDFOR»);
    «ENDFOR»

}


    '''
    }
}