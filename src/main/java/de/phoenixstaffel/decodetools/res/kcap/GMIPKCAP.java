package de.phoenixstaffel.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.ResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;

/*
 * 
 * 
 * 
 *
 */
public class GMIPKCAP extends AbstractKCAP {
    
    private static final int GMIP_VERSION = 0x01;
    
    private List<GMIOPayload> entries = new ArrayList<>();
    
    public GMIPKCAP(AbstractKCAP parent, List<GMIOPayload> images) {
        super(parent, 0);
        this.entries.addAll(images);
    }
    
    public GMIPKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // make sure it's actually a GMIP v1
        if (source.readInteger() != KCAPType.GMIP.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate GMIP KCAP, but didn't find a GMIP header.");
        
        int version = source.readInteger();
        if (version != GMIP_VERSION)
            throw new IllegalArgumentException("Tried to instanciate GMIP KCAP and expected version 1, but got " + version);
        
        if (source.readInteger() != info.entries) // GMIP numEntries, always matches KCAP numEntries?
            Main.LOGGER.warning(() -> "Number of entries in KCAP and GMIP header are not equal.");
        source.readInteger(); // padding
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        // load the names
        Map<Integer, String> names = loadNames(source, info);
        
        // load the entries
        for (int i = 0; i < info.entries; ++i) {
            KCAPPointer p = pointer.get(i);
            String name = names.getOrDefault(i, null); // give entries a name if they have one
            
            if (p.getOffset() == 0 && p.getSize() == 0)
                throw new IllegalArgumentException("Got a Void pointer, but only GMIO entries are allowed.");
            
            source.setPosition(info.startAddress + p.getOffset());
            ResPayload payload = ResPayload.craft(source, dataStart, this, p.getSize(), name);
            
            if (payload.getType() != Payload.GMIO)
                throw new IllegalArgumentException("Got a " + payload.getType() + " entry, but only GMIO entries are allowed.");
            
            entries.add((GMIOPayload) payload);
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if (source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for GMIP KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
    }
    
    public List<GMIOPayload> getGMIOEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    public void add(GMIOPayload gmio) {
        gmio.setParent(this);
        entries.add(gmio);
    }
    
    public void remove(int index) {
        entries.remove(index);
    }
    
    public void swap(int id1, int id2) {
        Collections.swap(entries, id1, id2);
    }
    
    @Override
    public List<ResPayload> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public GMIOPayload get(int i) {
        return entries.get(i);
    }
    
    @Override
    public int getEntryCount() {
        return entries.size();
    }
    
    @Override
    public KCAPType getKCAPType() {
        return KCAPType.GMIP;
    }
    
    @Override
    public int getSize() {
        int size = 0x30; // side of header
        size += getEntryCount() * 0x08; // size of pointer map
        
        // size of name payload
        size += entries.stream().filter(GMIOPayload::hasName).count() * 0x08;
        size += entries.stream().filter(GMIOPayload::hasName).collect(Collectors.summingInt((GMIOPayload a) -> a.getName().length() + 1));
        
        size = Utils.align(size, 0x04);
        
        for (ResPayload entry : entries) {
            if (entry.getType() == null) // VOID type, i.e. size 0 entries
                continue;
            
            size = Utils.align(size, 0x04); // align to GMIP specific alignment
            size += entry.getSize(); // size of entry
        }
        
        return size;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        long start = dest.getPosition();
        
        int typeCount = (int) entries.stream().filter(GMIOPayload::hasName).count();
        int payloadStart = typeCount > 0 ? 0x30 + getEntryCount() * 0x08 : 0;
        
        // write KCAP/GMIP header
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());
        
        dest.writeInteger(getEntryCount());
        dest.writeInteger(typeCount); // type count
        dest.writeInteger(0x30); // header size, always 0x30 for this type
        dest.writeInteger(payloadStart); // type payload start, after the pointer table or 0 if empty
        
        dest.writeInteger(getKCAPType().getMagicValue());
        dest.writeInteger(GMIP_VERSION);
        dest.writeInteger(getEntryCount());
        dest.writeInteger(0);
        
        // write pointer table
        int fileStart = 0x30;
        fileStart += getEntryCount() * 0x08;
        fileStart += typeCount * 0x08;
        fileStart += entries.stream().filter(GMIOPayload::hasName).collect(Collectors.summingInt((GMIOPayload a) -> a.getName().length() + 1));
        fileStart = Utils.align(fileStart, 0x04);
        
        int contentStart = fileStart;
        
        for (ResPayload entry : entries) {
            fileStart = Utils.align(fileStart, 0x04); // align content start
            
            dest.writeInteger(fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        // write GMIP payload
        int stringStart = payloadStart + typeCount * 0x08;
        writeNames(dest, stringStart, entries);
        
        // write entries
        dest.setPosition(start + contentStart);
        
        try (ResData localDataStream = new ResData(dataStream)) {
            for (ResPayload entry : entries) {
                // align content start
                long aligned = Utils.align(dest.getPosition() - start, 0x04);
                dest.setPosition(start + aligned);
                
                // write content
                entry.writeKCAP(dest, localDataStream);
            }
            dataStream.add(localDataStream);
        }
    }
    
}
