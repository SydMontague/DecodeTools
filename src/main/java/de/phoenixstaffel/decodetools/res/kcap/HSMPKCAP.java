package de.phoenixstaffel.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.VoidPayload;

/*
 * XTVP (mandatory, but can be VOID)
 * XDIP (mandatory, but can be VOID)
 * GMIP (optional)
 * HSEM (optional?)
 * PRGM (optional)
 * LRTM (optional?)
 * TNOJ (optional)
 * RTCL (optional)
 * TDTM (optional)
 */
public class HSMPKCAP extends AbstractKCAP {
    private static final int HSMP_VERSION = 0x100;
    // @formatter:off
    private static final KCAPType[] TYPE_ORDER = { KCAPType.XTVP, KCAPType.XDIP, KCAPType.GMIP, 
                                                   KCAPType.HSEM, KCAPType.PRGM, KCAPType.LRTM, 
                                                   KCAPType.TNOJ, KCAPType.RTCL, KCAPType.TDTM };
    // @formatter:on
    
    private float unknown2;
    private float unknown3;
    private float unknown4;
    private float unknown5;
    private float unknown6;
    private float unknown7;
    private float unknown8;
    private float scale;
    // int padding
    
    private String name;
    
    // TODO abstrahize, don't use KCAPs directly
    private XTVPKCAP xtvp = null;
    private XDIPKCAP xdip = null;
    private GMIPKCAP gmip;
    private HSEMKCAP hsem;
    private PRGMKCAP prgm;
    private LRTMKCAP lrtm;
    private TNOJKCAP tnoj;
    private RTCLKCAP rtcl;
    private TDTMKCAP tdtm;
    
    public HSMPKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // make sure it's actually a HSMP
        if (source.readInteger() != KCAPType.HSMP.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate HSMP KCAP, but didn't find a HSMP header.");
        
        source.readInteger(); // version, always 0x0100
        unknown2 = source.readFloat();
        unknown3 = source.readFloat();
        unknown4 = source.readFloat();
        unknown5 = source.readFloat();
        unknown6 = source.readFloat();
        unknown7 = source.readFloat();
        unknown8 = source.readFloat();
        scale = source.readFloat();
        source.readInteger(); // always 0
        name = source.readASCIIString();
        
        long diff = source.getPosition() - info.startAddress;
        diff = Utils.align(diff, 0x10) - diff;
        source.readByteArray(diff == 0 ? 0x10 : (int) diff); // padding
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        KCAPPointer xtvpPtr = pointer.get(0);
        
        if (xtvpPtr.getOffset() != 0 && xtvpPtr.getSize() != 0) {
            source.setPosition(info.startAddress + xtvpPtr.getOffset());
            ResPayload payload = ResPayload.craft(source, dataStart, this, xtvpPtr.getSize(), name);
            
            if (payload.getType() != Payload.KCAP || ((AbstractKCAP) payload).getKCAPType() != KCAPType.XTVP)
                throw new IllegalArgumentException("Tried to instanciate HSMP KCAP, but first child was not VOID or XTVP." + source.getPosition());
            
            xtvp = (XTVPKCAP) payload;
        }
        
        KCAPPointer xdipPtr = pointer.get(1);
        
        if (xdipPtr.getOffset() != 0 && xdipPtr.getSize() != 0) {
            source.setPosition(info.startAddress + xdipPtr.getOffset());
            ResPayload payload = ResPayload.craft(source, dataStart, this, xdipPtr.getSize(), name);
            
            if (payload.getType() != Payload.KCAP || ((AbstractKCAP) payload).getKCAPType() != KCAPType.XDIP)
                throw new IllegalArgumentException("Tried to instanciate HSMP KCAP, but first child was not VOID or XDIP.");
            
            xdip = (XDIPKCAP) payload;
        }
        
        int arrayPtr = 2;
        for (int i = 2; i < pointer.size(); i++) {
            KCAPPointer p = pointer.get(i);
            
            if (p.getOffset() == 0 && p.getSize() == 0)
                continue;
            
            source.setPosition(info.startAddress + p.getOffset());
            AbstractKCAP payload = AbstractKCAP.craftKCAP(source, this, dataStart);
            
            if (arrayPtr >= TYPE_ORDER.length)
                throw new IllegalArgumentException("HSMP KCAP still has unexpected child entries. Is the order messed up?");
            
            while (arrayPtr < TYPE_ORDER.length)
                if (payload.getKCAPType() == TYPE_ORDER[arrayPtr++]) {
                    setChild(payload);
                    break;
                }
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if (source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for HSMP KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
        
    }
    
    private void setChild(AbstractKCAP value) {
        if (value == null)
            throw new IllegalArgumentException("Value is null, but can't be null!");
        
        switch (value.getKCAPType()) {
            case XTVP:
                xtvp = (XTVPKCAP) value;
                break;
            case XDIP:
                xdip = (XDIPKCAP) value;
                break;
            case GMIP:
                gmip = (GMIPKCAP) value;
                break;
            case HSEM:
                hsem = (HSEMKCAP) value;
                break;
            case PRGM:
                prgm = (PRGMKCAP) value;
                break;
            case LRTM:
                lrtm = (LRTMKCAP) value;
                break;
            case TNOJ:
                tnoj = (TNOJKCAP) value;
                break;
            case RTCL:
                rtcl = (RTCLKCAP) value;
                break;
            case TDTM:
                tdtm = (TDTMKCAP) value;
                break;
            default:
                throw new IllegalArgumentException("This child type does not exist in a HSMP file: " + value.getKCAPType());
        }
    }
    
    @Override
    public List<ResPayload> getEntries() {
        List<ResPayload> list = new ArrayList<>();
        list.add(xtvp == null ? new VoidPayload(this) : xtvp);
        list.add(xdip == null ? new VoidPayload(this) : xdip);
        list.add(gmip);
        list.add(hsem);
        list.add(prgm);
        list.add(lrtm);
        list.add(tnoj);
        list.add(rtcl);
        list.add(tdtm == null && getParent() != null && getParent().getKCAPType() == KCAPType.XFEP && tnoj != null ? new VoidPayload(this) : tdtm);
        list.removeIf(Objects::isNull);
        return Collections.unmodifiableList(list);
    }
    
    @Override
    public ResPayload get(int i) {
        return getEntries().get(i);
    }
    
    @Override
    public int getEntryCount() {
        return getEntries().size();
    }
    
    @Override
    public KCAPType getKCAPType() {
        return KCAPType.HSMP;
    }
    
    @Override
    public int getSize() {
        int size = 0x4C;
        size += name.length() + 2;
        size = Utils.align(size, 0x10);
        size += getEntryCount() * 0x08;
        
        for (ResPayload entry : getEntries()) {
            if (entry.getType() == null) // VOID type, i.e. size 0 entries
                continue;
            
            size = Utils.align(size, 0x10); // align to specific alignment
            size += entry.getSize(); // size of entry
        }
        
        return size;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        long start = dest.getPosition();
        
        // KCAP head
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());
        
        dest.writeInteger(getEntryCount());
        dest.writeInteger(0x00); // type count, always 0 for this type
        dest.writeInteger(Utils.align(0x4C + name.length() + 2, 0x10)); // header size
        dest.writeInteger(0x00); // type payload start, always 0 for this type
        
        // HSMP head
        dest.writeInteger(getKCAPType().getMagicValue());
        dest.writeInteger(HSMP_VERSION);
        dest.writeFloat(unknown2);
        dest.writeFloat(unknown3);
        dest.writeFloat(unknown4);
        dest.writeFloat(unknown5);
        dest.writeFloat(unknown6);
        dest.writeFloat(unknown7);
        dest.writeFloat(unknown8);
        dest.writeFloat(scale);
        dest.writeInteger(0); // padding
        
        dest.writeString(name, "ASCII");
        dest.writeByte((byte) 0);
        
        long diff = dest.getPosition() - start;
        diff = Utils.align(diff, 0x10) - diff;
        dest.writeByteArray(new byte[diff == 0 ? 0x10 : (int) diff]); // padding
        
        // pointer table
        int fileStart = (int) Utils.align(dest.getPosition() - start, 0x10) + getEntryCount() * 0x08;
        int contentStart = fileStart;
        
        // write pointer table
        for (ResPayload entry : getEntries()) {
            fileStart = Utils.align(fileStart, 0x10); // align content start
            
            dest.writeInteger(entry.getType() == null ? 0 : fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        dest.setPosition(start + contentStart);
        
        for (ResPayload entry : getEntries()) {
            // align content start
            long aligned = Utils.align(dest.getPosition() - start, 0x10);
            dest.setPosition(start + aligned);
            
            // write content
            entry.writeKCAP(dest, dataStream);
        }
    }
    
    public void setXDIP(XDIPKCAP xdip) {
        this.xdip = xdip;
    }
    
    public void setXTVP(XTVPKCAP xtvp) {
        this.xtvp = xtvp;
    }
    
    public void setHSEM(HSEMKCAP hsem) {
        this.hsem = hsem;
    }

    public void setTNOJ(TNOJKCAP tnoj) {
        this.tnoj = tnoj;
    }
    
    public TNOJKCAP getTNOJ() {
        return tnoj;
    }
    
    public GMIPKCAP getGMIP() {
        return gmip;
    }
    
    public HSEMKCAP getHSEM() {
        return hsem;
    }
    
    @Override
    public String toString() {
        return getKCAPType().name() + " " + name;
    }
}
