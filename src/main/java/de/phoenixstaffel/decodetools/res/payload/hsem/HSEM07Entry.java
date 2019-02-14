package de.phoenixstaffel.decodetools.res.payload.hsem;

import de.phoenixstaffel.decodetools.core.Access;

public class HSEM07Entry implements HSEMEntry {
    private short unkn1; // culling mode?
    private short unkn2;
    private short unkn3; // transparency mode?
    private short unkn4;
    
    public HSEM07Entry(Access source) {
        unkn1 = source.readShort();
        unkn2 = source.readShort();
        unkn3 = source.readShort();
        unkn4 = source.readShort();
    }
    
    public HSEM07Entry(short b, short c, short d, short e) {
        this.unkn1 = b;
        this.unkn2 = c;
        this.unkn3 = d;
        this.unkn4 = e;
    }

    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort((short) getHSEMType().getId());
        dest.writeShort((short) getSize());
        
        dest.writeShort(unkn1);
        dest.writeShort(unkn2);
        dest.writeShort(unkn3);
        dest.writeShort(unkn4);
    }
    
    @Override
    public int getSize() {
        return 0x0C;
    }
    
    @Override
    public HSEMEntryType getHSEMType() {
        return HSEMEntryType.UNK07;
    }
    

    @Override
    public String toString() {
        return String.format("Entry03 | U1: %s | U2: %s | U3: %s | U4: %s", unkn1, unkn2, unkn3, unkn4);
    }
}
