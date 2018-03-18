package de.phoenixstaffel.lostevotools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.lostevotools.NCGR.ITileData;
import de.phoenixstaffel.lostevotools.NCLR.PaletteData;
import de.phoenixstaffel.lostevotools.NSCR.MapData;

public class GameImage {
    private NCLR palette;
    private NCGR tileMap;
    private NSCR map;
    
    public GameImage(Access nclr, Access ncgr, Access nscr) {
        this.palette = new NCLR(nclr);
        this.tileMap = new NCGR(ncgr);
        this.map = new NSCR(nscr);
    }
    
    public void saveTileMap(File output) {
        BufferedImage image = new BufferedImage(tileMap.getWidth() * 8, tileMap.getHeight() * 8,
                BufferedImage.TYPE_USHORT_555_RGB);
        
        for (int h = 0; h < tileMap.getHeight(); h++) {
            for (int w = 0; w < tileMap.getWidth(); w++) {
                ITileData data = tileMap.getTileData(w, h);
                
                BufferedImage im = data.toImage(palette.getPalette((short) 0));
                int[] rgbArray = im.getRGB(0, 0, 8, 8, null, 0, 8);
                image.setRGB(w * 8, h * 8, 8, 8, rgbArray, 0, 8);
            }
        }
        
        try {
            ImageIO.write(image, "PNG", output);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public void saveMap(File output) {
        BufferedImage image = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_USHORT_555_RGB);
        
        for (int h = 0; h < map.getHeight() / 8; h++) {
            for (int w = 0; w < map.getWidth() / 8; w++) {
                MapData data = map.getMapData(w, h);
                BufferedImage tile = tileMap.getTileData(data.getTileIndex()).toImage(this.palette.getPalette(data.getPaletteIndex()));
                
                if (data.isFlippedVertically())
                    tile = Utils.mirrorImageVertical(tile);
                if (data.isFlippedHorizontally())
                    tile = Utils.mirrorImageHorizontal(tile);
                
                int[] rgbArray = tile.getRGB(0, 0, 8, 8, null, 0, 8);
                image.setRGB(w * 8, h * 8, 8, 8, rgbArray, 0, 8);
            }
        }
        
        try {
            ImageIO.write(image, "PNG", output);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public NCGR getTileMap() {
        return tileMap;
    }
    
    public NSCR getMap() {
        return map;
    }
    
    public NCLR getPalette() {
        return palette;
    }
    
    public void setImage(BufferedImage image) {
        for (int w = 0; w < image.getWidth() / 8; w++)
            for (int h = 0; h < image.getHeight() / 8; h++) {
                MapData mapData = getMap().getMapData(w, h);
                ITileData tileData = getTileMap().getTileData(mapData.getTileIndex());
                PaletteData paletteData = getPalette().getPalette(mapData.getPaletteIndex());
                
                int[] data = new int[64];
                BufferedImage i = image.getSubimage(w * 8, h * 8, 8, 8);
                
                if (mapData.isFlippedVertically())
                    i = Utils.mirrorImageVertical(i);
                if (mapData.isFlippedHorizontally())
                    i = Utils.mirrorImageHorizontal(i);
                
                for (int x = 0; x < 8; x++)
                    for (int y = 0; y < 8; y++) {
                        data[y * 8 + x] = paletteData.getIndex(i.getRGB(x, y), (byte) tileData.getIndex(x, y));
                    }
                
                tileData.setData(data);
            }
        
    }
}
