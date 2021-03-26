package net.digimonworld.decodetools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import net.digimonworld.decodetools.arcv.ARCVFile;
import net.digimonworld.decodetools.arcv.VCRAFile;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.FileAccess;
import net.digimonworld.decodetools.gui.JLogWindow;
import net.digimonworld.decodetools.gui.MainWindow;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.ResPayload.Payload;

//TODO store settings and preferences
public class Main {
    public static final Logger LOGGER = Logger.getLogger("Decode Tool");
    
    private Main() {
        // no implementation
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
            if ("rebuild".equalsIgnoreCase(args[0])) {
                File input = new File(args[1]);
                File output = new File(args[2]);
                
                if (!input.isDirectory()) {
                    LOGGER.severe("The input value must be a directory.");
                    return;
                }
                
                if (!output.isDirectory())
                    output.mkdirs();
                
                ARCVFile arcv = new ARCVFile(input, true);
                arcv.saveFiles(output);
            }
            else if ("unpack".equalsIgnoreCase(args[0])) {
                File input = new File(args[1]); // folder with ARCVINFO.BIN and ARCV0.BIN
                File output = new File(args[2]); // folder to extract into
                
                if (!input.isDirectory()) {
                    LOGGER.severe("The input value must be a directory.");
                    return;
                }
                
                if (output.isFile()) {
                    LOGGER.severe("The out value must be a directory.");
                    return;
                }
                
                try (Access access = new FileAccess(new File(input, "ARCVINFO.BIN"))) {
                    VCRAFile info = new VCRAFile(access);
                    info.extractARCV(new File(input, "ARCV0.BIN").toPath(), output.toPath());
                }
            }
        }
        else {
            new JLogWindow(LOGGER, Logger.getLogger(Access.class.getName())).setVisible(true);
            new MainWindow().setVisible(true);
        }
        
        Files.walk(Paths.get("./Input/map/tow02.res")).forEach(a -> {
            if (!Files.isRegularFile(a))
                return;
            
            try (FileAccess access = new FileAccess(a.toFile(), true)) {
                if (!ResPayload.craft(access).getElementsWithType(Payload.PADH).isEmpty())
                    System.out.println(a + " has PADH");
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        
        System.out.println("done");
    }
}
