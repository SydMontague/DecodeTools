package de.phoenixstaffel.decodetools.dataminer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;

import de.phoenixstaffel.decodetools.Main;

public class Structure {
    private long offset;
    private int numEntries;
    private String structureClass;
    private List<StructureElement> structureList = new ArrayList<>();
    
    @SuppressWarnings("unchecked")
    public Structure(File file) {
        Yaml yaml = new Yaml();
        Map<Object, Object> map;
        
        try (Reader reader = new FileReader(file)) {
            map = yaml.loadAs(reader, Map.class);
        }
        catch (IOException e) {
            Main.LOGGER.log(Level.SEVERE, "Failed to read file: " + file + "Stacktrace: " + e);
            return;
        }
        
        offset = Long.parseLong(map.getOrDefault("Offset", "0").toString(), 16);
        numEntries = Integer.parseInt(map.getOrDefault("Entries", "0").toString());
        structureClass = map.getOrDefault("StructureClass", "java.lang.Object").toString();
        
        for (Entry<Object, Object> entry : ((Map<Object, Object>) map.getOrDefault("Structure",
                                                                                   new HashMap<>())).entrySet()) {
            String identifier = entry.getKey().toString();
            
            if (entry.getValue() instanceof Map) {
                
                Map<String, Object> localMap = (Map<String, Object>) entry.getValue();
                StructureType type = StructureType.valueOf(localMap.get("Type").toString());
                
                localMap.remove("Type");
                structureList.add(new StructureElement(identifier, type, localMap));
                continue;
            }
            
            StructureType type = StructureType.valueOf(entry.getValue().toString());
            structureList.add(new StructureElement(identifier, type));
        }
    }
    
    public List<StructureElement> getStructureElements() {
        return structureList;
    }
    
    public long getOffset() {
        return offset;
    }
    
    public int getNumEntries() {
        return numEntries;
    }
    
    public String getStructureClass() {
        return structureClass;
    }
}
