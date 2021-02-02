package de.phoenixstaffel.decodetools.res.payload;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.PixelFormat;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.NameablePayload;
import de.phoenixstaffel.decodetools.res.ResData;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class GMIOPayload extends NameablePayload {
    private static final int VERSION = 6;
    
    // magic value (4 byte)
    // version (4 byte)
    // always 0x3001 (2 byte)
    private byte dataId; // data ID? Based on GMIP head, can overflow
    private byte unknown1;
    // dataPointer (4 byte)
    
    // hasAlpha (4 byte)
    // uv width (2 byte)
    // uv height (2 byte)
    // always 0x0001 (2 byte)
    // depends on format (2 byte)
    // for $toon$ and $phong$ files (1 byte)
    // always 0x01 (byte)
    // bits per block (2 byte)
    
    private byte unknown2;
    private byte unknown3;
    private short unknown4;
    // width (2 byte)
    // height (2 byte)
    // always 0x01 (4 byte)
    // always 0x00 (4 byte)
    
    private PixelFormat format;
    // always 0x00 (4 byte)
    // hasExtraData (4 byte)
    // extraDataSize (4 byte)
    
    private byte[] extraData;
    
    // helper members
    private float uvWidth;
    private float uvHeight;
    private BufferedImage image;
    
    /**
     * Creates an empty GMIO
     * @param parent the parent KCAP
     */
    public GMIOPayload(AbstractKCAP parent) {
        super(parent, null);
        
        this.dataId = 0;
        this.unknown1 = 0;
        this.unknown2 = 0x22;
        this.unknown3 = 0x02;
        this.unknown4 = 0x22;
        
        this.format = PixelFormat.RGBA8;
        this.extraData = new byte[0];
        
        this.uvHeight = 1f;
        this.uvWidth = 1f;
        this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
    
    public GMIOPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        this(source, dataStart, parent, name);
    }
    
    private GMIOPayload(Access source, int dataStart, AbstractKCAP parent, String name) {
        super(parent, name);
        
        source.readInteger(); // magic value
        int version = source.readInteger();
        source.readShort(); // always 0x3001
        dataId = source.readByte();
        unknown1 = source.readByte();
        int dataPointer = source.readInteger();
        
        source.readInteger(); // hasAlpha
        short uvSizeX = source.readShort();
        short uvSizeY = source.readShort();
        source.readShort(); // always 1
        source.readShort();
        source.readByte();
        source.readByte(); // always 1
        source.readShort();

        unknown2 = source.readByte();
        unknown3 = source.readByte();
        unknown4 = source.readShort();
        short width = version == VERSION ? source.readShort() : uvSizeX;
        short height = version == VERSION ? source.readShort() : uvSizeY;
        if(version == VERSION) {
            source.readInteger(); // always 1
            source.readInteger(); // always 0
        }
        format = PixelFormat.valueOf(source.readInteger());
        
        source.readInteger(); // always 0
        boolean hasExtraData = source.readInteger() == 1;
        int extraDataLength = source.readInteger();
        
        extraData = hasExtraData ? source.readByteArray(extraDataLength) : new byte[0];
        
        if (width == 0 || height == 0 || dataPointer == 0xFFFFFFFF)
            return;
        
        uvWidth = (float) uvSizeX / width;
        uvHeight = (float) uvSizeY / height;
        
        byte[] pixelData = source.readByteArray((width * height * format.getBPP()) / 8, (long) dataPointer + dataStart);
        
        if (format != PixelFormat.SHADER && (width < 4 || height < 4))
            Main.LOGGER.severe("Found image with width or height smaller than 4. Those are not supported.");
        else {
            int[] convertedPixels = format.convertToRGBA(pixelData, width, height);
            convertedPixels = format.isTiled() ? Utils.untile(width, height, convertedPixels) : convertedPixels;
            
            BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            i.setRGB(0, 0, width, height, convertedPixels, 0, width);
            
            // flip image to make them logical for humans
            if (format != PixelFormat.ETC1 && format != PixelFormat.ETC1A4)
                i = Utils.mirrorImageVertical(i);
            
            image = i;
        }
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
    
    public boolean setImage(BufferedImage image) {
        if (!isValidResolution(image, format)) {
            Main.LOGGER.severe(() -> String.format("Tried to import image with resolution %dx%d, which is not a power of 4 or smaller than 4.", image.getWidth(), image.getHeight()));
            return false;
        }
        
        this.image = image;
        return true;
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
    public void writeKCAP(Access dest, ResData dataStream) {
        int dataAddress = 0xFFFFFFFF;
        if (image != null) {
            if (!isValidResolution(image, format))
                Main.LOGGER.severe(() -> String.format("Saving image %s with illegal resolution: %dx%d | This file will cause problems!",
                                                       getName(),
                                                       image.getWidth(),
                                                       image.getHeight()));
            
            byte[] pixelData = format.convertToFormat(image);
            dataAddress = dataStream.add(pixelData, hasName());
        }
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeShort((short) 0x3001);
        dest.writeByte(dataId);
        dest.writeByte(unknown1);
        dest.writeInteger(dataAddress);
        
        dest.writeInteger(format.hasAlpha() ? 2 : 0);
        dest.writeShort(image == null ? 0 : (short) (image.getWidth() * uvWidth)); // uv width?
        dest.writeShort(image == null ? 0 : (short) (image.getHeight() * uvHeight)); // uv height?
        dest.writeShort((short) 1); // always 1
        dest.writeShort(format.getUnknown());
        dest.writeByte((byte) (isShaderTexture() ? 1 : 0));
        dest.writeByte((byte) 1);
        dest.writeShort(format.getBlockSize());
        
        dest.writeByte(unknown2);
        dest.writeByte(unknown3);
        dest.writeShort(unknown4);
        dest.writeShort(image == null ? 0 : (short) image.getWidth()); // width
        dest.writeShort(image == null ? 0 : (short) image.getHeight()); // height
        dest.writeInteger(1);
        dest.writeInteger(0);
        
        dest.writeInteger(format.getId());
        dest.writeInteger(0);
        dest.writeInteger(extraData.length > 0 ? 1 : 0);
        dest.writeInteger(extraData.length);
        
        dest.writeByteArray(extraData);
    }
    
    public boolean isShaderTexture() {
        return hasName() && getName().startsWith("$");
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
        
        data.add(pixelData, size, hasName());
    }
    
    @Override
    public String toString() {
        return hasName() ? getName() : "GMIO " + " " + format + " " + getWidth() + " " + getHeight();
    }
    
    public int getWidth() {
        return image != null ? image.getWidth() : 0;
    }
    
    public int getHeight() {
        return image != null ? image.getHeight() : 0;
    }
    
    private static boolean isValidResolution(BufferedImage image, PixelFormat format) {
        int width = image.getWidth();
        int height = image.getHeight();

        if(format == PixelFormat.SHADER && width == 256 && height == 1)
            return true;
        
        if(!Utils.isPowOf2(image.getWidth()) || !Utils.isPowOf2(image.getHeight()))
            return false;
        
        return width >= 4 && height >= 4;
    }
}
