package de.phoenixstaffel.decodetools.res.extensions;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

public class VoidExtension implements HeaderExtension {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    public VoidExtension() {
        // nothing to instantiate
    }
    
    public VoidExtension(Access source) {
        log.log(Level.WARNING, "Instantiated VoidExtension Header, this should NOT happen!");
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new HeaderExtensionPayload() {
        };
    }
    
    @Override
    public Extensions getType() {
        return Extensions.VOID;
    }
    
    @Override
    public int getSize() {
        return 0;
    }
    
    @Override
    public void writeKCAP(Access dest) {
        // nothing to write
    }
    
    @Override
    public int getContentAlignment(KCAPFile parent) {
        if (parent.hasParent() && parent.getParent().getExtension().getType() == Extensions.TDTM)
            return 0x10;
        
        return parent.getGenericAlignment();
    }
}
