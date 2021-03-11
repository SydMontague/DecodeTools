package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class VoidPayload extends ResPayload {
    
    public VoidPayload(AbstractKCAP parent) {
        super(parent);
    }
    
    @Override
    public int getSize() {
        return 0;
    }
    
    @Override
    public Payload getType() {
        return null;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        // nothing to write
    }
    
    @Override
    public String toString() {
        return "VOID";
    }
}
