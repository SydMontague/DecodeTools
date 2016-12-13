package de.phoenixstaffel.decodetools;

public class Utils {
    private Utils() {
    }
    
    public static int getPadded(int value, int paddingSize) {
        if (value % paddingSize == 0)
            return value;
        
        return value + (paddingSize - (value % paddingSize));
    }

    public static long getPadded(long value, int paddingSize) {
        if (value % paddingSize == 0)
            return value;
        
        return value + (paddingSize - (value % paddingSize));
    }
}
