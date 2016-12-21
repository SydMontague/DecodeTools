package de.phoenixstaffel.decodetools.res;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

public class ResFile {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private KCAPPayload root;
    
    public ResFile(Access source) {
        int dataStart = Utils.getPadded(source.readInteger(0x8), 0x80);
        System.out.println(dataStart);
        root = new KCAPFile(source, dataStart, null);
        
        System.out.println(root.getSize());
        
        //TreeModel model = new DefaultTreeModel(root.getTreeNode());
        //new ExampleFrame(model).setVisible(true);
        
        repack(new File("newRes.res"));
    }
    
    public KCAPPayload getRoot() {
        return root;
    }
    
    public void repack(File file) {
        file.delete();
        if(!file.exists())
            try {
                file.createNewFile();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        
        try (Access dest = new FileAccess(file); ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            root.writeKCAP(dest, data);
            dest.setPosition(Utils.getPadded(root.getSizeOfRoot(), 0x80));
            dest.writeByteArray(data.toByteArray());
        }
        catch (IOException e) {
            log.warning("Exception while writing new .res file.");
            e.printStackTrace();
        }
    }
}
