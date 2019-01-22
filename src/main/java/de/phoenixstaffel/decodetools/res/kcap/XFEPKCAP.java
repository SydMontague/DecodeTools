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

/*
 * TREP
 * GMIP
 * MFTP
 * TMEP
 * CTPP
 * LTMP
 * TDTM
 * LDMP
 * HSMP
 */
public class XFEPKCAP extends AbstractKCAP {
    private static final int HSMP_VERSION = 0x100;
    private static final KCAPType TYPE_ORDER[] = { KCAPType.TREP, KCAPType.GMIP, KCAPType.MFTP, 
                                                   KCAPType.TMEP, KCAPType.CTPP, KCAPType.LTMP, 
                                                   KCAPType.TDTM, KCAPType.LDMP, KCAPType.HSMP };
    
    private short unknown1;
    private short unknown2;
    
    private int[] unknownData1 = new int[5];
    private int[] unknownData2;

    private String name;
    
    private TREPKCAP trep;
    private GMIPKCAP gmip;
    private MFTPKCAP mftp;
    private TMEPKCAP tmep;
    private CTPPKCAP ctpp;
    private LTMPKCAP ltmp;
    private TDTMKCAP tdtm;
    private LDMPKCAP ldmp;
    private List<HSMPKCAP> hsmp = new ArrayList<>();
    
    protected XFEPKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // make sure it's actually a HSMP
        if (source.readInteger() != KCAPType.XFEP.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate XFEP KCAP, but didn't find a XFEP header.");
        
        source.readInteger(); // version, always 0x0100
        unknown1 = source.readShort();
        unknown2 = source.readShort();
        
        for(int i = 0; i < 5; i++)
            unknownData1[i] = source.readInteger();
        
        unknownData2 = new int[unknown2];
        for(int i = 0; i < unknown2; i++)
            unknownData2[i] = source.readInteger();

        name = source.readASCIIString();

        long diff = source.getPosition() - info.startAddress;
        diff = Utils.align(diff, 0x10) - diff;
        source.readByteArray(diff == 0 ? 0x10 : (int) diff); // padding
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        int arrayPtr = 0;
        int i = 0;
        for (; i < pointer.size() && arrayPtr < TYPE_ORDER.length - 1; i++) {
            KCAPPointer p = pointer.get(i);
            source.setPosition(info.startAddress + p.getOffset());
            AbstractKCAP payload = AbstractKCAP.craftKCAP(source, parent, dataStart);
            
            while(arrayPtr < TYPE_ORDER.length - 1)
                if(payload.getKCAPType() == TYPE_ORDER[arrayPtr++]) {
                    setChild(payload);
                    break;
                }
        }
        
        for(; i < pointer.size(); i++) {
            KCAPPointer p = pointer.get(i);
            source.setPosition(info.startAddress + p.getOffset());
            AbstractKCAP payload = AbstractKCAP.craftKCAP(source, parent, dataStart);
            
            if(payload.getKCAPType() != KCAPType.HSMP)
                throw new IllegalArgumentException("XFEP KCAP still has unexpected child entries. Is the order messed up?");
            
            hsmp.add((HSMPKCAP) payload);
        }
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if(source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for XFEP KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);

    }

    private void setChild(AbstractKCAP value) {
        switch(value.getKCAPType()) {
            case TREP:
                trep = (TREPKCAP) value;
                break;
            case GMIP:
                gmip = (GMIPKCAP) value;
                break;
            case MFTP:
                mftp = (MFTPKCAP) value;
                break;
            case TMEP:
                tmep = (TMEPKCAP) value;
                break;
            case CTPP:
                ctpp = (CTPPKCAP) value;
                break;
            case LTMP:
                ltmp = (LTMPKCAP) value;
                break;
            case TDTM:
                tdtm = (TDTMKCAP) value;
                break;
            case LDMP:
                ldmp = (LDMPKCAP) value;
                break;
            case HSMP:
            default:
                throw new IllegalArgumentException("This child type does not exist in a XFEP file: " + value.getKCAPType());
        }
    }
    
    @Override
    public List<ResPayload> getEntries() {
        List<ResPayload> list = new ArrayList<>();
        list.add(trep);
        list.add(gmip);
        list.add(mftp);
        list.add(tmep);
        list.add(ctpp);
        list.add(ltmp);
        list.add(tdtm);
        list.add(ldmp);
        list.addAll(hsmp);
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
        return KCAPType.XFEP;
    }
    
    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        // TODO Auto-generated method stub
        
    }
    
}
