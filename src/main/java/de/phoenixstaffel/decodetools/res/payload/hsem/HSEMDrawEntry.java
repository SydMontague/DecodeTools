package de.phoenixstaffel.decodetools.res.payload.hsem;

import de.phoenixstaffel.decodetools.core.Access;

public class HSEMDrawEntry implements HSEMEntry {
    private short unkn1; // some mode?
    private short vertexId;
    private short indexId;
    private short unkn2;
    private short vertexOffset; // where to start reading the vertices from
    private short unkn4;
    private int vertexCount;
    
    public HSEMDrawEntry(short unkn1, short vertexId, short indexId, short unk2, short vertexOffset, short unk4, int vertexCount) {
        this.unkn1 = unkn1;
        this.vertexId = vertexId;
        this.indexId = indexId;
        this.unkn2 = unk2;
        this.vertexOffset = vertexOffset;
        this.unkn4 = unk4;
        this.vertexCount = vertexCount;
    }
    
    public HSEMDrawEntry(Access source) {
        unkn1 = source.readShort();
        vertexId = source.readShort();
        indexId = source.readShort();
        unkn2 = source.readShort();
        vertexOffset = source.readShort();
        unkn4 = source.readShort();
        vertexCount = source.readInteger();
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort((short) getHSEMType().getId());
        dest.writeShort((short) getSize());
        
        dest.writeShort(unkn1);
        dest.writeShort(vertexId);
        dest.writeShort(indexId);
        dest.writeShort(unkn2);
        dest.writeShort(vertexOffset);
        dest.writeShort(unkn4);
        dest.writeInteger(vertexCount);
    }
    
    @Override
    public int getSize() {
        return 0x14;
    }
    
    public short getVertexId() {
        return vertexId;
    }
    
    public short getIndexId() {
        return indexId;
    }
    
    @Override
    public HSEMEntryType getHSEMType() {
        return HSEMEntryType.DRAW;
    }
    

    @Override
    public String toString() {
        return String.format("Draw | M: %s | VID: %s | FID: %s | U2: %s | VOff: %s | U4: %s | VCnt: %s", 
                             unkn1, vertexId, indexId, unkn2, vertexOffset, unkn4, vertexCount);
        
    }
}
