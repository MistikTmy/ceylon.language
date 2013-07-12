import ceylon.language.metamodel.declaration {
    AttributeDeclaration
}
import ceylon.language.metamodel {
    ClosedType = Type
}

// FIXME it's not a type so let's find a better name
shared interface AttributeType<out Type>
        satisfies DeclarationType {

    shared formal actual AttributeDeclaration declaration;
    
    shared formal ClosedType type;
}
