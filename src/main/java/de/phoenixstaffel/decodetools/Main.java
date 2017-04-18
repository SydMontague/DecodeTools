package de.phoenixstaffel.decodetools;

import java.io.IOException;
import java.util.logging.Logger;

import de.phoenixstaffel.decodetools.gui.MainWindow;
import de.phoenixstaffel.decodetools.gui.JLogWindow;

/*
 * 
 * 
 * 
 */
public class Main {
    public static final Logger LOGGER = Logger.getLogger("Decode Tool");
    
    private Main() {
        // no implementation
    }
    
    public static void main(String[] args) throws IOException {
        new JLogWindow(LOGGER).setVisible(true);
        new MainWindow().setVisible(true);

        /*try (Access b = new FileAccess(new File("./Input/Map/life01.res"))) {
            ResFile f = new ResFile(b);
            f.getRoot().getSize();
            LOGGER.info("Packing");
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
            
            LOGGER.info(f.getPath());
            
            try (Access b = new FileAccess(f)) {
                new ResFile(b);//.getRoot().getSize();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        //new ARCVFile(new File("./Input"));
        
        /*try(Access source = new FileAccess(new File("./Inputa/ARCVINFO.BIN"))) {
            new VCRAFile(source);//.repack(new File("./Output/ARCVINFO.BIN"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
