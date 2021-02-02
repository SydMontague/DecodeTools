package de.phoenixstaffel.decodetools.res;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.phoenixstaffel.decodetools.core.Utils;

public class ResData implements IResData, Closeable {
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private final List<ResDataEntry> list = new ArrayList<>();
    private final Optional<ResData> parent;
    
    private int count = 0;
    
    public ResData(ResData parent) {
        this.parent = Optional.ofNullable(parent);
    }
    
    public ResData() {
        this(null);
    }
    
    @Override
    public int add(byte[] data, boolean onlyOnce) {
        Optional<ResDataEntry> entry = list.stream().filter(a -> onlyOnce && a.isEqual(data)).findFirst();
        
        if (entry.isPresent()) {
            return entry.get().getAddress();
        }

        stream.writeBytes(new byte[Utils.align(getSize(), 0x80) - getSize()]);
        
        int address = stream.size() + getOffset();
        count++;
        
        if (onlyOnce)
            list.add(new ResDataEntry(data, address));
        
        stream.write(data, 0, data.length);
        
        return address;
    }
    
    public void add(ResData data) {
        if(data.getSize() > 0) {
            stream.writeBytes(new byte[Utils.align(getSize(), 0x80) - getSize()]);
            stream.writeBytes(data.getStream().toByteArray());
            this.count += data.getDataEntries();
        }
    }
    
    private int getOffset() {
        return Utils.align(parent.map(IResData::getCurrentAddress).orElse(0), 0x80);
    }
    
    @Override
    public void close() {
        try {
            stream.close();
        }
        catch (IOException e) {
            // nothing to catch, will never happen with ByteArrayOutputStream
        }
    }
    
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
    
    @Override
    public int getCurrentAddress() {
        return getOffset() + getSize();
    }
}
