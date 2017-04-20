package de.phoenixstaffel.decodetools;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public enum PixelFormat {
    RGBA8(0, 32, true, PixelFormatDecoder::convertFromRGBA8, PixelFormatEncoder::convertToRGBA8),
    RGB8(1, 24, true, PixelFormatDecoder::convertFromRGB8, PixelFormatEncoder::convertToRGB8),
    RGB5551(2, 16, true, PixelFormatDecoder::convertFromRGBA5551, PixelFormatEncoder::convertToRGBA5551),
    RGB565(3, 16, true, PixelFormatDecoder::convertFromRGB565, PixelFormatEncoder::convertToRGB565),
    RGBA4(4, 16, true, PixelFormatDecoder::convertFromRGBA4, PixelFormatEncoder::convertToRGBA4),
    LA8(5, 16, true, PixelFormatDecoder::convertFromLA8, PixelFormatEncoder::convertToLA8),
    //HILO8(6, 16, true, null, null),
    L8(7, 8, true, PixelFormatDecoder::convertFromL8, PixelFormatEncoder::convertToL8),
    A8(8, 8, true, PixelFormatDecoder::convertFromA8, PixelFormatEncoder::convertToA8),
    LA4(9, 8, true, PixelFormatDecoder::convertFromLA4, PixelFormatEncoder::convertToLA4),
    L4(10, 4, true, PixelFormatDecoder::convertFromL4, PixelFormatEncoder::convertToL4),
    A4(11, 4, true, PixelFormatDecoder::convertFromA4, PixelFormatEncoder::convertToA4),
    ETC1(12, 4, false, PixelFormatDecoder::convertFromETC1, PixelFormatEncoder::convertToETC1),
    ETC1A4(13, 8, false, PixelFormatDecoder::convertFromETC1A4, PixelFormatEncoder::convertToETC1A4),
    
    UNKNOWN(-1, 32, false, PixelFormatDecoder::convertFromRGBA8, PixelFormatEncoder::convertToUnknown);
    
    private int id;
    private int bpp;
    private boolean tiled;
    
    private TriFunction<byte[], Integer, Integer, int[]> decoder;
    private Function<BufferedImage, byte[]> encoder;
    
    private PixelFormat(int id, int bytes, boolean tiled, TriFunction<byte[], Integer, Integer, int[]> decoder,
            Function<BufferedImage, byte[]> encoder) {
        this.id = id;
        this.bpp = bytes;
        this.tiled = tiled;
        this.decoder = decoder;
        this.encoder = encoder;
    }
    
    public int getId() {
        return id;
    }
    
    public int[] convertToRGBA(byte[] pixelData, int width, int height) {
        return decoder.apply(pixelData, width, height);
    }
    
    public byte[] convertToFormat(BufferedImage image) {
        //unflip images to make them applicable for the conversion process
        return encoder.apply(Utils.flipImage(image));
    }
    
    public static PixelFormat valueOf(int id) {
        for (PixelFormat f : values())
            if (f.id == id)
                return f;
            
        throw new IllegalArgumentException("Unknown PixelFormat " + id);
    }
    
    public int getBPP() {
        return bpp;
    }
    
    public boolean isTiled() {
        return tiled;
    }
}