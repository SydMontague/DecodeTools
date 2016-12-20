package de.phoenixstaffel.decodetools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.ResFile;

public class Main {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private Main() {
        // no implementation
    }
    
    public static void main(String[] args) throws IOException {
        new ResFile(new FileAccess(new File("Input/digi103.res")));
    }
}
