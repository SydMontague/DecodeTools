package de.phoenixstaffel.decodetools;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
        i = (i ^ (i << 1)) & 0x1515; // ---2 -1-0
        i = (i | (i >>> 7)) & 0x3F;
        return i;
    }
    
    public static BufferedImage flipImage(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }
    
    public static BufferedImage flipImageVertically(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
        return createTransformed(image, at);
    }
    
    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static List<File> fileOrder(File file) {
        List<File> files = new ArrayList<>();
        
        for(File f : file.listFiles()) {
            if(f.isFile())
                files.add(f);
        }
        
        for(File f : file.listFiles()) {
            if(f.isDirectory())
                files.addAll(fileOrder(f));
        }
        
        return files;
    }

    public static BufferedImage rotateImage(BufferedImage image, byte rotation) {
        AffineTransform at = new AffineTransform();
        at.rotate(rotation * Math.PI / 2, image.getWidth() / 2D, image.getHeight() / 2D);
        return createTransformed(image, at);
    }
}
