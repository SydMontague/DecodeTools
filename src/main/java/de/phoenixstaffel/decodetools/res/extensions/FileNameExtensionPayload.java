package de.phoenixstaffel.decodetools.res.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension.NamePointer;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;

class FileNameExtensionPayload implements HeaderExtensionPayload {
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
        
        return size;
    }
    
    @Override
    public void writeKCAP(Access dest, int start) {
        System.out.println("called" + nameMap.size());
        
        int stringStart = start + nameMap.size() * 8;
        
        for(Entry<Integer, String> entry : nameMap.entrySet()) {
            dest.writeInteger(stringStart);
            dest.writeInteger(entry.getKey());
            stringStart += entry.getValue().length() + 1;
        }

        for(String entry : nameMap.values()) {
            System.out.println(entry);
            dest.writeString(entry, "ASCII");
            dest.writeByte((byte) 0);
        }
    }

    @Override
    public int getEntryNumber() {
        return nameMap.size();
    }
}
