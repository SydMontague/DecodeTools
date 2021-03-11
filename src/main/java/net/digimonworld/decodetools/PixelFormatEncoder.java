package net.digimonworld.decodetools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import net.digimonworld.decodetools.core.FileAccess;
import net.digimonworld.decodetools.core.Utils;

public class PixelFormatEncoder {
    private static final String ENCODER_PATH = "./3dstex";
    
    private PixelFormatEncoder() {
    }
    
    public static byte[] convertToRGBA8(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 4];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            buffer.put((byte) ((i >>> 24) & 0xFF));
            buffer.put((byte) ((i) & 0xFF));
            buffer.put((byte) ((i >>> 8) & 0xFF));
            buffer.put((byte) ((i >>> 16) & 0xFF));
        }
        
        return data;
    }
    
    public static byte[] convertToUnknown(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 4];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        
        for (int i : pixels) {
            buffer.put((byte) ((i >>> 24) & 0xFF));
            buffer.put((byte) ((i) & 0xFF));
            buffer.put((byte) ((i >>> 8) & 0xFF));
            buffer.put((byte) ((i >>> 16) & 0xFF));
        }
        
        return data;
    }
    
    public static byte[] convertToRGB8(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 3];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            buffer.put((byte) ((i) & 0xFF));
            buffer.put((byte) ((i >>> 8) & 0xFF));
            buffer.put((byte) ((i >>> 16) & 0xFF));
        }
        
        return data;
    }
    
    public static byte[] convertToRGBA5551(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            short value = 0;
            value |= (i >>> 31) & 0x1;
            value |= ((i >>> 19) & 0x1F) << 11;
            value |= ((i >>> 11) & 0x1F) << 6;
            value |= ((i >>> 3) & 0x1F) << 1;
            buffer.putShort(value);
        }
        
        return data;
    }
    
    public static byte[] convertToRGB565(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            short value = 0;
            value |= ((i >>> 19) & 0x1F) << 11;
            value |= ((i >>> 10) & 0x3F) << 5;
            value |= ((i >>> 3) & 0x1F);
            buffer.putShort(value);
        }
        
        return data;
    }
    
    public static byte[] convertToRGBA4(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            short value = 0;
            value |= (short) ((i >>> 28) & 0xF);
            value |= (short) ((i >>> 20) & 0xF) << 12;
            value |= (short) ((i >>> 12) & 0xF) << 8;
            value |= (short) ((i >>> 4) & 0xF) << 4;
            buffer.putShort(value);
        }
        
        return data;
    }
    
    public static byte[] convertToLA8(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            buffer.put((byte) ((i >>> 24) & 0xFF));
            buffer.put((byte) ((i) & 0xFF));
        }
        
        return data;
    }
    
    public static byte[] convertToLA4(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight()];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            byte value = 0;
            value |= (byte) ((i >>> 28) & 0xF);
            value |= (byte) (i & 0xF0);
            buffer.put(value);
        }
        
        return data;
    }
    
    public static byte[] convertToA8(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight()];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            buffer.put((byte) ((i >>> 24) & 0xFF));
        }
        
        return data;
    }
    
    public static byte[] convertToL8(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight()];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        
        for (int i : pixels) {
            buffer.put((byte) ((i) & 0xFF));
        }
        
        return data;
    }
    
    public static byte[] convertToL4(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() / 2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        for (int i = 0; i < pixels.length / 2; i++) {
            byte value = 0;
            value |= (pixels[i * 2 + 1]) & 0xF0;
            value |= (pixels[i * 2 + 0] >>> 4) & 0xF;
            buffer.put(value);
        }
        
        return data;
    }
    
    public static byte[] convertToA4(BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() / 2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        int[] pixels = Utils.tile(image.getWidth(), image.getHeight(), image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()));
        for (int i = 0; i < pixels.length / 2; i++) {
            byte value = 0;
            value |= pixels[i * 2 + 1] >>> 24 & 0xF0;
            value |= (pixels[i * 2 + 0] >>> 28) & 0xF;
            buffer.put(value);
        }
        
        return data;
    }
    
    public static byte[] convertToETC1(BufferedImage image) {
        try {
            File source = File.createTempFile("etc1Source", null);
            File target = File.createTempFile("etc1Target", null);
            ImageIO.write(image, "PNG", source);
            
            Process process = new ProcessBuilder().command(ENCODER_PATH, "-r", "-o", "etc1", source.getPath(), target.getPath()).start();
            process.waitFor();
            
            try (FileAccess tmp = new FileAccess(target)) {
                return tmp.readByteArray(image.getHeight() * image.getWidth() / 2);
            }
            finally {
                Files.delete(source.toPath());
                Files.delete(target.toPath());
            }
        }
        catch (InterruptedException e) {
            Main.LOGGER.log(Level.SEVERE, "An interrupt happened while execution the process.", e);
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            Main.LOGGER.log(Level.SEVERE, "An IO error occured while converting to ETC1", e);
        }
        
        return new byte[0];
    }
    
    public static byte[] convertToETC1A4(BufferedImage image) {
        try {
            File source = File.createTempFile("etc1Source", null);
            File target = File.createTempFile("etc1Target", null);
            ImageIO.write(image, "PNG", source);
            
            Process process = new ProcessBuilder().command(ENCODER_PATH, "-r", "-o", "etc1a4", source.getPath(), target.getPath()).start();
            process.waitFor();
            
            try (FileAccess tmp = new FileAccess(target)) {
                return tmp.readByteArray(image.getHeight() * image.getWidth());
            }
            finally {
                Files.delete(source.toPath());
                Files.delete(target.toPath());
            }
        }
        catch (InterruptedException e) {
            Main.LOGGER.log(Level.SEVERE, "An interrupt happened while execution the process.", e);
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            Main.LOGGER.log(Level.SEVERE, "An IO error occured while converting to ETC1A4", e);
        }
        
        return new byte[0];
    }
}
