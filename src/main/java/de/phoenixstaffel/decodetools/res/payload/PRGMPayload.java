package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class PRGMPayload extends ResPayload {
    private String name;
    
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    
    public PRGMPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        super(parent);
        this.name = name;
        
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
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
    }
}
