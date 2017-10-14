package de.phoenixstaffel.decodetools.res.payload.hsem;

import java.util.HashMap;
import java.util.Map;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class HSEMTextureEntry implements HSEMEntryPayload {
    private short unkn1;
    private short textureCount;
    
    private Map<Short, Short> textureAssignment = new HashMap<>();
    
    public HSEMTextureEntry(Access source) {
        unkn1 = source.readShort();
        textureCount = source.readShort();
        
        for (int i = 0; i < textureCount; ++i)
            textureAssignment.put(source.readShort(), source.readShort());
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(unkn1);
        dest.writeShort((short) textureAssignment.size());
        
        textureAssignment.forEach((a, b) -> {
            dest.writeShort(a);
            dest.writeShort(b);
        });
    }
    
    @Override
    public int getSize() {
        return 0x04 + textureAssignment.size() * 0x04;
    }
}
