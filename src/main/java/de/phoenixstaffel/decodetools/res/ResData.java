package de.phoenixstaffel.decodetools.res;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

//FIXME takes too long for ARCV rebuilding
public class ResData implements Closeable {
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    
    private List<ResDataEntry> list = new ArrayList<>();
    private int count = 0;
    
    public int add(byte[] data, boolean onlyOnce, KCAPFile parent) {
        Optional<ResDataEntry> entry = list.stream().filter(a -> onlyOnce && a.isEqual(data, parent)).findFirst();
        
        if (entry.isPresent()) {
            return entry.get().getAddress();
        }
        
        int address = stream.size();
        count++;
        
        if (onlyOnce)
            list.add(new ResDataEntry(data, address, parent));
        
        byte[] padding = new byte[Utils.getPadded(data.length, 0x80) - data.length];
        stream.write(padding, 0, padding.length);
        stream.write(data, 0, data.length);
        
        return address;
    }
    
    @Override
    public void close() throws IOException {
        stream.close();
    }
    
    public ByteArrayOutputStream getStream() {
        return stream;
    }
    
    private class ResDataEntry {
        private int address;
        private byte[] data;
        private KCAPFile parent;
        
        public ResDataEntry(byte[] data, int address, KCAPFile parent) {
            this.data = data;
            this.address = address;
            this.parent = parent;
        }
        
        public int getAddress() {
            return address;
        }
        
        public boolean isEqual(byte[] data2, KCAPFile parent2) {
            if (parent != parent2)
                return false;
            
            if (data.length != data2.length)
                return false;
            
            return Arrays.equals(data, data2);
        }
    }
    
    public int getDataEntries() {
        return count;
    }
}
