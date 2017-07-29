package de.phoenixstaffel.decodetools.res.payload.hsem;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class HSEM07Entry implements HSEMEntryPayload {
    private short unkn1;
    private short unkn2;
    private short unkn3;
    private short unkn4;
    
    public HSEM07Entry(Access source) {
        unkn1 = source.readShort();
        unkn2 = source.readShort();
        unkn3 = source.readShort();
        unkn4 = source.readShort();
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(unkn1);
        dest.writeShort(unkn2);
        dest.writeShort(unkn3);
        dest.writeShort(unkn4);
    }
    
    @Override
    public int getSize() {
        return 0x08;
    }
}
