package net.digimonworld.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.payload.QSTMPayload;
import net.digimonworld.decodetools.res.payload.VCTMPayload;

//TODO figure out how TDTM works and abstrahize it per entry
// sets up the animation data, i.e. initial positions, which anim data to use and more
public class TDTMKCAP extends AbstractKCAP {
    private static final int TDTM_VERSION = 2;
    
    private List<TDTMEntry> tdtmEntry = new ArrayList<>();
    
    private List<QSTMPayload> qstm = new ArrayList<>();
    private List<VCTMPayload> vctm = new ArrayList<>();
    
    private float time1; // start
    private float time2; // end
    private float time3; // loop start
    private float time4; // loop end
    
    protected TDTMKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // make sure it's actually a TDTM
        if (source.readInteger() != KCAPType.TDTM.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate TDTM KCAP, but didn't find a TDTM header.");
        
        int version = source.readInteger();
        if (version != TDTM_VERSION)
            throw new IllegalArgumentException("Tried to instanciate TDTM KCAP and expected version 2, but got " + version);
        
        int numEntries = source.readInteger();
        source.readInteger(); // padding
        
        time1 = source.readFloat();
        time2 = source.readFloat();
        time3 = source.readFloat();
        time4 = source.readFloat();
        
        // one TDTM entry per QSTM in qstmKCAP?
        for (int i = 0; i < numEntries; ++i)
            tdtmEntry.add(new TDTMEntry(source));
        
        // TODO inconsistent by design?
        if (numEntries % 2 == 1 && info.headerSize % 0x10 == 0x00)
            source.readLong(); // padding
            
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        if (pointer.size() != 2)
            throw new IllegalArgumentException("A TDTM KCAP always has two elements, but this one has " + pointer.size() + "!");
        
        source.setPosition(info.startAddress + pointer.get(0).getOffset());
        NormalKCAP qstmKCAP = (NormalKCAP) AbstractKCAP.craftKCAP(source, this, dataStart);
        
        source.setPosition(info.startAddress + pointer.get(1).getOffset());
        NormalKCAP vctmKCAP = (NormalKCAP) AbstractKCAP.craftKCAP(source, this, dataStart);
        
        for (ResPayload entry : qstmKCAP) {
            if (entry.getType() != Payload.QSTM)
                throw new IllegalArgumentException("Got a " + entry.getType() + " entry, but only QSTM entries are allowed.");
            
            qstm.add((QSTMPayload) entry);
        }
        
        for (ResPayload entry : vctmKCAP) {
            if (entry.getType() != Payload.VCTM)
                throw new IllegalArgumentException("Got a " + entry.getType() + " entry, but only VCTM entries are allowed.");
            
            vctm.add((VCTMPayload) entry);
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if (source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for TDTM KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
    }
    
    @Override
    public List<ResPayload> getEntries() {
        List<ResPayload> entries = new ArrayList<>();
        entries.add(new NormalKCAP(this, qstm, true, false));
        entries.add(new NormalKCAP(this, vctm, true, false));
        
        return Collections.unmodifiableList(entries);
    }
    
    @Override
    public ResPayload get(int i) {
        switch (i) {
            case 0:
                return new NormalKCAP(this, qstm, true, false);
            case 1:
                return new NormalKCAP(this, vctm, true, false);
            default:
                throw new NoSuchElementException();
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
    public void writeKCAP(Access dest, ResData dataStream) {
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
        tdtmEntry.forEach(a -> a.writeKCAP(dest));
        
        if (tdtmEntry.size() % 2 == 1)
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
        try (ResData localDataStream = new ResData(dataStream)) {
            qstmKCAP.writeKCAP(dest, localDataStream);
            
            long aligned = Utils.align(dest.getPosition() - start, 0x10);
            dest.setPosition(start + aligned);
            
            vctmKCAP.writeKCAP(dest, localDataStream);
            dataStream.add(localDataStream);
        }
    }
    
    class TDTMEntry {
        private TDTMMode mode;
        
        /*
         * 0x10 joint    -> 3x translation | 4x rotation | 3x scale | 4x local scale
         * 0x20 texture  -> 2x translation | 1x rotation | 2x scale | none
         * 0x30 material -> 3x color (RGB) | 1x transparency
         */
        private byte unknown2;
        private short jointId;
        private int qstmId;
        
        public TDTMEntry(TDTMMode unknown1, byte unknown2, short jointId, int qstmId) {
            this.mode = unknown1;
            this.unknown2 = unknown2;
            this.jointId = jointId;
            this.qstmId = qstmId;
        }
        
        public TDTMEntry(Access source) {
            this.mode = TDTMMode.values()[source.readByte()];
            this.unknown2 = source.readByte();
            this.jointId = source.readShort();
            this.qstmId = source.readInteger();
        }
        
        public void writeKCAP(Access dest) {
            dest.writeByte((byte) mode.ordinal());
            dest.writeByte(unknown2);
            dest.writeShort(jointId);
            dest.writeInteger(qstmId);
        }
    }
    
    enum TDTMMode {
        TRANSLATION,
        ROTATION,
        SCALE,
        LOCAL_SCALE;
    }
}
