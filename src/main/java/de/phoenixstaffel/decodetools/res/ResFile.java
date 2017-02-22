package de.phoenixstaffel.decodetools.res;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;

public class ResFile {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private KCAPPayload root;
    
    public ResFile(Access source) {
        int dataStart = KCAPPayload.Payload.valueOf(null, source.readInteger(0)).getDataStart(source);
        root = KCAPPayload.craft(source, dataStart, null, -1);
    }
    
    public KCAPPayload getRoot() {
        return root;
    }
    
    public void repack(File file) {
        file.delete();
        if (!file.exists())
            try {
                file.createNewFile();
            }
            catch (IOException e1) {
                log.log(Level.WARNING, "Exception while writing new .res file.", e1);
            }
        
        try (Access dest = new FileAccess(file); ResData data = new ResData()) {
            root.writeKCAP(dest, data);
            dest.setPosition(Utils.getPadded(root.getSizeOfRoot(), 0x80));
            dest.writeByteArray(data.getStream().toByteArray());
        }
        catch (IOException e) {
            log.log(Level.WARNING, "Exception while writing new .res file.", e);
        }
    }
}
