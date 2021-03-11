package net.digimonworld.decodetools.res;

import java.util.Arrays;

public interface IResData {
    
    public int add(byte[] data, boolean onlyOnce);
    
    public int getDataEntries();
    
    public int getSize();

    public int getCurrentAddress();
    
    public class ResDataEntry {
        private final int address;
        private final byte[] data;
        
        public ResDataEntry(byte[] data, int address) {
            this.data = data;
            this.address = address;
        }
        
        public int getAddress() {
            return address;
        }
        
        public boolean isEqual(byte[] data2) {
            return Arrays.equals(data, data2);
        }
    }
}
