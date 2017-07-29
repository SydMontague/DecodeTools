package de.phoenixstaffel.decodetools.res.payload.hsem;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class HSEMMaterialEntry implements HSEMEntryPayload {
    private short unkn1;
    private short materialId;
    
    public HSEMMaterialEntry(Access source) {
        unkn1 = source.readShort();
        materialId = source.readShort();
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(unkn1);
        dest.writeShort(materialId);
    }
    
    @Override
    public int getSize() {
        return 0x04;
    }
}
