package de.phoenixstaffel.decodetools.res;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class ResData implements IResData {
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    
    private List<ResDataEntry> list = new ArrayList<>();
    private int count = 0;
    
    @Override
    public int add(byte[] data, boolean onlyOnce, AbstractKCAP parent) {
        Optional<ResDataEntry> entry = list.stream().filter(a -> onlyOnce && a.isEqual(data, parent)).findFirst();
        
        if (entry.isPresent()) {
            return entry.get().getAddress();
        }

        byte[] padding = new byte[Utils.align(getSize(), 0x80) - getSize()];
        stream.write(padding, 0, padding.length);
        
        int address = stream.size();
        count++;
        
        if (onlyOnce)
            list.add(new ResDataEntry(data, address, parent));
        
        stream.write(data, 0, data.length);
        
        return address;
    }
    
    @Override
    public void close() throws IOException {
        stream.close();
    }
    
    @Override
    public ByteArrayOutputStream getStream() {
        return stream;
    }
    
    @Override
    public int getDataEntries() {
        return count;
    }
    
    @Override
    public int getSize() {
        return stream.size();
    }
}
