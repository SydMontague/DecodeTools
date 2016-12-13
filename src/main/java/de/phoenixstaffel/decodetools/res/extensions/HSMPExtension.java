package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;

public class HSMPExtension implements HeaderExtension {
    
    private int magicValue = Extensions.HSMP.getMagicValue();
    
    private int unknown1;
    private float unknown2;
    private float unknown3;
    private float unknown4;
    private float unknown5;
    private float unknown6;
    private float unknown7;
    private float unknown8;
    private float unknown9;
    private int unknown10; // padding?
    
    private String name;
    
    public HSMPExtension(Access source) {
        
        unknown1 = source.readInteger();
        unknown2 = source.readFloat();
        unknown3 = source.readFloat();
        unknown4 = source.readFloat();
        unknown5 = source.readFloat();
        unknown6 = source.readFloat();
        unknown7 = source.readFloat();
        unknown8 = source.readFloat();
        unknown9 = source.readFloat();
        unknown10 = source.readInteger();
        
        name = source.readString(0x14, "ASCII");
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new HeaderExtensionPayload() {
        };
    }
    @Override
    public Extensions getType() {
        return Extensions.HSMP;
    }

    @Override
    public int getSize() {
        return 0x40;
    }

    @Override
    public void writeKCAP(Access dest) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(unknown1);
        dest.writeFloat(unknown2);
        dest.writeFloat(unknown3);
        dest.writeFloat(unknown4);
        dest.writeFloat(unknown5);
        dest.writeFloat(unknown6);
        dest.writeFloat(unknown7);
        dest.writeFloat(unknown8);
        dest.writeFloat(unknown9);
        dest.writeInteger(unknown10);
        
        dest.writeString(name, "ASCII");
        
        for(int i = 0x14 - name.length(); i > 0; i--)
            dest.writeByte((byte) 0); 
    }
}
