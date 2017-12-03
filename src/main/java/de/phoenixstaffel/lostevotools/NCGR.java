package de.phoenixstaffel.lostevotools;

import java.awt.image.BufferedImage;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.lostevotools.NCLR.PaletteData;

public class NCGR {
    private NitroHeader nHeader;
    
    private int magicValueCHAR;
    private int charSize;
    private short height;
    private short width;
    private int depth;
    
    private int unknown2;
    private int unknown3;
    private int tileSize;
    private int unknown4;
    
    private TileData[][] tileData;
    
    private int magicValueCPOS;
    private int unknown5;
    private int unknown6;
    private short width2;
    private short height2;
    
    public NCGR(Access access) {
        nHeader = new NitroHeader(access);
        
        magicValueCHAR = access.readInteger();
        charSize = access.readInteger();
        height = access.readShort();
        width = access.readShort();
        depth = access.readInteger();
        unknown2 = access.readInteger();
        unknown3 = access.readInteger();
        tileSize = access.readInteger();
        unknown4 = access.readInteger();
        
        this.tileData = new TileData[width][height];
        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                tileData[i][j] = new TileData(access.readByteArray(64 * 4 / 8));
            }
        
        magicValueCPOS = access.readInteger();
        unknown5 = access.readInteger();
        unknown6 = access.readInteger();
        width2 = access.readShort();
        height2 = access.readShort();
    }
    
    public boolean save(Access dest) {
        nHeader.save(dest);
        
        dest.writeInteger(magicValueCHAR);
        dest.writeInteger(charSize);
        dest.writeShort(height);
        dest.writeShort(width);
        dest.writeInteger(depth);
        dest.writeInteger(unknown2);
        dest.writeInteger(unknown3);
        dest.writeInteger(tileSize);
        dest.writeInteger(unknown4);

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                tileData[i][j].save(dest);
            }
        
        dest.writeInteger(magicValueCPOS);
        dest.writeInteger(unknown5);
        dest.writeInteger(unknown6);
        dest.writeShort(width2);
        dest.writeShort(height2);
        
        return true;
    }
    
    class TileData {
        byte[] data;
        
        public TileData(byte[] data) {
            this.data = new byte[data.length * 2];
            
            int i = 0;
            for(byte d : data) {
                this.data[i++] = (byte) (d & 0xF);
                this.data[i++] = (byte) ((d >>> 4) & 0xF);
            }
        }

        public void save(Access dest) {
            byte[] data2 = new byte[data.length / 2];
            
            for(int i = 0; i < data.length; i += 2)
            {
                data2[i / 2] = (byte) (data[i] + (data[i+1] << 4));
            }
            
            dest.writeByteArray(data2);
        }

        public byte getIndex(int i, int j) {
            return data[j * 8 + i];
        }

        public BufferedImage toImage(PaletteData palette) {
            BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_USHORT_555_RGB);
            
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++)
                    image.setRGB(i, j, palette.getColor(getIndex(i, j)).toRGB8());

            return image;
        }

        public void setData(byte[] data2) {
            this.data = data2;
        }
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public TileData getTileData(int w, int h) {
        return tileData[w][h];
    }

    public TileData getTileData(short index) {
        int x = index % width;
        int y = index / width;

        return tileData[x][y];
    }
}
