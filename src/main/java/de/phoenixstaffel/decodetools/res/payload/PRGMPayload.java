package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.ResData;

public class PRGMPayload extends KCAPPayload {
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    
    public PRGMPayload(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);
        
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
    }
    
    @Override
    public int getSize() {
        return 0x10;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.PRGM;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
    }
}