package de.phoenixstaffel.lostevotools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.FileAccess;

public class Main {
    private Main() {
    }
    
    public static void main(String[] args) throws IOException {
        GameImage image;
        switch (getMode(args)) {
            case 1:
                image = getGameImage(args[1], args[2], args[3]);
                image.saveMap(new File(args[4]));
                return;
            case 2:
                image = getGameImage(args[1], args[2], args[3]);
                image.setImage(ImageIO.read(new File(args[4])));
                try (Access access = new FileAccess(new File(args[5]))) {
                    image.getTileMap().save(access);
                }
                return;
            case 3:
                image = getGameImage(args[1], args[2], args[3]);
                
                NCGR a = image.getTileMap();
                BufferedImage target = new BufferedImage(a.getWidth() * 16, a.getHeight() * 16, BufferedImage.TYPE_INT_ARGB);
                
                for(int x = 0; x < a.getWidth(); ++x)
                    for(int y = 0; y < a.getHeight(); ++y)
                    {
                        BufferedImage im = a.getTileData(x, y).toImage(image.getPalette().getPalette((short) 0));
                        int[] rgbArray = im.getRGB(0, 0, 8, 8, null, 0, 8);
                        target.setRGB(x * 8, y * 8, 8, 8, rgbArray, 0, 8);
                    }
                
                ImageIO.write(target, "PNG", new File(args[4]));
                return;
            case -1:
            default:
                System.out.println("Usage:  java -jar lostevotool.jar <mode> [options...]");
                System.out.println("Modes:  export <nclrPath> <ncgrPath> <nscrPath> <outputPath>");
                System.out.println("        update <nclrPath> <ncgrPath> <nscrPath> <pngPath> <ncgrOutputPath>");
                System.out.println("        tilemap <nclrPath> <ncgrPath> <nscrPath> <outputPath>");
                return;
        }
    }
    
    private static int getMode(String[] args) {
        if (args.length < 5)
            return -1;
        
        switch (args[0]) {
            case "export":
                return 1;
            case "update":
                return args.length == 6 ? 2 : 0;
            case "tilemap":
                return 3;
            default:
                return -1;
        }
    }
    
    private static GameImage getGameImage(String nclrPath, String ncgrPath, String nscrPath) throws IOException {
        try (FileAccess nclr = new FileAccess(new File(nclrPath));
                FileAccess ncgr = new FileAccess(new File(ncgrPath));
                FileAccess nscr = new FileAccess(new File(nscrPath))) {
            
            return new GameImage(nclr, ncgr, nscr);
        }
    }
}
