package de.phoenixstaffel.decodetools.res.payload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.function.Function;

import de.phoenixstaffel.decodetools.PixelFormatDecoder;
import de.phoenixstaffel.decodetools.PixelFormatEncoder;
import de.phoenixstaffel.decodetools.TriFunction;
import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class GMIOFile extends KCAPPayload {
    
    private int magicValue;
    private int version; // ?
    private int unknown1;
    private int dataPointer;
    
    private int unknown2;
    private short uvSizeX; // ?
    private short uvSizeY; // ?
    private int unknown3;
    private int unknown4;
    
    private int unknown5;
    private short width;
    private short height;
    private int unknown6;
    private int unknown7;
    
    private PixelFormat format;
    private int unknown8; // padding?
    private int unknown9; // padding?
    private int unknown10; // additional data length?
    
    private byte[] extraData;
    
    // TODO remove all variables that can be deducted from the image
    private BufferedImage image;
    
    public GMIOFile(Access source, int dataStart, KCAPFile parent, int size) {
        this(source, dataStart, parent);
    }
    
    private GMIOFile(Access source, int dataStart, KCAPFile parent) {
        super(parent);
        
        KCAPPayload p = this;
        while ((p = p.getParent()) != null)
            System.out.print("  ");
        
        System.out.println(Long.toHexString(source.getPosition()) + " GMIO ");
        
        magicValue = source.readInteger();
        version = source.readInteger();
        unknown1 = source.readInteger();
        dataPointer = source.readInteger();
        
        unknown2 = source.readInteger();
        uvSizeX = source.readShort();
        uvSizeY = source.readShort();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
        
        unknown5 = source.readInteger();
        width = source.readShort();
        height = source.readShort();
        unknown6 = source.readInteger();
        unknown7 = source.readInteger();
        
        format = PixelFormat.valueOf(source.readInteger());
        unknown8 = source.readInteger();
        unknown9 = source.readInteger();
        unknown10 = source.readInteger();
        
        extraData = new byte[unknown10];
        
        for (int i = 0; i < unknown10; i++)
            extraData[i] = source.readByte();
        
        byte[] pixelData = source.readByteArray((width * height * format.getBPP()) / 8, dataPointer + dataStart);
        
        int[] convertedPixels = format.convertToRGBA(pixelData, width, height);
        convertedPixels = format.isTiled() ? Utils.untile(width, height, convertedPixels) : convertedPixels;
        
        BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        i.setRGB(0, 0, width, height, convertedPixels, 0, width);

        image = format == PixelFormat.ETC1 || format == PixelFormat.ETC1A4 ? Utils.flipImage(i) : i;
    }
    
    @Override
    public int getSize() {
        return 0x40 + extraData.length;
    }
    
    @Override
    public Payload getType() {
        return Payload.GMIO;
    }
    
    @Override
    public void writeKCAP(Access dest, ByteArrayOutputStream dataStream) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(6); // TODO externalise version magic number
        dest.writeInteger(unknown1);
        dest.writeInteger(dataStream.size());
        
        dest.writeInteger(unknown2);
        dest.writeShort((short) image.getWidth()); //uv width?
        dest.writeShort((short) image.getHeight()); //uv height?
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        
        dest.writeInteger(unknown5);
        dest.writeShort((short) image.getWidth()); //width
        dest.writeShort((short) image.getHeight()); //height
        dest.writeInteger(unknown6);
        dest.writeInteger(unknown7);
        
        dest.writeInteger(format.getId());
        dest.writeInteger(unknown8);
        dest.writeInteger(unknown9);
        dest.writeInteger(unknown10);
        
        dest.writeByteArray(extraData);
        
        byte[] pixelData = format.convertToFormat(image);
        dataStream.write(pixelData, 0, pixelData.length);
    }
    
    @Override
    public int getAlignment() {
        return getParent().getGenericAlignment();
    }
    
    enum PixelFormat {
        RGBA8(0, 32, true, PixelFormatDecoder::convertFromRGBA8, PixelFormatEncoder::convertToRGBA8),
        RGB8(1, 24, true, PixelFormatDecoder::convertFromRGB8, PixelFormatEncoder::convertToRGB8),
        RGB5551(2, 16, true, PixelFormatDecoder::convertFromRGBA5551, PixelFormatEncoder::convertToRGBA5551),
        RGB565(3, 16, true, PixelFormatDecoder::convertFromRGB565, PixelFormatEncoder::convertToRGB565),
        RGBA4(4, 16, true, PixelFormatDecoder::convertFromRGBA4, PixelFormatEncoder::convertToRGBA4),
        LA8(5, 16, true, PixelFormatDecoder::convertFromLA8, PixelFormatEncoder::convertToLA8),
        HILO8(6, 16, true, null, null),
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
        
        private PixelFormat(int id, int bytes, boolean tiled, TriFunction<byte[], Integer, Integer, int[]> decoder, Function<BufferedImage, byte[]> encoder) {
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
            return encoder.apply(this == PixelFormat.ETC1 || this == PixelFormat.ETC1A4 ? Utils.flipImage(image) : image);
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
        
        /*-
         * public enum Bpp
        {
        Etc1A4 = 13,
        BC1 = 14,
        BC2 = 15,
        BC3 = 16,
        BC4L = 17,
        BC4A = 18,
        BC5 = 19,
        Rgba8_SRGB = 20,
        BC1_SRGB = 21,
        BC2_SRGB = 22,
        BC3_SRGB = 23,
        RGB10_A2 = 24
        }
         */
        
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    @Override
    public String toString() {
        return "GMIO " + " " + format + " " + width + " " + height;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
