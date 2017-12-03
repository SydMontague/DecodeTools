package de.phoenixstaffel.decodetools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.phoenixstaffel.decodetools.core.Utils;

public class PixelFormatDecoder {
    /**
     * Static Color offset table as defined in the OpenGL standard. See
     * {@link}https://www.khronos.org/registry/gles/extensions/OES/OES_compressed_ETC1_RGB8_texture.txt}
     */
    private static final int[][] OFFSET_TABLE = new int[][] { 
            new int[] { -8, -2, 2, 8 }, new int[] { -17, -5, 5, 17 }, 
            new int[] { -29, -9, 9, 29 }, new int[] { -42, -13, 13, 42 }, 
            new int[] { -60, -18, 18, 60 }, new int[] { -80, -24, 24, 80 }, 
            new int[] { -106, -33, 33, 106 }, new int[] { -183, -47, 47, 183 } };
    
    private PixelFormatDecoder() {
    }
    
    public static int[] convertFromETC1A4(byte[] input, int width, int height) {
        return calculate(input, width, height, true);
    }
    
    public static int[] convertFromETC1(byte[] input, int width, int height) {
        return calculate(input, width, height, false);
    }
    
    public static int[] convertFromRGBA8(byte[] a, int width, int height) {
        int[] data = new int[a.length / 4];
        for (int i = 0; i < data.length; i++) {
            data[i] += (Byte.toUnsignedInt(a[i * 4]) << 24);
            data[i] += (Byte.toUnsignedInt(a[i * 4 + 1]));
            data[i] += (Byte.toUnsignedInt(a[i * 4 + 2]) << 8);
            data[i] += (Byte.toUnsignedInt(a[i * 4 + 3]) << 16);
        }
        return data;
    }
    
    public static int[] convertFromRGB8(byte[] a, int width, int height) {
        int[] data = new int[a.length / 3];
        for (int i = 0; i < data.length; i++) {
            data[i] += 255 << 24;
            data[i] += (Byte.toUnsignedInt(a[i * 3]));
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
            data[i] += Utils.extend5To8((value >>> 1) & 0x1F);
            data[i] += Utils.extend5To8((value >>> 6) & 0x1F) << 8;
            data[i] += Utils.extend5To8((value >>> 11) & 0x1F) << 16;
        }
        return data;
    }
    
    public static int[] convertFromRGBA4(byte[] a, int width, int height) {
        int[] data = new int[a.length / 2];
        for (int i = 0; i < data.length; i++) {
            int value = (Byte.toUnsignedInt(a[i * 2 + 1]) << 8) + Byte.toUnsignedInt(a[i * 2]);
            data[i] += Utils.extend4To8(value & 0xF) << 24;
            data[i] += Utils.extend4To8((value >>> 4) & 0x1F);
            data[i] += Utils.extend4To8((value >>> 8) & 0xF) << 8;
            data[i] += Utils.extend4To8((value >>> 12) & 0xF) << 16;
        }
        return data;
    }
    
    public static int[] convertFromRGB565(byte[] a, int width, int height) {
        int[] data = new int[a.length / 2];
        for (int i = 0; i < data.length; i++) {
            int value = (Byte.toUnsignedInt(a[i * 2 + 1]) << 8) + Byte.toUnsignedInt(a[i * 2]);
            data[i] += 255 << 24;
            data[i] += Utils.extend5To8(value & 0x1F);
            data[i] += Utils.extend6To8((value & 0x7E0) >>> 5) << 8;
            data[i] += Utils.extend5To8((value & 0xF800) >>> 11) << 16;
        }
        return data;
    }
    
    public static int[] convertFromLA4(byte[] a, int width, int height) {
        int[] data = new int[a.length];
        
        for (int i = 0; i < data.length; i++) {
            data[i] += Utils.extend4To8(a[i] & 0xF) << 24;
            data[i] += Utils.extend4To8((a[i] >>> 4) & 0xF);
            data[i] += Utils.extend4To8((a[i] >>> 4) & 0xF) << 8;
            data[i] += Utils.extend4To8((a[i] >>> 4) & 0xF) << 16;
        }
        
        return data;
    }
    
    public static int[] convertFromLA8(byte[] a, int width, int height) {
        int[] data = new int[a.length / 2];
        
        for (int i = 0; i < data.length; i++) {
            data[i] += (Byte.toUnsignedInt(a[i * 2 + 1])) << 24;
            data[i] += (Byte.toUnsignedInt(a[i * 2 + 0])) << 16;
            data[i] += (Byte.toUnsignedInt(a[i * 2 + 0])) << 8;
            data[i] += (Byte.toUnsignedInt(a[i * 2 + 0]));
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
            data[i * 2 + 1] += 255 << 24;
            data[i * 2 + 1] += Utils.extend4To8(a[i] >>> 4);
            data[i * 2 + 1] += Utils.extend4To8(a[i] >>> 4) << 8;
            data[i * 2 + 1] += Utils.extend4To8(a[i] >>> 4) << 16;
            
            data[i * 2 + 0] += 255 << 24;
            data[i * 2 + 0] += Utils.extend4To8(a[i] & 0xF);
            data[i * 2 + 0] += Utils.extend4To8(a[i] & 0xF) << 8;
            data[i * 2 + 0] += Utils.extend4To8(a[i] & 0xF) << 16;
        }
        
        return data;
    }
    
    public static int[] convertFromA4(byte[] a, int width, int height) {
        int[] data = new int[a.length * 2];
        
        for (int i = 0; i < a.length; i++) {
            data[i * 2 + 1] += Utils.extend4To8(a[i] >>> 4) << 24;
            data[i * 2 + 1] += 255;
            data[i * 2 + 1] += 255 << 8;
            data[i * 2 + 1] += 255 << 16;
            
            data[i * 2 + 0] += Utils.extend4To8(a[i] & 0xF) << 24;
            data[i * 2 + 0] += 255;
            data[i * 2 + 0] += 255 << 8;
            data[i * 2 + 0] += 255 << 16;
        }
        
        return data;
    }
    
    private static int buildRGBA8(long r, long g, long b, long a) {
        int data = 0;
        data += (a << 24);
        data += (r << 16);
        data += (g << 8);
        data += b;
        
        return data & 0xFFFFFFFF;
    }
    
    private static int[] calculate(byte[] input, int width, int height, boolean alpha) {
        int[] output = new int[(input.length / 8) * 16];
        
        ByteBuffer inbuff = ByteBuffer.wrap(input);
        inbuff.order(ByteOrder.LITTLE_ENDIAN);
        
        int x = 0;
        int y = 0;
        while (inbuff.hasRemaining()) {
            int[][] value00 = getPixelsForBlock(alpha ? inbuff.getLong() : 0xFFFFFFFFFFFFFFFFL, inbuff.getLong());
            int[][] value10 = getPixelsForBlock(alpha ? inbuff.getLong() : 0xFFFFFFFFFFFFFFFFL, inbuff.getLong());
            int[][] value01 = getPixelsForBlock(alpha ? inbuff.getLong() : 0xFFFFFFFFFFFFFFFFL, inbuff.getLong());
            int[][] value11 = getPixelsForBlock(alpha ? inbuff.getLong() : 0xFFFFFFFFFFFFFFFFL, inbuff.getLong());
            
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    output[(y + 0) * 4 * width + (x + 0) * 4 + j * width + i] = value00[i][j];
                    output[(y + 1) * 4 * width + (x + 0) * 4 + j * width + i] = value01[i][j];
                    output[(y + 0) * 4 * width + (x + 1) * 4 + j * width + i] = value10[i][j];
                    output[(y + 1) * 4 * width + (x + 1) * 4 + j * width + i] = value11[i][j];
                }
            
            x += 2;
            if (x * 4 >= width) {
                x = 0;
                y += 2;
            }
        }
        
        return output;
    }
    
    private static int[][] getPixelsForBlock(long alpha, long value) {
        int[][] data = new int[4][4];
        
        boolean isDifferential = Utils.getBitValue(value, 33);
        boolean isFlip = Utils.getBitValue(value, 32);
        
        int tableCW2 = (int) Utils.getSubInteger(value, 34, 3);
        int tableCW1 = (int) Utils.getSubInteger(value, 37, 3);
        
        long r1;
        long g1;
        long b1;
        
        long r2;
        long g2;
        long b2;
        
        if (isDifferential) {
            long baseR = Utils.getSubInteger(value, 59, 5);
            long baseG = Utils.getSubInteger(value, 51, 5);
            long baseB = Utils.getSubInteger(value, 43, 5);
            
            long diffR = Utils.getSubInteger(value, 56, 3);
            long diffG = Utils.getSubInteger(value, 48, 3);
            long diffB = Utils.getSubInteger(value, 40, 3);
            
            r1 = Utils.extend5To8(baseR);
            g1 = Utils.extend5To8(baseG);
            b1 = Utils.extend5To8(baseB);
            
            r2 = Utils.extend5To8(Utils.add3BitSigned(baseR, diffR));
            g2 = Utils.extend5To8(Utils.add3BitSigned(baseG, diffG));
            b2 = Utils.extend5To8(Utils.add3BitSigned(baseB, diffB));
        }
        else {
            r1 = Utils.extend4To8(Utils.getSubInteger(value, 60, 4));
            g1 = Utils.extend4To8(Utils.getSubInteger(value, 52, 4));
            b1 = Utils.extend4To8(Utils.getSubInteger(value, 44, 4));
            
            r2 = Utils.extend4To8(Utils.getSubInteger(value, 56, 4));
            g2 = Utils.extend4To8(Utils.getSubInteger(value, 48, 4));
            b2 = Utils.extend4To8(Utils.getSubInteger(value, 40, 4));
        }
        
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++) {
                int id = x * 4 + y;
                
                boolean msb = Utils.getBitValue(value, 16 + id);
                boolean lsb = Utils.getBitValue(value, id);
                int offset = (!msb ? 0b10 : 0) + ((lsb ^ msb) ? 0b1 : 0);
                long a = Utils.extend4To8(Utils.getSubInteger(alpha, id * 4, 4));
                
                long r;
                long g;
                long b;
                if (isFlip && y >= 2 || !isFlip && x >= 2) {
                    r = Utils.crop(r2 + OFFSET_TABLE[tableCW2][offset], 0, 255);
                    g = Utils.crop(g2 + OFFSET_TABLE[tableCW2][offset], 0, 255);
                    b = Utils.crop(b2 + OFFSET_TABLE[tableCW2][offset], 0, 255);
                }
                else {
                    r = Utils.crop(r1 + OFFSET_TABLE[tableCW1][offset], 0, 255);
                    g = Utils.crop(g1 + OFFSET_TABLE[tableCW1][offset], 0, 255);
                    b = Utils.crop(b1 + OFFSET_TABLE[tableCW1][offset], 0, 255);
                }
                
                data[x][y] = buildRGBA8(r, g, b, a);
            }
        
        return data;
    }
    
}
