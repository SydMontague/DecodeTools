package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class VoidPayload extends ResPayload {
    
    public VoidPayload(AbstractKCAP parent) {
        super(null); //TODO parent
    }
    
    public VoidPayload(KCAPPayload parent) {
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
    public void writeKCAP(Access dest, IResData dataStream) {
        // nothing to write
    }
    
    @Override
    public String toString() {
        return "VOID";
    }
}
