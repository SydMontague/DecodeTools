package de.phoenixstaffel.decodetools.res.payload;

import java.io.ByteArrayOutputStream;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

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
    public void writeKCAP(Access dest, ByteArrayOutputStream dataStream) {
        //nothing to write
    }
    
}
