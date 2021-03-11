package net.digimonworld.decodetools.res.payload.hsem;

import net.digimonworld.decodetools.core.Access;

public class HSEMMaterialEntry implements HSEMEntry {
    private short unkn1;
    private short materialId;
    
    public HSEMMaterialEntry(short unk1, short materialId) {
        this.unkn1 = unk1;
        this.materialId = materialId;
    }
    
    public HSEMMaterialEntry(Access source) {
        unkn1 = source.readShort();
        materialId = source.readShort();
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort((short) getHSEMType().getId());
        dest.writeShort((short) getSize());
        dest.writeShort(unkn1);
        dest.writeShort(materialId);
    }
    
    @Override
    public int getSize() {
        return 0x08;
    }
    
    @Override
    public HSEMEntryType getHSEMType() {
        return HSEMEntryType.MATERIAL;
    }
    
    @Override
    public String toString() {
        return String.format("Material | U1: %s | MID: %s", unkn1, materialId);
    }
}
