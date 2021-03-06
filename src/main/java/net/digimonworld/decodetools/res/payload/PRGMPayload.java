package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.NameablePayload;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class PRGMPayload extends NameablePayload {
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    
    public PRGMPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent, name);
        
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
