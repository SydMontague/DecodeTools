package de.phoenixstaffel.decodetools.core;

/**
 * Represents an operation that accepts 4 inputs and returns no result.
 *
 * @param <A> the type of the first input to the operation
 * @param <B> the type of the second input to the operation
 * @param <C> the type of the third input to the operation
 * @param <D> the type of the fourth input to the operation
 */
@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
    /**
     * Performs this operation on the given arguments.
     * 
     * @param a the first input argument
     * @param b the second input argument
     * @param c the third input argument
     * @param d the fourth input argument
     */
    public void accept(A a, B b, C c, D d);
}
