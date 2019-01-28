package de.phoenixstaffel.decodetools.res;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class ResFile {
    private ResPayload root;
    
    public ResFile(Access source) {
        int dataStart = ResPayload.Payload.valueOf(null, source.readLong(0)).getDataStart(source);
        root = ResPayload.craft(source, dataStart, (AbstractKCAP) null, -1, null);
    }
    
    public ResPayload getRoot() {
        return root;
    }
    
    public void repack(File file) {
        file.delete();
        if (!file.exists())
            try {
                file.createNewFile();
            }
            catch (IOException e1) {
                Main.LOGGER.log(Level.WARNING, "Exception while writing new .res file.", e1);
            }
        
        try (Access dest = new FileAccess(file); IResData data = new ResData()) {
            root.writeKCAP(dest, data);
            
            if(data.getSize() != 0) {
                dest.setPosition(Utils.align(root.getSizeOfRoot(), 0x80));
                dest.writeByteArray(data.getStream().toByteArray());
            }
        }
        catch (IOException e) {
            Main.LOGGER.log(Level.WARNING, "Exception while writing new .res file.", e);
        }
    }
}
