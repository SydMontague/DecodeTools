package net.digimonworld.decodetools.res;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.digimonworld.decodetools.core.Utils;

public class DummyResData implements IResData {
    private final List<ResDataEntry> list = new ArrayList<>();
    private final Optional<DummyResData> parent;
    
    private int count = 0;
    private int currentSize = 0;
    
    public DummyResData(DummyResData parent) {
        this.parent = Optional.ofNullable(parent);
    }
    
    public DummyResData() {
        this(null);
    }
    
    public int add(byte[] data, int size, boolean onlyOnce) {
        Optional<ResDataEntry> entry = list.stream().filter(a -> onlyOnce && a.isEqual(data)).findFirst();
        
        if (entry.isPresent())
            return entry.get().getAddress();
        
        byte[] padding = new byte[Utils.align(getSize(), 0x80) - getSize()];
        currentSize += padding.length;
        
        int address = currentSize + getOffset();
        count++;
        
        if (onlyOnce)
            list.add(new ResDataEntry(data, address));
        
        currentSize += size;
        
        return address;
    }
    
    @Override
    public int add(byte[] data, boolean onlyOnce) {
        return add(data, data.length, onlyOnce);
    }
    
    public void add(DummyResData data) {
        if (data.getSize() > 0) {
            this.currentSize = Utils.align(getSize(), 0x80);
            this.currentSize += data.getSize();
            this.count += data.getDataEntries();
        }
    }

    private int getOffset() {
        return Utils.align(parent.map(IResData::getCurrentAddress).orElse(0), 0x80);
    }
    
    @Override
    public int getDataEntries() {
        return count;
    }
    
    @Override
    public int getSize() {
        return currentSize;
    }
    
    @Override
    public int getCurrentAddress() {
        return getOffset() + getSize();
    }
}
