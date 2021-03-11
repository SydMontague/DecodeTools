package net.digimonworld.decodetools.res.payload.hsem;

import java.util.HashMap;
import java.util.Map;

import net.digimonworld.decodetools.core.Access;

public class HSEMTextureEntry implements HSEMEntry {
    private short unkn1; // always 0?
    // short textureCount
    
    private Map<Short, Short> textureAssignment = new HashMap<>();
    
    public HSEMTextureEntry(Map<Short, Short> assignments) {
        this.textureAssignment.putAll(assignments);
    }
    
    public HSEMTextureEntry(Access source) {
        unkn1 = source.readShort();
        short textureCount = source.readShort();
        
        for (int i = 0; i < textureCount; ++i)
            textureAssignment.put(source.readShort(), source.readShort());
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort((short) getHSEMType().getId());
        dest.writeShort((short) getSize());
        
        dest.writeShort(unkn1);
        dest.writeShort((short) textureAssignment.size());
        
        textureAssignment.forEach((a, b) -> {
            dest.writeShort(a);
            dest.writeShort(b);
        });
    }
    
    @Override
    public int getSize() {
        return 0x08 + textureAssignment.size() * 0x04;
    }
    
    @Override
    public HSEMEntryType getHSEMType() {
        return HSEMEntryType.TEXTURE;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Texture | U1: ");
        builder.append(unkn1);
        
        textureAssignment.forEach((k, v) -> {
            builder.append(" | ");
            builder.append(k);
            builder.append(" ");
            builder.append(v);
        });
        return builder.toString();
    }
    
    public Map<Short, Short> getTextureAssignment() {
        return textureAssignment;
    }
}
