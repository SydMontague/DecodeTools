package de.phoenixstaffel.decodetools;

import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

public class TestUtils {
    private TestUtils() {
    }
    
    public static <T extends Exception> void assertException(Class<T> expected, Callable<?> input) {
        Exception ee = null;
        try {
            input.call();
        }
        catch (Exception e) {
            ee = e;
        }
        
        if (!expected.isInstance(ee))
            fail("Expected <" + expected.getName() + "> to be thrown.");
    }
}
