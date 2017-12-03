package de.phoenixstaffel.lostevotools;

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
            case -1:
            default:
                System.out.println("Usage:  java -jar lostevotool.jar <mode> [options...]");
                System.out.println("Modes:  export <nclrPath> <ncgrPath> <nscrPath> <outputPath>");
                System.out.println("        update <nclrPath> <ncgrPath> <nscrPath> <pngPath> <ncgrOutputPath>");
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
