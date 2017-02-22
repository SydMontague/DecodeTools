package de.phoenixstaffel.lostevotools;

import de.phoenixstaffel.decodetools.PixelFormatDecoder;
import de.phoenixstaffel.decodetools.dataminer.Access;

/*
 * RLCN
 * 0x0100FEFF
 * fileSize
 * 0x00020010
 * 
 * TTLP
 * size
 * 0x3
 * 0x0
 * color size
 * 0x10
 * 
 * palettes * 0x10 * RGB555
 * 
 * PMCP
 * 0x1A
 * 0xBEEF0005
 * 0x8
 * 0x10000
 */
public class NCLR {
    private NitroHeader nHeader;
    
    private int magicValuePLTT;
    private int plttSize;
    private ColorDepth depth;
    private short unknown3;
    private int unknown4;
    private int plttDataSize;
    private int unknown;
    
    private PaletteData[] colors;
    
    private int magicValuePCMP;
    private int pcmpSize;
    private short paletteCount;
    private short beef; // always 0xBEEF?
    private int unknown5; // always 0x8?
    private short[] paletteOrder;
    
    public NCLR(Access access) {
        nHeader = new NitroHeader(access);
        
        magicValuePLTT = access.readInteger();
        plttSize = access.readInteger();
        depth = ColorDepth.valueOf(access.readShort());
        
        unknown3 = access.readShort();
        unknown4 = access.readInteger();
        plttDataSize = access.readInteger();
        unknown = access.readInteger();
        
        this.colors = new PaletteData[plttDataSize / depth.getNumColors() / 2];
        for (int j = 0; j < plttDataSize / depth.getNumColors() / 2; j++) {
            short[] lColors = new short[depth.getNumColors()];
            
            for (int i = 0; i < depth.getNumColors(); i++)
                lColors[i] = access.readShort();
            
            this.colors[j] = new PaletteData(lColors);
        }
        
        magicValuePCMP = access.readInteger();
        pcmpSize = access.readInteger();
        paletteCount = access.readShort();
        beef = access.readShort();
        unknown5 = access.readInteger();
    }
    
    class PaletteData {
        private PaletteColor[] colors;
        
        public PaletteData(short[] colors) {
            this.colors = new PaletteColor[colors.length];
            
            for (int i = 0; i < colors.length; i++) {
                this.colors[i] = new PaletteColor(colors[i]);
            }
        }
        
        public PaletteColor getColor(byte index) {
            return colors[index];
        }
        
        public byte getIndex(int rgb, byte original) {
            byte red = (byte) (PixelFormatDecoder.getSubInteger(rgb, 0, 8) >>> 3);
            byte green = (byte) (PixelFormatDecoder.getSubInteger(rgb, 8, 8) >>> 3);
            byte blue = (byte) (PixelFormatDecoder.getSubInteger(rgb, 16, 8) >>> 3);
            
            short delta = 1024;
            byte tmp = 0;
            short difference;
            
            for (byte i = 0; i < colors.length; i++) {
                PaletteColor color = colors[i];
                
                difference = (short) Math.abs(red - color.red);
                difference += Math.abs(green - color.green);
                difference += Math.abs(blue - color.blue);
                
                if(difference == 0 && i == original)
                    return i;
                
                if(difference < delta) {
                    tmp = i;
                    delta = difference;
                }
            }
            
            return tmp;
        }
    }
    
    class PaletteColor {
        private byte red;
        private byte green;
        private byte blue;
        
        public PaletteColor(short color) {
            this.red = (byte) ((color >>> 10) & 0x1F);
            this.green = (byte) ((color >>> 5) & 0x1F);
            this.blue = (byte) ((color) & 0x1F);
        }
        
        public int toRGB8() {
            int color = 0;
            color += (PixelFormatDecoder.extend5To8(red));
            color += (PixelFormatDecoder.extend5To8(green) << 8);
            color += (PixelFormatDecoder.extend5To8(blue) << 16);
            
            return color;
        }
    }
    
    public PaletteColor getColor(byte index) {
        return colors[0].getColor(index);
    }
    
    enum ColorDepth {
        FOUR_BPP(3, 16),
        EIGHT_BPP(4, 256);
        
        private int id;
        private int numColors;
        
        private ColorDepth(int id, int numColors) {
            this.id = id;
            this.numColors = numColors;
        }
        
        public static ColorDepth valueOf(int id) {
            for (ColorDepth d : values())
                if (d.id == id)
                    return d;
                
            throw new IllegalArgumentException("ID not known");
        }
        
        public int getNumColors() {
            return numColors;
        }
        
    }
    
    public PaletteData getPalette(short palette) {
        return colors[palette];
    }
}
