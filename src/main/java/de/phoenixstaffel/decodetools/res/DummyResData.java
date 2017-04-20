package de.phoenixstaffel.decodetools.res;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

public class DummyResData implements IResData {
    private List<ResDataEntry> list = new ArrayList<>();
    private int count = 0;
    private int currentAddress = 0;
    
    public int add(byte[] data, int size, boolean onlyOnce, KCAPPayload parent) {

        Optional<ResDataEntry> entry = list.stream().filter(a -> onlyOnce && a.isEqual(data, parent)).findFirst();
        
        if (entry.isPresent()) {
            return entry.get().getAddress();
        }

        byte[] padding = new byte[Utils.getPadded(getSize(), 0x80) - getSize()];
        currentAddress += padding.length;
        
        int address = currentAddress; 
        count++;
        
        if (onlyOnce)
            list.add(new ResDataEntry(data, address, parent));
        
        currentAddress += size;
        
        return address;
    }
    
    @Override
    public int add(byte[] data, boolean onlyOnce, KCAPPayload parent) {
        return add(data, data.length, onlyOnce, parent);
    }
    
    @Override
    public void close() throws IOException {
        //nothing to implement
    }
    
    @Override
    public ByteArrayOutputStream getStream() {
        return null;
    }
    
    @Override
    public int getDataEntries() {
        return count;
    }

    @Override
    public int getSize() {
        return currentAddress;
    }
}
