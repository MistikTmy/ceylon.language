
shared interface Method<in Container, out Type, in Arguments>
        satisfies FunctionType<Type, Arguments> & Member<Container, Function<Type, Arguments>>
        given Arguments satisfies Anything[] {
}
