import ceylon.language.model {
    AppliedClass = Class,
    Type
}

shared interface ClassDeclaration
        satisfies ClassOrInterfaceDeclaration & FunctionalDeclaration {
    
    shared formal Boolean anonymous;
    
    shared formal actual AppliedClass<Anything, Nothing> apply(Type* types);

    shared formal actual AppliedClass<Anything, Nothing> bindAndApply(Object instance, Type* types);
}