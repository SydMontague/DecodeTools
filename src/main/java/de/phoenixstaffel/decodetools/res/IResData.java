package de.phoenixstaffel.decodetools.res;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.util.Arrays;

import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

public interface IResData extends Closeable {
    
    public int add(byte[] data, boolean onlyOnce, KCAPFile parent);
    
    public ByteArrayOutputStream getStream();
    
    public int getDataEntries();
    
    public int getSize();

    public class ResDataEntry {
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
}
