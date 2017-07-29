package de.phoenixstaffel.decodetools;

@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
    public void apply(A a, B b, C c, D d);
}
