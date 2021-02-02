package de.phoenixstaffel.decodetools;

import java.awt.image.BufferedImage;
import java.util.function.Function;

import de.phoenixstaffel.decodetools.core.TriFunction;
import de.phoenixstaffel.decodetools.core.Utils;

public enum PixelFormat {
    RGBA8(0, 32, 0x1001, true, true, PixelFormatDecoder::convertFromRGBA8, PixelFormatEncoder::convertToRGBA8),
    RGB8(1, 24, 0x1002, true, false, PixelFormatDecoder::convertFromRGB8, PixelFormatEncoder::convertToRGB8),
    RGB5551(2, 16, 0x1004, true, true, PixelFormatDecoder::convertFromRGBA5551, PixelFormatEncoder::convertToRGBA5551),
    RGB565(3, 16, 0x1003, true, false, PixelFormatDecoder::convertFromRGB565, PixelFormatEncoder::convertToRGB565),
    RGBA4(4, 16, 0x1005, true, true, PixelFormatDecoder::convertFromRGBA4, PixelFormatEncoder::convertToRGBA4),
    LA8(5, 16, 0x1101, true, true, PixelFormatDecoder::convertFromLA8, PixelFormatEncoder::convertToLA8),
    //HILO8(6, 16, true, null, null),
    L8(7, 8, 0x1103, true, false, PixelFormatDecoder::convertFromL8, PixelFormatEncoder::convertToL8),
    A8(8, 8, 0x1201, true, true, PixelFormatDecoder::convertFromA8, PixelFormatEncoder::convertToA8),
    LA4(9, 8, 0x1102, true, true, PixelFormatDecoder::convertFromLA4, PixelFormatEncoder::convertToLA4),
    L4(10, 4, 0x1104, true, false, PixelFormatDecoder::convertFromL4, PixelFormatEncoder::convertToL4),
    A4(11, 4, 0x1202, true, true, PixelFormatDecoder::convertFromA4, PixelFormatEncoder::convertToA4),
    ETC1(12, 4, 0x3004, false, false, PixelFormatDecoder::convertFromETC1, PixelFormatEncoder::convertToETC1),
    ETC1A4(13, 8, 0x3005, false, true, PixelFormatDecoder::convertFromETC1A4, PixelFormatEncoder::convertToETC1A4),
    
    SHADER(-1, 32, 0x1109, false, false, PixelFormatDecoder::convertFromRGBA8, PixelFormatEncoder::convertToUnknown);
    
    private int id;
    private int bpp;
    private boolean tiled;
    private boolean hasAlpha;
    private short unknown;
    
    private TriFunction<byte[], Integer, Integer, int[]> decoder;
    private Function<BufferedImage, byte[]> encoder;
    
    private PixelFormat(int id, int bitPerPixel, int unknown, boolean tiled, boolean hasAlpha, TriFunction<byte[], Integer, Integer, int[]> decoder,
            Function<BufferedImage, byte[]> encoder) {
        this.id = id;
        this.bpp = bitPerPixel;
        this.unknown = (short) unknown;
        this.tiled = tiled;
        this.hasAlpha = hasAlpha;
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
        return encoder.apply(Utils.mirrorImageVertical(image, true));
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

    public boolean hasAlpha() {
        return hasAlpha;
    }
    
    public short getUnknown() {
        return unknown;
    }
    
    public short getBlockSize() {
        int etc1Factor = this == ETC1 || this == PixelFormat.ETC1A4 ? 16 : 1;
        return (short) (getBPP() * etc1Factor);
    }
}