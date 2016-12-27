package de.phoenixstaffel.decodetools.res.payload;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import de.phoenixstaffel.decodetools.ETC1Util;
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
    
    private byte[] pixelData;
    
    // TODO remove all variables that can be deducted from the image
    private Image image;
    
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
        
        pixelData = source.readByteArray((width * height * format.getBPP()) / 8, dataPointer + dataStart);
        
        int[] convertedPixels = format.convertToRGBA(pixelData, width, height);
        convertedPixels = format.isTiled() ? Utils.untile(width, height, convertedPixels) : convertedPixels;
        
        BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        i.setRGB(0, 0, width, height, convertedPixels, 0, width);
        
        image = i;
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
        dest.writeShort(uvSizeX);
        dest.writeShort(uvSizeY);
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        
        dest.writeInteger(unknown5);
        dest.writeShort(width);
        dest.writeShort(height);
        dest.writeInteger(unknown6);
        dest.writeInteger(unknown7);
        
        dest.writeInteger(format.id);
        dest.writeInteger(unknown8);
        dest.writeInteger(unknown9);
        dest.writeInteger(unknown10);
        
        dest.writeByteArray(extraData);
        
        // TODO IMPORTANT: Convert image data into specified PixelFormat
        dataStream.write(pixelData, 0, pixelData.length);
    }
    
    @Override
    public int getAlignment() {
        return getParent().getGenericAlignment();
    }
    
    enum PixelFormat {
        // TODO de- and encoder have to be nicer
        RGBA8(0, 32, true, Utils::convertFromRGBA8),
        RGB8(1, 24, true, Utils::convertFromRGB8),
        RGB5551(2, 16, true, Utils::convertFromRGBA5551),
        RGB565(3, 16, true, Utils::convertFromRGB565),
        RGAB4(4, 16, true, Utils::convertFromRGBA4),
        LA8(5, 16, true, Utils::convertFromLA8),
        HILO8(6, 16, true, null),
        L8(7, 8, true, Utils::convertFromL8),
        A8(8, 8, true, Utils::convertFromA8),
        LA4(9, 8, true, Utils::convertFromLA4),
        L4(10, 4, true, Utils::convertFromL4),
        A4(11, 4, true, Utils::convertFromA4),
        ETC1(12, 4, false, ETC1Util::calculateWithoutAlpha),
        ETC1A4(13, 8, false, ETC1Util::calculateWithAlpha),
        
        UNKNOWN(-1, 32, false, Utils::convertFromRGBA8);
        
        private int id;
        private int bpp;
        private boolean tiled;
        
        private TriFunction<byte[], Integer, Integer, int[]> decoder;
        
        private PixelFormat(int id, int bytes, boolean tiled, TriFunction<byte[], Integer, Integer, int[]> convert) {
            this.id = id;
            this.bpp = bytes;
            this.tiled = tiled;
            this.decoder = convert;
        }
        
        public int[] convertToRGBA(byte[] pixelData, int width, int height) {
            return decoder.apply(pixelData, width, height);
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
    
    public Image getImage() {
        return image;
    }
    
    @Override
    public String toString() {
        return "GMIO " + " " + format + " " + width + " " + height;
    }
}
