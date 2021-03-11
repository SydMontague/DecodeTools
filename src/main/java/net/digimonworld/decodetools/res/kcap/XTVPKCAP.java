package net.digimonworld.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.payload.XTVOPayload;

public class XTVPKCAP extends AbstractKCAP {
    private static final int XTVP_VERSION = 0x01;
    
    private List<XTVOPayload> entries = new ArrayList<>();
    
    public XTVPKCAP(AbstractKCAP parent, List<XTVOPayload> entries) {
        super(parent, 0);
        
        this.entries = new ArrayList<>(entries);
    }
    
    public XTVPKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        if (source.readInteger() != KCAPType.XTVP.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate XTVP KCAP, but didn't find a XTVP header.");
        
        int version = source.readInteger();
        if (version != XTVP_VERSION)
            throw new IllegalArgumentException("Tried to instanciate XTVP KCAP and expected version 1, but got " + version);
        
        if (source.readInteger() != info.entries) // XTVP numEntries, always matches KCAP numEntries?
            Main.LOGGER.warning(() -> "Number of entries in KCAP and XTVP header are not equal.");
        source.readInteger(); // padding
        
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        for (KCAPPointer p : pointer) {
            if (p.getOffset() == 0 && p.getSize() == 0)
                throw new IllegalArgumentException("Got a Void pointer, but only XTVO entries are allowed.");
            
            source.setPosition(info.startAddress + p.getOffset());
            ResPayload payload = ResPayload.craft(source, dataStart, this, p.getSize(), null);
            
            if (payload.getType() != Payload.XTVO)
                throw new IllegalArgumentException("Got a " + payload.getType() + " entry, but only XTVO entries are allowed.");
            
            entries.add((XTVOPayload) payload);
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if (source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for XTVP KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
    }

    public List<XTVOPayload> getXTVOEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public List<ResPayload> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public XTVOPayload get(int i) {
        return entries.get(i);
    }
    
    @Override
    public int getEntryCount() {
        return entries.size();
    }
    
    @Override
    public KCAPType getKCAPType() {
        return KCAPType.XTVP;
    }
    
    @Override
    public int getSize() {
        int size = 0x30;
        size += getEntryCount() * 0x08;
        
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
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());
        
        dest.writeInteger(getEntryCount());
        dest.writeInteger(0x00); // type count, always 0 for this type
        dest.writeInteger(0x30); // header size, always 0x30 for this type
        dest.writeInteger(0x00); // type payload start, always 0 for this type
        
        dest.writeInteger(getKCAPType().getMagicValue());
        dest.writeInteger(XTVP_VERSION);
        dest.writeInteger(getEntryCount());
        dest.writeInteger(0x00); // padding
        
        int fileStart = Utils.align(0x30 + getEntryCount() * 0x08, 0x10);
        int contentStart = fileStart;
        
        // write pointer table
        for (ResPayload entry : entries) {
            fileStart = Utils.align(fileStart, 0x10); // align content start
            
            dest.writeInteger(fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
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
