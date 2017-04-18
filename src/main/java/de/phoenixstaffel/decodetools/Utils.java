package de.phoenixstaffel.decodetools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    
    public static BufferedImage flipImage(BufferedImage image) {
        int[] original = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        int[] flipped = new int[image.getWidth() * image.getHeight()];
        
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++)
                flipped[x + y * image.getWidth()] = original[x + (image.getHeight() - y - 1) * image.getWidth()];
            
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), flipped, 0, image.getWidth());
        return image;
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
}
