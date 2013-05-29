import ceylon.language.metamodel {
    AppliedClass = Class,
    Type
}

shared interface ClassDeclaration
        satisfies ClassOrInterfaceDeclaration {
    shared formal actual AppliedClass<Anything, Nothing> apply(Type* types);

    shared formal actual AppliedClass<Anything, Nothing> bindAndApply(Object instance, Type* types);
    
    shared formal Parameter[] parameters;
}