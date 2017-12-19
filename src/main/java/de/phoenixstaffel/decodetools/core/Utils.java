package de.phoenixstaffel.decodetools.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Utils() {
    }
    
    /**
     * <p>
     * Aligns an input to a given alignment.
     * </p>
     * The returned value will be:
     * <ul>
     * <li>larger or equal to the input
     * <li>evenly divisible by the alignment (-> value % alignment == 0)
     * </ul>
     * 
     * @param input the value to be aligned
     * @param alignment the alignment, must be larger than 0
     * @return the aligned value
     */
    public static int align(int input, int alignment) {
        if(alignment <= 0)
            throw new IllegalArgumentException("Can't align to an alignment of 0 or less!");
        
        if (input % alignment == 0)
            return input;
        
        int value = input + (alignment - Math.abs(input % alignment));
        
        if(value < input)
            throw new IllegalArgumentException("New value is smaller than the input, have you encountered an overflow?");
        
        return value;
    }

    /**
     * <p>
     * Aligns an input to a given alignment.
     * </p>
     * The returned value will be:
     * <ul>
     * <li>larger or equal to the input
     * <li>evenly divisible by the alignment (-> value % alignment == 0)
     * </ul>
     * 
     * @param input the value to be aligned
     * @param alignment the alignment, must be larger than 0
     * @return the aligned value
     */
    public static long align(long input, int alignment) {
        if(alignment <= 0)
            throw new IllegalArgumentException("Can't align to an alignment of 0 or less!");
        
        if (input % alignment == 0)
            return input;

        long value = input + (alignment - Math.abs(input % alignment));
        
        if(value < input)
            throw new IllegalArgumentException("New value is smaller than the input, have you encountered an overflow?");
        
        return value;
    }
    
    public static BufferedImage flipImage(BufferedImage image) {
        return flipImage(image, false);
    }
    
    public static BufferedImage flipImage(BufferedImage image, boolean newImage) {
        BufferedImage target = newImage ? new BufferedImage(image.getWidth(), image.getHeight(), image.getType()) : image;
        
        int[] original = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        int[] flipped = new int[image.getWidth() * image.getHeight()];
        
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++)
                flipped[x + y * image.getWidth()] = original[x + (image.getHeight() - y - 1) * image.getWidth()];
            
        target.setRGB(0, 0, image.getWidth(), image.getHeight(), flipped, 0, image.getWidth());
        return target;
    }
    
    public static BufferedImage flipImageVertically(BufferedImage image) {
        int[] original = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        int[] flipped = new int[image.getWidth() * image.getHeight()];
        
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++)
                flipped[x + y * image.getWidth()] = original[(image.getWidth() - x - 1) + y * image.getWidth()];
            
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), flipped, 0, image.getWidth());
        return image;
    }
    
    public static List<File> fileOrder(File file) {
        List<File> files = new ArrayList<>();
        
        for (File f : file.listFiles()) {
            if (f.isFile())
                files.add(f);
        }
        
        for (File f : file.listFiles()) {
            if (f.isDirectory())
                files.addAll(fileOrder(f));
        }
        
        return files;
    }
    
    /**
     * Checks whether a given number is a power of 2.
     * 
     * @param x the number to check
     * @return true when the number is power of 2, false otherwise
     */
    public static boolean isPowOf2(int x) {
        /*
         * A number can only be a power of 2 if only exactly one bit is set.
         * Doing an AND operation on the value and the value-1 will always result in 0 if it's a power of 2.
         * 
         *   01000000 (64)
         * & 00111111 (64-1 -> 63)
         * = 00000000 (0)
         *   
         *   01001100 (76)
         * & 01001011 (76-1 -> 75)
         * = 01001000 (72)
         */
        return x >= 0 && (x & (x - 1)) == 0;
    }
    
    /**
     * Returns a cropped value to be between a lower and an upper boundary.
     * If the given input is smaller than the lower boundary the lower boundary is returned,
     * if the given input is larger than the upper boundary the upper boardary is returned.
     * Otherwise, the input itself is returned.
     * 
     * @param input the value to crop
     * @param b1 the first boundary
     * @param b2 the second boundary
     * @return the cropped value
     */
    public static long crop(long input, long b1, long b2) {
        long min = b1 < b2 ? b1 : b2;
        long max = b1 < b2 ? b2 : b1;
        
        return Math.min(Math.max(min, input), max);
    }
    
    /**
     * Returns the sum of two numbers where the second input is interpreted as a 3-bit signed integer.
     * 
     * @param base the first summand
     * @param toAdd the second, 3-bit, summand, must be between 0 and 7 (inclusive)
     * @return the sum of the two numbers, interpreting the second input as 3-bit signed number
     */
    public static long add3BitSigned(long base, long toAdd) {
        if (toAdd < 0 || toAdd > 7)
            throw new IllegalArgumentException("Second argument must be between 0 and 7 (inclusive), but was " + toAdd);
        
        if ((toAdd & 0x4) == 0) //bit 3 is the signed bit, it being 0 means we can add normally
            return base + toAdd;
        
        return base - ((~toAdd & 0x3) + 1); //build the two's complement and subtract it
    }
    
    /**
     * Returns whether a bit in a given number is set or not set.
     * 
     * @param value the value to check
     * @param bit the index of the bit, starting at 0 at the LSB. Must be between 0 and 63
     * @return true when the bit is 1, false otherwise
     */
    public static boolean getBitValue(long value, int bit) {
        if (bit >= Long.SIZE || bit < 0)
            throw new IllegalArgumentException("Can't get the " + bit + " bit of a 64bit number.");
        
        return (value >>> bit & 0x1) != 0;
    }
    
    public static long getSubInteger(long value, int bit, int length) {
        if (bit < 0 || length <= 0 || Long.SIZE - bit < length)
            throw new IllegalArgumentException("Can't get bits " + bit + " to " + (bit + length) + " of a long int.");
        
        return (value << Long.SIZE - length - bit) >>> (Long.SIZE - length);
    }
    
    public static long extend4To8(long value) {
        long tmp = value & 0xF;
        
        return (tmp << 4) + tmp;
    }
    
    public static long extend5To8(long value) {
        long tmp = value & 0x1F;
        
        return (tmp << 3) + (tmp >>> 2);
    }
    
    public static long extend6To8(long value) {
        long tmp = value & 0x3F;
        
        return (tmp << 2) + (tmp >>> 4);
    }

    public static int[] untile(short width, short height, int[] pixelData) {
        int[] data = new int[width * height];
        
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int posY = height - 1 - i;
                int coarseY = posY & ~7;
                int offset = getMortonOffset(j, posY) + coarseY * width;
                data[i * width + j] = pixelData[offset];
            }
        return data;
    }
    
    public static int[] tile(int width, int height, int[] pixelData) {
        int[] data = new int[width * height];
        
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int posY = height - 1 - i;
                int coarseY = posY & ~7;
                int offset = getMortonOffset(j, posY) + coarseY * width;
                data[offset] = pixelData[i * width + j];
            }
        
        return data;
    }
    
    private static int getMortonOffset(int x, int y) {
        int blockHeight = 8;
        int coarseX = x & ~7;
        int i = mortonInterleave(x, y);
        
        return i + coarseX * blockHeight;
    }
    
    private static int mortonInterleave(int x, int y) {
        int i = (x & 7) | ((y & 7) << 8);
        i = (i ^ (i << 2)) & 0x1313;
        i = (i ^ (i << 1)) & 0x1515;
        i = (i | (i >>> 7)) & 0x3F;
        return i;
    }
}
