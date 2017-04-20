package de.phoenixstaffel.decodetools.res.payload;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import de.phoenixstaffel.decodetools.PixelFormat;
import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class GMIOPayload extends ResPayload {
    private static final int VERSION = 6;
    
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
    private float uvWidth;
    private float uvHeight;
    private BufferedImage image;
    
    public GMIOPayload(Access source, int dataStart, KCAPPayload parent, int size) {
        this(source, dataStart, parent);
    }
    
    private GMIOPayload(Access source, int dataStart, KCAPPayload parent) {
        super(parent);
        
        source.readInteger(); // magic value
        int version = source.readInteger();
        unknown1 = source.readInteger();
        dataPointer = source.readInteger();
        
        unknown2 = source.readInteger();
        uvSizeX = source.readShort();
        uvSizeY = source.readShort();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
        
        unknown5 = source.readInteger();
        
        if (version == 6) {
            width = source.readShort();
            height = source.readShort();
            unknown6 = source.readInteger();
            unknown7 = source.readInteger();
        }
        else {
            width = uvSizeX;
            height = uvSizeY;
            unknown6 = 0;
            unknown7 = 0;
        }
        
        format = PixelFormat.valueOf(source.readInteger());
        
        unknown8 = source.readInteger();
        unknown9 = source.readInteger();
        unknown10 = source.readInteger();
        
        extraData = source.readByteArray(unknown10);
        
        if (width == 0 || height == 0 || dataPointer == 0xFFFFFFFF)
            return;
        
        uvWidth = (float) uvSizeX / width;
        uvHeight = (float) uvSizeY / height;
        
        byte[] pixelData = source.readByteArray((width * height * format.getBPP()) / 8, (long) dataPointer + dataStart);
        
        int[] convertedPixels = format.convertToRGBA(pixelData, width, height);
        convertedPixels = format.isTiled() ? Utils.untile(width, height, convertedPixels) : convertedPixels;
        
        BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        i.setRGB(0, 0, width, height, convertedPixels, 0, width);
        
        //flip image to make them logical for humans
        if(format != PixelFormat.ETC1 && format != PixelFormat.ETC1A4)
            i = Utils.flipImage(i);
        
        image = i;
    }
    
    @Override
    public int getSize() {
        return Utils.getPadded(0x40 + extraData.length, 4);
    }
    
    @Override
    public Payload getType() {
        return Payload.GMIO;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        int dataAddress = 0xFFFFFFFF;
        if (image != null) {
            byte[] pixelData = format.convertToFormat(image);
            dataAddress = dataStream.add(pixelData, true, getParent());
        }
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(unknown1);
        dest.writeInteger(dataAddress);
        
        dest.writeInteger(unknown2);
        dest.writeShort(image == null ? 0 : (short) (image.getWidth() * uvWidth)); // uv width?
        dest.writeShort(image == null ? 0 : (short) (image.getHeight() * uvHeight)); // uv height?
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        
        dest.writeInteger(unknown5);
        dest.writeShort(image == null ? 0 : (short) image.getWidth()); // width
        dest.writeShort(image == null ? 0 : (short) image.getHeight()); // height
        dest.writeInteger(unknown6);
        dest.writeInteger(unknown7);
        
        dest.writeInteger(format.getId());
        dest.writeInteger(unknown8);
        dest.writeInteger(unknown9);
        dest.writeInteger(unknown10);
        
        dest.writeByteArray(extraData);
    }
    
    @Override
    public int getAlignment() {
        return 4;
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
    
    @Override
    public void fillDummyResData(DummyResData data) {
        if (image == null)
            return;
        
        byte[] pixelData;
        int size;
        
        if (format == PixelFormat.ETC1 || format == PixelFormat.ETC1A4) {
            int[] rgbData = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            ByteBuffer buffer = ByteBuffer.allocate(rgbData.length * 4);
            for (int i : rgbData)
                buffer.putInt(i);
            pixelData = buffer.array();
            size = image.getWidth() * image.getHeight() * format.getBPP() / 8;
        }
        else {
            pixelData = format.convertToFormat(image);
            size = pixelData.length;
        }
        
        data.add(pixelData, size, true, getParent());
    }

    public PixelFormat getFormat() {
        return format;
    }
    
    public void setFormat(PixelFormat format) {
        this.format = format;
    }
}
