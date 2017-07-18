package com.tenxdev.jsinterop.generator.processing.unionargsexampansion;

import com.tenxdev.jsinterop.generator.model.types.*;
import com.tenxdev.jsinterop.generator.processing.visitors.AbstractTypeVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * provides replacement types for an type containing union types
 */
public class UnionTypeReplacementVisitor extends AbstractTypeVisitor<List<Type>> {

    @Override
    public List<Type> accept(Type type) {
        return type == null ? Collections.emptyList() : super.accept(type);
    }

    @Override
    protected List<Type> visitArrayType(ArrayType type) {
        List<Type> unionTypes = accept(type.getType());
        return unionTypes.stream()
                .map(unionType->new ArrayType(unionType))
                .collect(Collectors.toList());
    }

    @Override
    protected List<Type> visitUnionType(UnionType type) {
        return type.getTypes();
    }

    @Override
    protected List<Type> visitParameterizedType(ParameterizedType type) {
        List<Type> types = accept(type.getBaseType());
        if (!types.isEmpty()){
            return types.stream()
                    .map(unionType->new ParameterizedType(unionType, type.getTypeParameters()))
                    .collect(Collectors.toList());
        }
        for (Type subType: type.getTypeParameters()){
            List<Type> unionTypes = accept(subType);
            if (!unionTypes.isEmpty()){
                List<Type> result=new ArrayList<>();
                for (Type unionType: unionTypes){
                    ArrayList<Type> newSubTypes = new ArrayList<>(type.getTypeParameters());
                    int index=newSubTypes.indexOf(subType);
                    newSubTypes.set(index, unionType);
                    result.add(new ParameterizedType(type.getBaseType(), newSubTypes));
                }
                return result;
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected List<Type> visitEnumType(EnumType type) {
        return Collections.emptyList();
    }

    @Override
    protected List<Type> visitObjectType(ObjectType type) {
        return Collections.emptyList();
    }

    @Override
    protected List<Type> visitNativeType(NativeType type) {
        return Collections.emptyList();
    }
}
