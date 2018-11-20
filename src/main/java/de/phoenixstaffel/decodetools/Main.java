package de.phoenixstaffel.decodetools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import de.phoenixstaffel.decodetools.arcv.ARCVFile;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.gui.JLogWindow;
import de.phoenixstaffel.decodetools.gui.MainWindow;

//TODO store settings and preferences
public class Main {
    public static final Logger LOGGER = Logger.getLogger("Decode Tool");
    
    private Main() {
        // no implementation
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length == 3 && "rebuild".equalsIgnoreCase(args[0])) {
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
        else {
            new JLogWindow(LOGGER, Logger.getLogger(Access.class.getName())).setVisible(true);
            new MainWindow().setVisible(true);
        }
    }
}
