package de.phoenixstaffel.decodetools.res.payload.hsem;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class HSEMDrawEntry implements HSEMEntryPayload {
    private short unkn1;
    private short vertexId;
    private short indexId;
    private short unkn2;
    private short unkn3;
    private short unkn4;
    private int vertexCount;
    
    public HSEMDrawEntry(Access source) {
        unkn1 = source.readShort();
        vertexId = source.readShort();
        indexId = source.readShort();
        unkn2 = source.readShort();
        unkn3 = source.readShort();
        unkn4 = source.readShort();
        vertexCount = source.readInteger();
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(unkn1);
        dest.writeShort(vertexId);
        dest.writeShort(indexId);
        dest.writeShort(unkn2);
        dest.writeShort(unkn3);
        dest.writeShort(unkn4);
        dest.writeInteger(vertexCount);
    }
    
    @Override
    public int getSize() {
        return 0x10;
    }
    
    public short getVertexId() {
        return vertexId;
    }
    
    public short getIndexId() {
        return indexId;
    }
}