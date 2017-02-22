package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.ResData;

public class VoidPayload extends KCAPPayload {
    
    public VoidPayload(KCAPFile parent) {
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
