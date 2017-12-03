package de.phoenixstaffel.decodetools;

import java.io.IOException;
import java.util.logging.Logger;

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
        new JLogWindow(LOGGER, Logger.getLogger(Access.class.getName())).setVisible(true);
        new MainWindow().setVisible(true);
        
        /*- 
           Files.walk(Paths.get("./Input")).filter(a -> a.toString().endsWith("NPC\\digi166.res")).forEach(a -> {
           if (!a.toFile().isFile())
               return;
           try (Access b = new FileAccess(a.toFile())) {
               System.out.println("Opening " + a);
               ResFile f = new ResFile(b);
               f.getRoot().getElementsWithType(Payload.HSEM).forEach(entry ->  {
        
                   try (PrintStream out = new PrintStream("digi166" + ".obj")) {
                       ((HSEMPayload) entry).toObj(out);}
                   catch (FileNotFoundException e) {
                       e.printStackTrace();
                   }
                                                                     
               });
           }
           catch (IOException e) {
               e.printStackTrace();
           }
        });
        */
        /*-
            File f = a.toFile();
            
            if (!f.isFile())
                return;
            
            LOGGER.info(f.getPath());
            
            try (Access b = new FileAccess(f)) {
                ResFile res = new ResFile(b);
                KCAPPayload p = (KCAPPayload) ((KCAPPayload) res.getRoot()).get(7);
                
                int bb = 0;
                for(ResPayload image : p.getElementsWithType(Payload.GMIO)) {
                    GMIOPayload ii = (GMIOPayload) image;
                    ImageIO.write(ii.getImage(), "PNG", new File("Output/Sprites/Color/" + bb++ + ".png"));
                    
                }
        
                //new ResFile(b).repack(new File("outputput/", f.getPath()));// .getRoot().getSize();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        // new ARCVFile(new File("./Input"));
        /*- 
        try(Access source = new FileAccess(new File("./Inputa/ARCVINFO.BIN"))) {
            new VCRAFile(source);//.repack(new File("./Output/ARCVINFO.BIN"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}
