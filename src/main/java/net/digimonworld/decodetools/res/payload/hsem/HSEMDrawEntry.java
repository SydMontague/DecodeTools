package net.digimonworld.decodetools.res.payload.hsem;

import net.digimonworld.decodetools.core.Access;

public class HSEMDrawEntry implements HSEMEntry {
 // probably BeginMode mapping? Valid values 0-8
    /*
     * Probably BeginMode mapping, i.e. GL_TRIANGLES, GL_TRIANGLE_STRIP, etc.
     * 0 -> LINES? GPU melts
     * 1 -> LINE_LOOP? Model disappears
     * 2 -> nothing?
     * 3 -> LINE_STRIP? Does nothing
     * 4 -> TRIANGLES
     * 5 -> TRIANGLE_STRIP
     * 6 -> TRIANGLE_FAN
     * 7 -> nothing?
     * 8 -> QUADS? Does nothing
     */
    private short unkn1; 
    private short vertexId;
    private short indexId;
    private short unkn2;
    private int vertexOffset; // where to start reading the vertices from
    private int vertexCount;
    
    public HSEMDrawEntry(short unkn1, short vertexId, short indexId, short unk2, int vertexOffset, int vertexCount) {
        this.unkn1 = unkn1;
        this.vertexId = vertexId;
        this.indexId = indexId;
        this.unkn2 = unk2;
        this.vertexOffset = vertexOffset;
        this.vertexCount = vertexCount;
    }
    
    public HSEMDrawEntry(Access source) {
        unkn1 = source.readShort();
        vertexId = source.readShort();
        indexId = source.readShort();
        unkn2 = source.readShort();
        vertexOffset = source.readInteger();
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
        dest.writeInteger(vertexOffset);
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
        return String.format("Draw | M: %s | VID: %s | FID: %s | U2: %s | VOff: %s | VCnt: %s", 
                             unkn1, vertexId, indexId, unkn2, vertexOffset, vertexCount);
        
    }
}
