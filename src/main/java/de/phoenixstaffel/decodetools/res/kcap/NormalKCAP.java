package de.phoenixstaffel.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.ResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.VoidPayload;

public class NormalKCAP extends AbstractKCAP {
    
    private boolean genericAligned = false;
    private List<ResPayload> entries = new ArrayList<>();
    
    public NormalKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        // determine how the KCAP is aligned
        genericAligned = pointer.stream().allMatch(a -> (a.getOffset() % 0x10) == 0);
        
        long diff = source.getPosition() - info.startAddress;
        diff = Utils.align(diff, 0x10) - diff;
        source.readByteArray((int) diff); // padding
        
        // load the content
        for (KCAPPointer p : pointer) {
            if (p.getOffset() == 0 && p.getSize() == 0) // those empty entries exist and have to be preserved
                entries.add(new VoidPayload(this));
            else {
                source.setPosition(info.startAddress + p.getOffset());
                entries.add(ResPayload.craft(source, dataStart, this, p.getSize(), null));
            }
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if (source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for normal KCAP does not match the header. Current: " + source.getPosition() + " Expected: "
                    + expectedEnd);
    }
    
    public NormalKCAP(AbstractKCAP parent, List<? extends ResPayload> entries, boolean genericAligned) {
        super(parent, 0);
        
        this.entries.addAll(entries);
        this.genericAligned = genericAligned;
    }
    
    public int getGenericAlignment() {
        return genericAligned ? 0x10 : 0x04;
    }
    
    @Override
    public List<ResPayload> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public ResPayload get(int i) {
        return entries.get(i);
    }
    
    @Override
    public int getEntryCount() {
        return entries.size();
    }
    
    @Override
    public KCAPType getKCAPType() {
        return KCAPType.NONE;
    }
    
    @Override
    public int getSize() {
        int size = 0x20; // size of header
        size += getEntryCount() * 0x08; // size of pointer map
        
        for (ResPayload entry : entries) {
            if (entry.getType() == null) // VOID type, i.e. size 0 entries
                continue;
            
            size = Utils.align(size, getGenericAlignment()); // align to specific alignment
            size += entry.getSize(); // size of entry
        }
        
        return size;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        long start = dest.getPosition();
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());
        
        dest.writeInteger(getEntryCount());
        dest.writeInteger(0x00); // type count, always 0 for this type
        dest.writeInteger(0x20); // header size, always 0x20 for this type
        dest.writeInteger(0x00); // type payload start, always 0 for this type
        
        int fileStart = Utils.align(0x20 + getEntryCount() * 0x08, getGenericAlignment());
        int contentStart = fileStart;
        
        // write pointer table
        for (ResPayload entry : entries) {
            if (entry.getType() != null) // null entries don't get aligned but still have a pointer
                fileStart = Utils.align(fileStart, getGenericAlignment()); // align content start
                
            dest.writeInteger(entry.getType() == null ? 0 : fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        // move write pointer to start of content
        dest.setPosition(start + contentStart);
        
        try (ResData localDataStream = new ResData(dataStream.getCurrentAddress())) {
            for (ResPayload entry : entries) {
                if (entry.getType() == null) // null entries have no content
                    continue;
                
                // align content start
                long aligned = Utils.align(dest.getPosition() - start, getGenericAlignment());
                dest.setPosition(start + aligned);
                
                // write content
                entry.writeKCAP(dest, localDataStream);
            }
            dataStream.add(localDataStream);
        }
    }
    
}
