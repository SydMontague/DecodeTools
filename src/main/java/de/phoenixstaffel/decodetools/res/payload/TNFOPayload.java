package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.ResData;

public class TNFOPayload extends KCAPPayload {
    private byte[] data;
    
    public TNFOPayload(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);
        
        data = source.readByteArray(size);
    }
    
    @Override
    public int getSize() {
        return data.length;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.TNFO;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        for (byte i : data)
            dest.writeByte(i);
    }
}
