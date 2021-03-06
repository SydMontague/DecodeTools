package net.digimonworld.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.payload.TNOJPayload;

public class TNOJKCAP extends AbstractKCAP {
    
    private List<TNOJPayload> entries = new ArrayList<>();
    
    public TNOJKCAP(HSMPKCAP parent, List<TNOJPayload> entries) {
        super(parent, 0);
        
        this.entries.addAll(entries);
        entries.forEach(a -> a.setParent(this));
    }
    
    public TNOJKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // make sure it's actually a TNOJ
        if (source.readInteger() != KCAPType.TNOJ.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate TNOJ KCAP, but didn't find a TNOJ header.");
        
        // padding, always 0
        source.readInteger();
        source.readInteger();
        source.readInteger();
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        // load the names
        Map<Integer, String> names = loadNames(source, info);
        
        // load the content
        for (int i = 0; i < info.entries; ++i) {
            KCAPPointer p = pointer.get(i);
            String name = names.getOrDefault(i, null); // give entries a name if they have one
            
            if (p.getOffset() == 0 && p.getSize() == 0)
                throw new IllegalArgumentException("Got a Void pointer, but only TNOJ entries are allowed.");
            
            source.setPosition(info.startAddress + p.getOffset());
            entries.add(new TNOJPayload(source, dataStart, this, p.getSize(), name));
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if (source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for TNOJ KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
    }
    
    public List<TNOJPayload> getTNOJEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public List<ResPayload> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public TNOJPayload get(int i) {
        return entries.get(i);
    }
    
    @Override
    public int getEntryCount() {
        return entries.size();
    }
    
    @Override
    public KCAPType getKCAPType() {
        return KCAPType.TNOJ;
    }
    
    @Override
    public int getSize() {
        int size = 0x30; // size of header
        size += getEntryCount() * 0x08; // size of pointer map
        
        // size of name payload
        size += entries.stream().filter(TNOJPayload::hasName).count() * 0x08;
        size += entries.stream().filter(TNOJPayload::hasName).collect(Collectors.summingInt((TNOJPayload a) -> a.getName().length() + 1));
        
        size = Utils.align(size, 0x10);
        
        for (ResPayload entry : entries) {
            if (entry.getType() == null) // VOID type, i.e. size 0 entries
                continue;
            
            size = Utils.align(size, 0x10); // align to specific alignment
            size += entry.getSize(); // size of entry
        }
        
        return size;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        long start = dest.getPosition();
        
        int typeCount = (int) entries.stream().filter(TNOJPayload::hasName).count();
        int payloadStart = typeCount > 0 ? 0x30 + getEntryCount() * 0x08 : 0;
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());
        
        dest.writeInteger(getEntryCount());
        dest.writeInteger(typeCount); // type count, i.e. named entries
        dest.writeInteger(0x30); // header size, always 0x30 for this type
        dest.writeInteger(payloadStart); // type payload start
        
        dest.writeInteger(getKCAPType().getMagicValue());
        dest.writeInteger(0x00); // padding
        dest.writeInteger(0x00); // padding
        dest.writeInteger(0x00); // padding
        
        int fileStart = 0x30;
        fileStart += getEntryCount() * 0x08;
        fileStart += typeCount * 0x08;
        fileStart += entries.stream().filter(TNOJPayload::hasName).collect(Collectors.summingInt((TNOJPayload a) -> a.getName().length() + 1));
        fileStart = Utils.align(fileStart, 0x10);
        int contentStart = fileStart;
        
        // write pointer table
        for (ResPayload entry : entries) {
            fileStart = Utils.align(fileStart, 0x10); // align content start
            
            dest.writeInteger(fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        // write name payload
        int stringStart = payloadStart + typeCount * 0x08;
        writeNames(dest, stringStart, entries);
        
        // move write pointer to start of content
        dest.setPosition(start + contentStart);
        
        try (ResData localDataStream = new ResData(dataStream)) {
            for (ResPayload entry : entries) {
                // align content start
                long aligned = Utils.align(dest.getPosition() - start, 0x10);
                dest.setPosition(start + aligned);
                
                // write content
                entry.writeKCAP(dest, localDataStream);
            }
            dataStream.add(localDataStream);
        }
    }
    
}
