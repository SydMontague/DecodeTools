package de.phoenixstaffel.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.IResData;
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

    public GMIPKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);

        // make sure it's actually a GMIP v1
        if(source.readInteger() != KCAPType.GMIP.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate GMIP KCAP, but didn't find a GMIP header.");
        
        int version = source.readInteger();
        if(version != 0x01)
            throw new IllegalArgumentException("Tried to instanciate GMIP KCAP and expected version 1, but got " + version);
        
        source.readInteger(); //GMIP numEntries, always matches KCAP numEntries?
        source.readInteger(); //padding
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = new ArrayList<>();
        
        for(int i = 0; i < info.entries; ++i) {
            pointer.add(new KCAPPointer(source.readInteger(), source.readInteger()));
        }
        
        // make sure we're actually at the payload start
        if(info.payloadStart != 0) {
            long expectedPayloadStart = info.startAddress + info.payloadStart;
            if(source.getPosition() != expectedPayloadStart)
                throw new IllegalArgumentException("Tried started reading GMIP Payload at " + source.getPosition() + " but expected it to be " + expectedPayloadStart);
        }
        
        // load the payload
        NamePointer[] namePointer = new NamePointer[info.typeEntries];
        for(int i = 0; i < info.typeEntries; ++i) {
            namePointer[i] = new NamePointer(source.readInteger(), source.readInteger());
        }
        
        Map<Integer, String> names = new HashMap<>();
        for(NamePointer name : namePointer) {
            long expectedStringStart = info.startAddress + name.getOffset();
            if(source.getPosition() != expectedStringStart)
                throw new IllegalArgumentException("Tried reading a String from " + source.getPosition() + " but expected it to be from " + expectedStringStart);
            
            names.put(name.getID(), source.readASCIIString());
        }
        
        // load the entries
        for(int i = 0; i < info.entries; ++i) {
            KCAPPointer p = pointer.get(i);
            String name = names.getOrDefault(i, null); // give entries a name if they have one
            
            if(p.getOffset() == 0 && p.getSize() == 0) 
                throw new IllegalArgumentException("Got a Void pointer, but only GMIO entries are allowed.");

            source.setPosition(info.startAddress + p.getOffset());
            ResPayload payload = ResPayload.craft(source, dataStart, this, p.getSize(), name);
            
            if(payload.getType() != Payload.GMIO)
                throw new IllegalArgumentException("Got a " + payload.getType() + " entry, but only GMIO entries are allowed.");
            
            entries.add((GMIOPayload) payload);
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if(source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for normal KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
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
        int size = 0x30;
        size += getEntryCount() * 0x08;
        
        size += entries.stream().filter(GMIOPayload::hasName).count() * 0x08;
        size += entries.stream().filter(GMIOPayload::hasName).collect(Collectors.summingInt((GMIOPayload a) -> a.getName().length() + 1));
        
        size = Utils.align(size, 0x04);

        for(ResPayload entry : entries) {
            size = Utils.align(size, 0x04); // align to GMIP specific alignment
            size += entry.getSize(); // size of entry
        }
        
        return size;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        long start = dest.getPosition();
        
        int typeCount = (int) entries.stream().filter(GMIOPayload::hasName).count();
        int payloadStart = 0;
        if(entries.stream().filter(GMIOPayload::hasName).count() > 0)
            payloadStart = 0x30 + getEntryCount() * 0x08;
        
        // write KCAP/GMIP header
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());
        
        dest.writeInteger(getEntryCount());
        dest.writeInteger(typeCount); // type count, always 0 for this type
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

        for(ResPayload entry : entries) {
            fileStart = Utils.align(fileStart, 0x04); // align content start
            
            dest.writeInteger(fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        // write GMIP payload
        int stringStart = payloadStart + typeCount * 0x08;
        int idCounter = 0;
        
        for(GMIOPayload entry : entries) {
            if(!entry.hasName())
                continue;
            
            dest.writeInteger(stringStart);
            dest.writeInteger(idCounter++);
            stringStart += entry.getName().length() + 1;
        }

        for(GMIOPayload entry : entries) {
            if(!entry.hasName())
                continue;
            
            dest.writeString(entry.getName(), "ASCII");
            dest.writeByte((byte) 0);
        }

        // write entries
        dest.setPosition(start + contentStart);
        
        for(ResPayload entry : entries) {
            // align content start
            long aligned = Utils.align(dest.getPosition() - start, 0x04);
            dest.setPosition(start + aligned);
            
            System.out.println(entry.getType());
            // write content
            entry.writeKCAP(dest, dataStream);
        }
    }
    
}
