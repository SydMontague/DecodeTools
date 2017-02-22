package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

public class XFEPExtension implements HeaderExtension {
    private int unknown1;
    private short unknownNum1;
    private short unknownNum2;
    
    private int[] data1;
    
    private int[] data2;
    
    private String name;
    
    public XFEPExtension(Access source) {
        unknown1 = source.readInteger();
        unknownNum1 = source.readShort();
        unknownNum2 = source.readShort();
        
        data1 = new int[5];
        
        for (int i = 0; i < 5; i++)
            data1[i] = source.readInteger();
        
        data2 = new int[unknownNum2];
        for (int i = 0; i < unknownNum2; i++)
            data2[i] = source.readInteger();
        
        name = source.readASCIIString();
        
        // TODO dirty
        int size = 0x0C + data1.length * 4 + data2.length * 4 + name.length() + 1;
        size = Utils.getPadded(size, 16) - size;
        
        if (size == 0)
            size = 16;
        
        for (int i = 0; i < size; i++)
            source.readByte();
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new HeaderExtensionPayload() {
        };
    }
    
    @Override
    public Extensions getType() {
        return Extensions.XFEP;
    }
    
    @Override
    public int getSize() {
        return Utils.getPadded(0x0C + data1.length * 4 + data2.length * 4 + name.length() + 2, 16);
    }
    
    @Override
    public void writeKCAP(Access dest) {
        long start = dest.getPosition();
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(unknown1);
        dest.writeShort(unknownNum1);
        dest.writeShort((short) data2.length);
        
        for (int i : data1)
            dest.writeInteger(i);
        
        for (int i : data2)
            dest.writeInteger(i);
        
        dest.writeString(name, "ASCII");
        dest.setPosition(start + getSize());
    }
    
    @Override
    public int getContentAlignment(KCAPFile parent) {
        return 0x10;
    }
}
