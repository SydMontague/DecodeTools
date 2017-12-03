package de.phoenixstaffel.decodetools.res.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.phoenixstaffel.decodetools.res.HeaderExtension.NamePointer;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;

public class FileNameExtensionPayload implements HeaderExtensionPayload {
    private Map<Integer, String> nameMap = new HashMap<>();
    
    public FileNameExtensionPayload(int entryCount, Access source) {
        NamePointer[] pointers = new NamePointer[entryCount];
        String[] names = new String[entryCount];
        
        for (int i = 0; i < entryCount; i++)
            pointers[i] = new NamePointer(source.readInteger(), source.readInteger());
        
        for (int i = 0; i < entryCount; i++)
            names[i] = source.readASCIIString();
        
        for (int i = 0; i < entryCount; i++) {
            nameMap.put(pointers[i].getLength(), names[i]);
        }
    }
    
    @Override
    public int getSize() {
        int size = nameMap.size() * 8;
        
        for (String name : nameMap.values()) {
            size += name.length() + 1;
        }
        
        return Utils.getPadded(size, 0x4);
    }
    
    @Override
    public void writeKCAP(Access dest, int start) {
        int stringStart = start + nameMap.size() * 8;
        
        for (Entry<Integer, String> entry : nameMap.entrySet()) {
            dest.writeInteger(stringStart);
            dest.writeInteger(entry.getKey());
            stringStart += entry.getValue().length() + 1;
        }
        
        for (String entry : nameMap.values()) {
            dest.writeString(entry, "ASCII");
            dest.writeByte((byte) 0);
        }
        
        dest.setPosition(start + getSize());
    }
    
    @Override
    public int getEntryCount() {
        return nameMap.size();
    }
    
    @Override
    public String get(int i) {
        return nameMap.getOrDefault(i, null);
    }
}
