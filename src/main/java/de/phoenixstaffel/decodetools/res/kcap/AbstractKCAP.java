package de.phoenixstaffel.decodetools.res.kcap;

import java.util.List;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

public abstract class AbstractKCAP extends ResPayload {
    
    protected static final int VERSION = 1;
    
    private int unknown;
    
    protected AbstractKCAP(AbstractKCAP parent, int unknown) {
        super(null); //TODO super(parent);
        
        this.unknown = unknown;
    }
    
    public int getUnknown() {
        return unknown;
    }

    public abstract List<ResPayload> getEntries();
    
    public abstract ResPayload get(int i);
    
    public abstract int getEntryCount();
    
    public abstract KCAPType getKCAPType();

    @Override
    public Payload getType() {
        return Payload.KCAP;
    }
    
    enum KCAPType {
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
            for(KCAPType value : values())
                if(value.magicValue == val)
                    return value;
            
            return NONE;
        }
    }
    
    //TODO temporary method to test thigns, remove it
    public static AbstractKCAP craftKCAP(Access source, int dataStart,  KCAPPayload parent, int size, String name) {
        return craftKCAP(source, null, dataStart);
    }
    
    public static AbstractKCAP craftKCAP(Access source, AbstractKCAP parent, int dataStart) {
        long startAddress = source.getPosition();
        KCAPType extension = KCAPType.valueOf(source.readIntegerOffset(0x20));
        
        if(source.readInteger() != 0x5041434B)
            throw new IllegalArgumentException("Given Access does not point to a KCAP element.");
        
        if(source.readInteger() != VERSION)
            throw new IllegalArgumentException("The given KCAP is not of Version 1 and thus not supported.");
        
        KCAPInformation info = new KCAPInformation(startAddress, source);
        
        switch(extension) {
            case CTPP:
            case GMIP:
            case HSEM:
            case HSMP:
            case KPTF: 
            case LDMP:
            case LRTM:
            case LTMP:
            case MFTP:
            case PRGM:
            case RTCL:
            case TDTM:
            case TMEP:
            case TNOJ:
            case TREP:
            case XDIP:
            case XFEP:
            case XTVP:
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
}
