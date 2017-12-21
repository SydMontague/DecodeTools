package de.phoenixstaffel.decodetools.res.payload;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.PixelFormat;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class GMIOPayload extends ResPayload {
    private static final int VERSION = 6;
    
    private String name;
    
    private short unknown1;
    private byte unknown1_1;
    private byte unknown1_2;
    private int dataPointer;
    
    private int unknown2;
    // uv height and width (4 byte)
    private int unknown3;
    private int unknown4;
    
    private int unknown5;
    // width (2 byte)
    // height (2 byte)
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
    
    public GMIOPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        this(source, dataStart, parent, name);
    }
    
    private GMIOPayload(Access source, int dataStart, KCAPPayload parent, String name) {
        super(parent);
        
        this.name = name;
        
        source.readInteger(); // magic value
        int version = source.readInteger();
        unknown1 = source.readShort(); // always 0x3001
        unknown1_1 = source.readByte();
        unknown1_2 = source.readByte();
        dataPointer = source.readInteger();
        
        unknown2 = source.readInteger();
        short uvSizeX = source.readShort();
        short uvSizeY = source.readShort();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
        
        unknown5 = source.readInteger();
        short width = version == VERSION ? source.readShort() : uvSizeX;
        short height = version == VERSION ? source.readShort() : uvSizeY;
        unknown6 = version == VERSION ? source.readInteger() : 0;
        unknown7 = version == VERSION ? source.readInteger() : 0;
        
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
        
        // flip image to make them logical for humans
        if (format != PixelFormat.ETC1 && format != PixelFormat.ETC1A4)
            i = Utils.flipImage(i);
        
        image = i;
    }
    
    public double getUVHeight() {
        return uvHeight;
    }
    
    public int getUVHeightAbsolute() {
        return (int) Math.round(getUVHeight() * getHeight());
    }
    
    public double getUVWidth() {
        return uvWidth;
    }
    
    public int getUVWidthAbsolute() {
        return (int) Math.round(getUVWidth() * getWidth());
    }
    
    public void setUVWidth(float uvWidth) {
        this.uvWidth = uvWidth;
    }
    
    public void setUVWidthAbsolute(int uvWidth) {
        this.uvWidth = (float) uvWidth / getWidth();
    }
    
    public void setUVHeight(float uvHeight) {
        this.uvHeight = uvHeight;
    }
    
    public void setUVHeightAbsolute(int uvHeight) {
        this.uvHeight = (float) uvHeight / getHeight();
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void setImage(BufferedImage image) {
        if (!Utils.isPowOf2(image.getWidth()) || !Utils.isPowOf2(image.getHeight()))
            Main.LOGGER.warning(() -> "Given image resolution are not powers of two, but they are required to be. \n" + "Resolution: " + image.getWidth() + "x"
                    + image.getHeight() + " | Exporting this file will cause problems!");
        
        this.image = image;
    }
    
    public PixelFormat getFormat() {
        return format;
    }
    
    public void setFormat(PixelFormat format) {
        this.format = format;
    }
    
    @Override
    public int getSize() {
        return Utils.align(0x40 + extraData.length, 4);
    }
    
    @Override
    public Payload getType() {
        return Payload.GMIO;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        int dataAddress = 0xFFFFFFFF;
        if (image != null) {
            if (!Utils.isPowOf2(image.getWidth()) || !Utils.isPowOf2(image.getHeight()))
                Main.LOGGER.warning(() -> "Saving image " + name + " with illegal resolution. \n" + "Resolution: " + image.getWidth() + "x" + image.getHeight()
                        + " | This file will cause problems!");
        
            byte[] pixelData = format.convertToFormat(image);
            dataAddress = dataStream.add(pixelData, name != null, getParent());
        }
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeShort(unknown1);
        dest.writeByte(unknown1_1);
        dest.writeByte(unknown1_2);
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
        
        data.add(pixelData, size, name != null, getParent());
    }
    
    @Override
    public int getAlignment() {
        return 4;
    }
    
    @Override
    public String toString() {
        return name != null ? name : "GMIO " + " " + format + " " + getWidth() + " " + getHeight();
    }

    public int getWidth() {
        return image != null ? image.getWidth() : 0;
    }
    
    public int getHeight() {
        return image != null ? image.getHeight() : 0;
    }
}
