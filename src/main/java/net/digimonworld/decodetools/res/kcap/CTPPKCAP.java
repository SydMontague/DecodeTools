package net.digimonworld.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.payload.CTPPPayload;

public class CTPPKCAP extends AbstractKCAP {
    
    private List<CTPPPayload> entries = new ArrayList<>();
    
    public CTPPKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // make sure it's actually a CTPP
        if (source.readInteger() != KCAPType.CTPP.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate CTPP KCAP, but didn't find a CTPP header.");
        
        // padding, always 0
        source.readInteger();
        source.readInteger();
        source.readInteger();
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        // load the content
        for (KCAPPointer p : pointer) {
            source.setPosition(info.startAddress + p.getOffset());
            entries.add(new CTPPPayload(source, dataStart, this, p.getSize(), null));
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if (source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for CTPP KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
    }
    
    @Override
    public List<ResPayload> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public CTPPPayload get(int i) {
        return entries.get(i);
    }
    
    @Override
    public int getEntryCount() {
        return entries.size();
    }
    
    @Override
    public KCAPType getKCAPType() {
        return KCAPType.CTPP;
    }
    
    @Override
    public int getSize() {
        int size = 0x30; // size of header
        size += getEntryCount() * 0x08; // size of pointer map
        
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
        dest.writeInteger(0x00); // padding
        dest.writeInteger(0x00); // padding
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
        }
    }
    
}
