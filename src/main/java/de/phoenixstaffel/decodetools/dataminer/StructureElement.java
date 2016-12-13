package de.phoenixstaffel.decodetools.dataminer;

import java.util.HashMap;
import java.util.Map;

public class StructureElement {
    private final String identifier;
    private final StructureType type;
    private final Map<String, Object> extra;
    
    public StructureElement(String identifier, StructureType type) {
        this(identifier, type, new HashMap<String, Object>());
    }
    
    public StructureElement(String identifier, StructureType type, Map<String, Object> extra) {
        this.identifier = identifier;
        this.type = type;
        this.extra = extra;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public StructureType getType() {
        return type;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
}