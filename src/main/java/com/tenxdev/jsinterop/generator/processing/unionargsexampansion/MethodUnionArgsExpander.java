package com.tenxdev.jsinterop.generator.processing.unionargsexampansion;

import com.tenxdev.jsinterop.generator.model.InterfaceDefinition;
import com.tenxdev.jsinterop.generator.model.Method;
import com.tenxdev.jsinterop.generator.model.MethodArgument;
import com.tenxdev.jsinterop.generator.model.Model;
import com.tenxdev.jsinterop.generator.model.types.Type;
import com.tenxdev.jsinterop.generator.model.types.UnionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class generates new methods for methods with union type arguments, and removes the definition of methods with
 * union types
 * for example, given foo( (HTMLImageElement or SVGImageElement or HTMLVideoElement ) image, double x, double y)
 * The following methods will be created, keeping a pointer to the original method, and the original method will be
 * removed from the definition:
 * - foo(HTMLImageElement image, double x, double y)
 * - foo(SVGImageElement image, double x, double y)
 * - foo(HTMLVideoElement image, double x, double y)
 */
public class MethodUnionArgsExpander {
    private final Model model;
    private final GetUnionTypesVisitor getUnionTypesVisitor=new GetUnionTypesVisitor();

    public MethodUnionArgsExpander(Model model) {
        this.model = model;
    }

    public void processModel() {
        model.getDefinitions().forEach(definitionInfo -> {
            if (definitionInfo.getDefinition() instanceof InterfaceDefinition) {
                processInterface((InterfaceDefinition) definitionInfo.getDefinition());
            }
        });
    }

    private void processInterface(InterfaceDefinition definition) {
        expandMethodArguments(definition);
        expandConstructorArguments(definition);
        findUnionReturnTypes(definition);
    }

    private void expandMethodArguments(InterfaceDefinition definition) {
        List<Method> newMethods = processMethods(definition.getMethods());
        definition.getMethods().clear();
        definition.getMethods().addAll(newMethods);
    }

    private void expandConstructorArguments(InterfaceDefinition definition) {
        List<Method> newConstructors = processMethods(definition.getConstructors());
        definition.getConstructors().clear();
        definition.getConstructors().addAll(newConstructors);
    }

    private void findUnionReturnTypes(InterfaceDefinition definition) {
        List<UnionType> unionReturnTypes = definition.getMethods().stream()
                .map(method -> getUnionTypesVisitor.accept(method.getReturnType()))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        definition.setUnionReturnTypes(unionReturnTypes);
    }

    private List<Method> processMethods(List<Method> methods) {
        List<Method> newMethods = new ArrayList<>();
        methods.forEach(method -> processMethod(method, newMethods));
        return newMethods;
    }

    private void processMethod(Method method, List<Method> newMethods) {
        UnionTypeReplacementVisitor unionTypeVisitor = new UnionTypeReplacementVisitor();
        for (MethodArgument methodArgument : method.getArguments()) {
            List<Type> suggestedTypes = unionTypeVisitor.accept(methodArgument.getType());
            if (!suggestedTypes.isEmpty()) {
                processArgument(method, methodArgument, suggestedTypes, newMethods);
                return;
            }
        }
        newMethods.add(method);
    }

    private void processArgument(Method method, MethodArgument argument, List<Type> suggestedTypes, List<Method> newMethods) {
        int argumentIndex = method.getArguments().indexOf(argument);
        for (Type type : suggestedTypes) {
            List<MethodArgument> newArguments = new ArrayList<>(method.getArguments());
            newArguments.set(argumentIndex, new MethodArgument(argument.getName(), type, argument.isVararg(),
                    argument.isOptional(), argument.getDefaultValue()));
            Method newMethod = new Method(method.getName(), method.getReturnType(), newArguments, method.isStatic());
            processMethod(newMethod, newMethods);
        }
    }


}
