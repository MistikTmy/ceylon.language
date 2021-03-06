package com.redhat.ceylon.compiler.java.runtime.metamodel;

import ceylon.language.Sequential;
import ceylon.language.model.Function;
import ceylon.language.model.FunctionModel$impl;
import ceylon.language.model.Method$impl;
import ceylon.language.model.Model$impl;
import ceylon.language.model.declaration.FunctionDeclaration;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;
import com.redhat.ceylon.compiler.java.metadata.Variance;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;

@Ceylon(major = 5)
@com.redhat.ceylon.compiler.java.metadata.Class
@TypeParameters({
    @TypeParameter(value = "Container", variance = Variance.IN),
    @TypeParameter(value = "Type", variance = Variance.OUT),
    @TypeParameter(value = "Arguments", variance = Variance.IN, satisfies = "ceylon.language::Sequential<ceylon.language::Anything>"),
})
public class AppliedMethod<Container, Type, Arguments extends Sequential<? extends Object>> 
    extends AppliedMember<Container, ceylon.language.model.Function<? extends Type, ? super Arguments>> 
    implements ceylon.language.model.Method<Container, Type, Arguments> {

    private FreeFunction declaration;
    private ProducedTypedReference appliedFunction;
    private ceylon.language.model.Type closedType;
    @Ignore
    private TypeDescriptor $reifiedType;
    @Ignore
    private TypeDescriptor $reifiedArguments;

    @Override
    public String toString() {
        return Metamodel.getProducedTypedReferenceString(appliedFunction);
    }
    
    public AppliedMethod(@Ignore TypeDescriptor $reifiedContainer, 
                         @Ignore TypeDescriptor $reifiedType, 
                         @Ignore TypeDescriptor $reifiedArguments, 
                         ProducedTypedReference appliedFunction, 
                         FreeFunction declaration,
                         ceylon.language.model.ClassOrInterface<? extends Object> container) {
        super($reifiedType, TypeDescriptor.klass(ceylon.language.model.Function.class, $reifiedType, $reifiedArguments), container);
        this.$reifiedType = $reifiedType;
        this.$reifiedArguments = $reifiedArguments;
        this.appliedFunction = appliedFunction;
        this.declaration = declaration;
        this.closedType = Metamodel.getAppliedMetamodel(Metamodel.getFunctionReturnType(appliedFunction));
    }

    @Override
    @Ignore
    public Model$impl $ceylon$language$model$Model$impl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Ignore
    public Method$impl<Container, Type, Arguments> $ceylon$language$model$Method$impl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Ignore
    public FunctionModel$impl<Type, Arguments> $ceylon$language$model$FunctionModel$impl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @TypeInfo("ceylon.language.model.declaration::FunctionDeclaration")
    public FunctionDeclaration getDeclaration() {
        return declaration;
    }

    @Override
    @TypeInfo("ceylon.language.model::Type")
    public ceylon.language.model.Type getType() {
        return closedType;
    }

    @Override
    protected Function<Type, Arguments> bindTo(Object instance) {
        return new AppliedFunction($reifiedType, $reifiedArguments, appliedFunction, declaration, instance);
    }

    @Ignore
    @Override
    public TypeDescriptor $getType() {
        return TypeDescriptor.klass(AppliedMethod.class, super.$reifiedType, $reifiedType, $reifiedArguments);
    }
}
