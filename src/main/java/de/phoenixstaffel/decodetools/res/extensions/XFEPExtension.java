package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;

public class XFEPExtension implements HeaderExtension {
    
    private int magicValue = Extensions.XFEP.getMagicValue();
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
        
        for(int i = 0; i < 5; i++)
            data1[i] = source.readInteger();

        data2 = new int[unknownNum2];
        for(int i = 0; i < unknownNum2; i++)
            data2[i] = source.readInteger();
        
        name = source.readASCIIString();
        
        int size = 0x0C + data1.length * 4 + data2.length * 4 + name.length() + 1;
        size = Utils.getPadded(size, 16) - size;
        
        System.out.println(Long.toHexString(source.getPosition()) + " " + size);
        
        if(size == 0)
            size = 16;
        
        for(int i = 0; i < size; i++)
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
        int extra = (0x0C + data1.length * 4 + data2.length * 4 + name.length() + 1) % 0x10 == 0 ? 16 : 0;
        
        return Utils.getPadded(0x0C + data1.length * 4 + data2.length * 4 + name.length() + 1 + extra, 16);
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(unknown1);
        dest.writeShort(unknownNum1);
        dest.writeShort((short) data2.length);
        
        for(int i : data1)
            dest.writeInteger(i);
        
        for(int i : data2)
            dest.writeInteger(i);
        
        dest.writeString(name, "ASCII");
        
        int extra = (0x0C + data1.length * 4 + data2.length * 4 + name.length() + 1) % 0x10;
        extra = extra == 0 ? 17 : 16 - extra + 1;

        for(int i = 0; i < extra; i++)
            dest.writeByte((byte) 0);
    }
}
