package de.phoenixstaffel.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.QSTMPayload;
import de.phoenixstaffel.decodetools.res.payload.VCTMPayload;

//TODO figure out how TDTM works and abstrahize it per entry
public class TDTMKCAP extends AbstractKCAP {
    private static final int TDTM_VERSION = 2;

    private List<TDTMEntry> tdtmEntry = new ArrayList<>();
    
    private List<QSTMPayload> qstm = new ArrayList<>();
    private List<VCTMPayload> vctm = new ArrayList<>();
    
    private float time1;
    private float time2;
    private float time3;
    private float time4;
    
    protected TDTMKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);

        // make sure it's actually a TDTM
        if(source.readInteger() != KCAPType.TDTM.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate TDTM KCAP, but didn't find a TDTM header.");
        
        int version = source.readInteger();
        if(version != TDTM_VERSION)
            throw new IllegalArgumentException("Tried to instanciate TDTM KCAP and expected version 2, but got " + version);
        
        int numEntries = source.readInteger();
        source.readInteger(); // padding
        
        time1 = source.readFloat();
        time2 = source.readFloat();
        time3 = source.readFloat();
        time4 = source.readFloat();
        
        // one TDTM entry per QSTM in qstmKCAP?
        for(int i = 0; i < numEntries; ++i)
            tdtmEntry.add(new TDTMEntry(source));
        
        if(numEntries % 2 == 1)
            source.readLong(); // padding
        
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);

        if(pointer.size() != 2)
            throw new IllegalArgumentException("A TDTM KCAP has always two elements, but this one has " + pointer.size() + "!");
        
        source.setPosition(info.startAddress + pointer.get(0).getOffset());
        NormalKCAP qstmKCAP = (NormalKCAP) AbstractKCAP.craftKCAP(source, this, dataStart);

        source.setPosition(info.startAddress + pointer.get(1).getOffset());
        NormalKCAP vctmKCAP = (NormalKCAP) AbstractKCAP.craftKCAP(source, this, dataStart);
        
        for(ResPayload entry : qstmKCAP) {
            if(entry.getType() != Payload.QSTM) 
                throw new IllegalArgumentException("Got a " + entry.getType() + " entry, but only QSTM entries are allowed.");
            
            qstm.add((QSTMPayload) entry);
        }
        
        for(ResPayload entry : vctmKCAP) {
            if(entry.getType() != Payload.VCTM) 
                throw new IllegalArgumentException("Got a " + entry.getType() + " entry, but only VCTM entries are allowed.");
            
            vctm.add((VCTMPayload) entry);
        }
    }

    @Override
    public List<ResPayload> getEntries() {
        List<ResPayload> entries = new ArrayList<>();
        entries.add(new NormalKCAP(this, qstm, true));
        entries.add(new NormalKCAP(this, vctm, true));

        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public ResPayload get(int i) {
        switch(i) {
            case 0: return new NormalKCAP(this, qstm, true);
            case 1: return new NormalKCAP(this, vctm, true);
            default: throw new NoSuchElementException();
        }
    }
    
    @Override
    public int getEntryCount() {
        return 2;
    }
    
    @Override
    public KCAPType getKCAPType() {
        return KCAPType.TDTM;
    }
    
    @Override
    public int getSize() {
        int size = 0x40; // header
        size += tdtmEntry.size() * 0x08; // TDTM header entries
        size = Utils.align(size, 0x10); // padding
        size += getEntryCount() * 8; // pointer table
        
        size += get(0).getSize();
        size = Utils.align(size, 0x10); // padding
        size += get(1).getSize();
        
        return size;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        long start = dest.getPosition();
        
        int headerSize = 0x40; // header
        headerSize += tdtmEntry.size() * 0x08; // TDTM header entries
        headerSize = Utils.align(headerSize, 0x10); // padding
        
        // write KCAP/TDTM header
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());
        
        dest.writeInteger(getEntryCount());
        dest.writeInteger(0); // type count
        dest.writeInteger(headerSize); // header size, always 0x30 for this type
        dest.writeInteger(0); // type payload start, after the pointer table or 0 if empty
        
        dest.writeInteger(getKCAPType().getMagicValue());
        dest.writeInteger(TDTM_VERSION);
        dest.writeInteger(tdtmEntry.size());
        dest.writeInteger(0); // padding

        dest.writeFloat(time1);
        dest.writeFloat(time2);
        dest.writeFloat(time3);
        dest.writeFloat(time4);
        
        // write TDTM entries
        for(TDTMEntry entry : tdtmEntry) {
            dest.writeShort(entry.unknown1);
            dest.writeShort(entry.unknown2);
            dest.writeInteger(entry.unknown3);
        }
        
        if(tdtmEntry.size() % 2 == 1)
            dest.writeLong(0); // padding
        
        // write pointer table
        int fileStart = (int) (dest.getPosition() - start + 0x10);
        
        ResPayload qstmKCAP = get(0);
        dest.writeInteger(fileStart);
        dest.writeInteger(qstmKCAP.getSize());
        fileStart += qstmKCAP.getSize();
        
        fileStart = Utils.align(fileStart, 0x10); // align content start

        ResPayload vctmKCAP = get(1);
        dest.writeInteger(fileStart);
        dest.writeInteger(vctmKCAP.getSize());
        
        // write entries
        qstmKCAP.writeKCAP(dest, dataStream);

        long aligned = Utils.align(dest.getPosition() - start, 0x10);
        dest.setPosition(start + aligned);

        vctmKCAP.writeKCAP(dest, dataStream);
    }
    
    class TDTMEntry {
        short unknown1;
        short unknown2; // joint ID?
        int unknown3; // entry ID?
        
        public TDTMEntry() {
            // everything 0
        }
        
        public TDTMEntry(short unknown1, short unknown2, int unknown3) {
            this.unknown1 = unknown1;
            this.unknown2 = unknown2;
            this.unknown3 = unknown3;
        }
        
        public TDTMEntry(Access source) {
            this.unknown1 = source.readShort();
            this.unknown2 = source.readShort();
            this.unknown3 = source.readInteger();
        }
    }
}
