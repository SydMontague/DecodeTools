package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.ResData;

public class LDMPPayload extends KCAPPayload {
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    private int unknown5;
    private int unknown6;
    private int unknown7;
    private int unknown8;
    
    public LDMPPayload(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);
        
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
        unknown5 = source.readInteger();
        unknown6 = source.readInteger();
        unknown7 = source.readInteger();
        unknown8 = source.readInteger();
    }
    
    @Override
    public int getSize() {
        return 0x20;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.LDMP;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        dest.writeInteger(unknown5);
        dest.writeInteger(unknown6);
        dest.writeInteger(unknown7);
        dest.writeInteger(unknown8);
    }
}
