package de.phoenixstaffel.lostevotools;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;

public class NSCR {
    private NitroHeader nHeader;
    
    private int magicValueSCRN;
    private int scrnSize;
    private short width;
    private short height;
    private int unknown;
    private int size;
    
    private MapData[] tileIndices;
    
    public NSCR(Access access) {
        nHeader = new NitroHeader(access);
        
        magicValueSCRN = access.readInteger();
        scrnSize = access.readInteger();
        width = access.readShort();
        height = access.readShort();
        unknown = access.readInteger();
        
        size = access.readInteger();
        
        tileIndices = new MapData[(width * height) / 64];
        
        for(int i = 0; i < tileIndices.length; i++)
            tileIndices[i] = new MapData(access.readShort());
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public MapData getMapData(int w, int h) {
        return tileIndices[h * width / 8 + w];
    }
    
    class MapData 
    {
        private short data;
        
        public MapData(short data) {
            this.data = data;
        }
        
        public boolean isFlippedVertically() {
            return Utils.getBitValue(data, 11);
        }
        
        public boolean isFlippedHorizontally() {
            return Utils.getBitValue(data, 10);
        }
        
        public byte getPaletteIndex() {
            return (byte) (data >>> 12);
        }
        
        public short getTileIndex() {
            return (short) Utils.getSubInteger(data, 0, 10);
        }
    }
}
