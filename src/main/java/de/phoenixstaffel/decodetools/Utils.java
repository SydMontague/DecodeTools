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
    
    public static int[][] untile2(int width, int height, int[][] pixelData) {
        int[][] data = new int[width * height][];
        
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int posY = height - 1 - i;
                int coarseY = posY & ~7;
                int offset = getMortonOffset(j, posY) + coarseY * width;
                data[i * width + j] = pixelData[offset];
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
    
    public static int[] convertFromRGBA8(byte[] a, int width, int height) {
        int[] data = new int[a.length / 4];
        for (int i = 0; i < data.length; i++) {
            data[i] += (Byte.toUnsignedInt(a[i * 4]) << 24);
            data[i] += (Byte.toUnsignedInt(a[i * 4 + 1]) << 0);
            data[i] += (Byte.toUnsignedInt(a[i * 4 + 2]) << 8);
            data[i] += (Byte.toUnsignedInt(a[i * 4 + 3]) << 16);
        }
        return data;
    }
    
    public static int[] convertFromRGB8(byte[] a, int width, int height) {
        int[] data = new int[a.length / 3];
        for (int i = 0; i < data.length; i++) {
            data[i] += 255 << 24;
            data[i] += (Byte.toUnsignedInt(a[i * 3]) << 0);
            data[i] += (Byte.toUnsignedInt(a[i * 3 + 1]) << 8);
            data[i] += (Byte.toUnsignedInt(a[i * 3 + 2]) << 16);
        }
        return data;
    }
    
    public static int[] convertFromRGBA5551(byte[] a, int width, int height) {
        int[] data = new int[a.length / 2];
        for (int i = 0; i < data.length; i++) {
            int value = (Byte.toUnsignedInt(a[i * 2 + 1]) << 8) + Byte.toUnsignedInt(a[i * 2]);
            data[i] += ((value & 0x01) != 0 ? 255 : 0) << 24;
            data[i] += ETC1Util.extend5To8(((value >>> 1) & 0x1F)) << 0;
            data[i] += ETC1Util.extend5To8(((value >>> 6) & 0x1F)) << 8;
            data[i] += ETC1Util.extend5To8(((value >>> 11) & 0x1F)) << 16;
        }
        return data;
    }
    
    public static int[] convertFromRGBA4(byte[] a, int width, int height) {
        int[] data = new int[a.length / 2];
        for (int i = 0; i < data.length; i++) {
            int value = (Byte.toUnsignedInt(a[i * 2 + 1]) << 8) + Byte.toUnsignedInt(a[i * 2]);
            data[i] += ETC1Util.extend4To8(value & 0xF) << 24;
            data[i] += ETC1Util.extend4To8(((value >>> 4) & 0x1F)) << 0;
            data[i] += ETC1Util.extend4To8(((value >>> 8) & 0xF)) << 8;
            data[i] += ETC1Util.extend4To8(((value >>> 12) & 0xF)) << 16;
        }
        return data;
    }
    
    public static int[] convertFromRGB565(byte[] a, int width, int height) {
        int[] data = new int[a.length / 2];
        for (int i = 0; i < data.length; i++) {
            int value = (Byte.toUnsignedInt(a[i * 2 + 1]) << 8) + Byte.toUnsignedInt(a[i * 2]);
            data[i] += 255 << 24;
            data[i] += ETC1Util.extend5To8((value & 0x1F)) << 0;
            data[i] += ETC1Util.extend6To8(((value & 0x7E0) >>> 5)) << 8;
            data[i] += ETC1Util.extend5To8(((value & 0xF800) >>> 11)) << 16;
        }
        return data;
    }
    
    public static int[] convertFromLA4(byte[] a, int width, int height) {
        int[] data = new int[a.length];
        
        for (int i = 0; i < data.length; i++) {
            data[i] += ETC1Util.extend4To8(a[i] & 0xF) << 24;
            data[i] += ETC1Util.extend4To8((a[i] >>> 4) & 0xF);
            data[i] += ETC1Util.extend4To8((a[i] >>> 4) & 0xF) << 8;
            data[i] += ETC1Util.extend4To8((a[i] >>> 4) & 0xF) << 16;
        }
        
        return data;
    }
    
    public static int[] convertFromLA8(byte[] a, int width, int height) {
        int[] data = new int[a.length / 2];
        
        for (int i = 0; i < data.length; i++) {
            data[i] += (Byte.toUnsignedInt(a[i * 2 + 1])) << 24;
            data[i] += (Byte.toUnsignedInt(a[i * 2]));
            data[i] += (Byte.toUnsignedInt(a[i * 2])) << 8;
            data[i] += (Byte.toUnsignedInt(a[i * 2])) << 16;
        }
        
        return data;
    }
    
    public static int[] convertFromA8(byte[] a, int width, int height) {
        int[] data = new int[a.length];
        
        for (int i = 0; i < data.length; i++) {
            data[i] += Byte.toUnsignedInt(a[i]) << 24;
            data[i] += 255;
            data[i] += 255 << 8;
            data[i] += 255 << 16;
        }
        
        return data;
    }
    
    public static int[] convertFromL8(byte[] a, int width, int height) {
        int[] data = new int[a.length];
        
        for (int i = 0; i < data.length; i++) {
            data[i] += 255 << 24;
            data[i] += Byte.toUnsignedInt(a[i]);
            data[i] += Byte.toUnsignedInt(a[i]) << 8;
            data[i] += Byte.toUnsignedInt(a[i]) << 16;
        }
        
        return data;
    }
    
    public static int[] convertFromL4(byte[] a, int width, int height) {
        int[] data = new int[a.length * 2];
        
        for (int i = 0; i < a.length; i++) {
            data[i * 2] += 255 << 24;
            data[i * 2] += ETC1Util.extend4To8(a[i] >>> 4);
            data[i * 2] += ETC1Util.extend4To8(a[i] >>> 4) << 8;
            data[i * 2] += ETC1Util.extend4To8(a[i] >>> 4) << 16;
            
            data[i * 2 + 1] += 255 << 24;
            data[i * 2 + 1] += ETC1Util.extend4To8(a[i] & 0xF);
            data[i * 2 + 1] += ETC1Util.extend4To8(a[i] & 0xF) << 8;
            data[i * 2 + 1] += ETC1Util.extend4To8(a[i] & 0xF) << 16;
        }
        
        return data;
    }
    
    public static int[] convertFromA4(byte[] a, int width, int height) {
        int[] data = new int[a.length * 2];
        
        for (int i = 0; i < a.length; i++) {
            data[i * 2] += ETC1Util.extend4To8(a[i] >>> 4) << 24;
            data[i * 2] += 255;
            data[i * 2] += 255 << 8;
            data[i * 2] += 255 << 16;
            
            data[i * 2 + 1] += ETC1Util.extend4To8(a[i] & 0xF) << 24;
            data[i * 2 + 1] += 255;
            data[i * 2 + 1] += 255 << 8;
            data[i * 2 + 1] += 255 << 16;
        }
        
        return data;
    }
}
