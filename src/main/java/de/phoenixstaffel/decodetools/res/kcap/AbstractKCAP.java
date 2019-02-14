package de.phoenixstaffel.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.NameablePayload;
import de.phoenixstaffel.decodetools.res.ResPayload;

/*
 * TODO reduce code redundancy, especially with in reading/writing/size calculation
 * TODO unit tests
 */
public abstract class AbstractKCAP extends ResPayload implements Iterable<ResPayload> {
    
    protected static final int VERSION = 1;
    
    private int unknown;
    
    protected AbstractKCAP(AbstractKCAP parent, int unknown) {
        super(parent);
        
        this.unknown = unknown;
    }
    
    public int getUnknown() {
        return unknown;
    }
    
    /**
     * <p>
     * Returns an immutable List of all the entries this KCAP structure contains in their respective order.
     * While the list itself is immutable the entries are not, so any changes on them change the KCAP itself.
     * </p>
     * <p>
     * To add/remove/replace entries use the respective modification operations of the KCAP instance.
     * </p>
     * 
     * @return a immutable List of entries
     */
    public abstract List<ResPayload> getEntries();
    
    // TODO JavaDocs
    public abstract ResPayload get(int i);
    
    // TODO JavaDocs
    public abstract int getEntryCount();
    
    // TODO JavaDocs
    public abstract KCAPType getKCAPType();
    
    @Override
    public void fillDummyResData(DummyResData data) {
        getEntries().forEach(a -> a.fillDummyResData(data));
    }
    
    @Override
    public List<ResPayload> getElementsWithType(Payload type) {
        List<ResPayload> list = super.getElementsWithType(type);
        
        getEntries().forEach(a -> list.addAll(a.getElementsWithType(type)));
        
        return list;
    }
    
    @Override
    public Iterator<ResPayload> iterator() {
        return getEntries().iterator();
    }
    
    @Override
    public Payload getType() {
        return Payload.KCAP;
    }
    
    public enum KCAPType {
        CTPP(0x50505443),
        GMIP(0x50494D47),
        HSEM(0x4D455348),
        HSMP(0x504D5348),
        KPTF(0x4654504B),
        LDMP(0x504D444C),
        LRTM(0x4D54524C),
        LTMP(0x504D544C),
        MFTP(0x5054464D),
        PRGM(0x4D475250),
        RTCL(0x4C435452),
        TDTM(0x4D544454),
        TMEP(0x50454D54),
        TNOJ(0x4A4F4E54),
        TREP(0x50455254),
        XDIP(0x50494458),
        XFEP(0x50454658),
        XTVP(0x50565458),
        NONE(0x00000000);
        
        private int magicValue;
        
        private KCAPType(int magicValue) {
            this.magicValue = magicValue;
        }
        
        public static KCAPType valueOf(int val) {
            for (KCAPType value : values())
                if (value.magicValue == val)
                    return value;
                
            return NONE;
        }
        
        public int getMagicValue() {
            return magicValue;
        }
    }
    
    public static AbstractKCAP craftKCAP(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        return craftKCAP(source, parent, dataStart);
    }
    
    public static AbstractKCAP craftKCAP(Access source, AbstractKCAP parent, int dataStart) {
        long startAddress = source.getPosition();
        KCAPType extension = KCAPType.valueOf(source.readIntegerOffset(0x20));
        
        if (source.readInteger() != 0x5041434B)
            throw new IllegalArgumentException("Given Access does not point to a KCAP element.");
        
        if (source.readInteger() != VERSION)
            throw new IllegalArgumentException("The given KCAP is not of Version 1 and thus not supported.");
        
        KCAPInformation info = new KCAPInformation(startAddress, source);
        
        switch (extension) {
            case CTPP:
                return new CTPPKCAP(parent, source, dataStart, info);
            case GMIP:
                return new GMIPKCAP(parent, source, dataStart, info);
            case HSEM:
                return new HSEMKCAP(parent, source, dataStart, info);
            case HSMP:
                return new HSMPKCAP(parent, source, dataStart, info);
            case KPTF:
                return new KPTFKCAP(parent, source, dataStart, info);
            case LDMP:
                return new LDMPKCAP(parent, source, dataStart, info);
            case LRTM:
                return new LRTMKCAP(parent, source, dataStart, info);
            case LTMP:
                return new LTMPKCAP(parent, source, dataStart, info);
            case MFTP:
                return new MFTPKCAP(parent, source, dataStart, info);
            case PRGM:
                return new PRGMKCAP(parent, source, dataStart, info);
            case RTCL:
                return new RTCLKCAP(parent, source, dataStart, info);
            case TDTM:
                return new TDTMKCAP(parent, source, dataStart, info);
            case TMEP:
                return new TMEPKCAP(parent, source, dataStart, info);
            case TNOJ:
                return new TNOJKCAP(parent, source, dataStart, info);
            case TREP:
                return new TREPKCAP(parent, source, dataStart, info);
            case XDIP:
                return new XDIPKCAP(parent, source, dataStart, info);
            case XFEP:
                return new XFEPKCAP(parent, source, dataStart, info);
            case XTVP:
                return new XTVPKCAP(parent, source, dataStart, info);
            case NONE:
            default:
                return new NormalKCAP(parent, source, dataStart, info);
            
        }
        
    }
    
    static class KCAPInformation {
        long startAddress;
        
        int size;
        int flags;
        
        int entries;
        int typeEntries;
        int headerSize;
        int payloadStart;
        
        public KCAPInformation(long startAddress, Access source) {
            this.startAddress = startAddress;
            
            this.size = source.readInteger();
            this.flags = source.readInteger();
            
            this.entries = source.readInteger();
            this.typeEntries = source.readInteger();
            this.headerSize = source.readInteger();
            this.payloadStart = source.readInteger();
        }
    }
    
    static class KCAPPointer {
        private final int offset;
        private final int size;
        
        public KCAPPointer(int offset, int size) {
            this.offset = offset;
            this.size = size;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getSize() {
            return size;
        }
    }
    
    static class NamePointer {
        private final int offset;
        private final int id;
        
        public NamePointer(int offset, int id) {
            this.offset = offset;
            this.id = id;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getID() {
            return id;
        }
    }
    
    static List<KCAPPointer> loadKCAPPointer(Access source, int count) {
        List<KCAPPointer> pointer = new ArrayList<>();
        for (int i = 0; i < count; ++i)
            pointer.add(new KCAPPointer(source.readInteger(), source.readInteger()));
        
        return pointer;
    }
    
    static Map<Integer, String> loadNames(Access source, KCAPInformation info) {
        // make sure we're actually at the payload start
        long expectedPayloadStart = info.startAddress + info.payloadStart;
        if (info.payloadStart != 0 && source.getPosition() != expectedPayloadStart)
            throw new IllegalArgumentException("Tried started reading Payload at " + source.getPosition() + " but expected it to be " + expectedPayloadStart);
        
        // load the payload
        NamePointer[] namePointer = new NamePointer[info.typeEntries];
        for (int i = 0; i < info.typeEntries; ++i)
            namePointer[i] = new NamePointer(source.readInteger(), source.readInteger());
        
        Map<Integer, String> names = new HashMap<>();
        for (NamePointer name : namePointer) {
            long expectedStringStart = info.startAddress + name.getOffset();
            if (source.getPosition() != expectedStringStart)
                throw new IllegalArgumentException(
                        "Tried reading a String from " + source.getPosition() + " but expected it to be from " + expectedStringStart);
            
            names.put(name.getID(), source.readASCIIString());
        }
        return names;
    }
    
    static void writeNames(Access dest, int stringStart, List<? extends NameablePayload> entries) {
        Iterator<? extends NameablePayload> itr = entries.stream()
                                                         .filter(NameablePayload::hasName)
                                                         .sorted(Comparator.comparing(NameablePayload::getName))
                                                         .iterator();
        
        // write name table
        while (itr.hasNext()) {
            NameablePayload entry = itr.next();
            int id = entries.indexOf(entry);
            
            dest.writeInteger(stringStart);
            dest.writeInteger(id);
            stringStart += entry.getName().length() + 1;
        }
        
        // write names
        entries.stream().filter(NameablePayload::hasName).sorted(Comparator.comparing(NameablePayload::getName)).forEachOrdered(a -> {
            dest.writeString(a.getName(), "ASCII");
            dest.writeByte((byte) 0);
        });
    }
    
    @Override
    public String toString() {
        return getType().name() + " " + getKCAPType().name();
    }
}
