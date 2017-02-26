package de.phoenixstaffel.decodetools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import de.phoenixstaffel.decodetools.arcv.ARCVFile;
import de.phoenixstaffel.decodetools.arcv.VCRAFile;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.gui.ExampleFrame;
import de.phoenixstaffel.decodetools.res.ResFile;

/*
 * 
 * 
 * 
 */
public class Main {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private Main() {
        // no implementation
    }
    
    public static void main(String[] args) throws IOException {
        new ExampleFrame().setVisible(true);
        
        /*try (Access b = new FileAccess(new File("./Input/Map/life01.res"))) {
            ResFile f = new ResFile(b);
            f.getRoot().getSize();
            System.out.println("Packing");
            f.repack(new File("Output/life01.res"));
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }/*/
        /*
        Files.walk(Paths.get("./Input")).forEach(a -> {
            File f = a.toFile();
            
            if(!f.isFile())
                return;
            
            System.out.println(f.getPath());
            
            try (Access b = new FileAccess(f)) {
                new ResFile(b).getRoot().getSize();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });*/
        //new ARCVFile(new File("./Input"));
        
        /*try(Access source = new FileAccess(new File("./Inputa/ARCVINFO.BIN"))) {
            new VCRAFile(source);//.repack(new File("./Output/ARCVINFO.BIN"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
