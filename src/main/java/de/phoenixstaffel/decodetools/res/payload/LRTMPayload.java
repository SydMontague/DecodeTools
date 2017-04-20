package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class LRTMPayload extends ResPayload {
    private int index;
    private short unknown1; //shading type? 4 = unshaded?
    private short unknown2; //???
    private int colorFilter; //?
    private short unknown4; //lighting size
    private short unknown5; //lighting point
    
    private short unknown6; //??? size
    private short unknown7; //??? pointer
    
    private byte[] data1; //more lighting  diffuse - specular - constant?
    private byte[] data2; //???
    
    public LRTMPayload(Access source, int dataStart, KCAPPayload parent, int size) {
        super(parent);
        
        index = source.readInteger();
        unknown1 = source.readShort();
        unknown2 = source.readShort();
        colorFilter = source.readInteger();
        unknown4 = source.readShort();
        unknown5 = source.readShort();
        
        unknown6 = source.readShort();
        unknown7 = source.readShort();
        source.readInteger(); // padding
        source.readInteger(); // padding
        source.readInteger(); // padding
        
        data1 = source.readByteArray(unknown4);
        data2 = source.readByteArray(unknown6);
    }
    
    @Override
    public int getSize() {
        return 0x20 + data1.length + data2.length;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.LRTM;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(index);
        dest.writeShort(unknown1);
        dest.writeShort(unknown2);
        dest.writeInteger(colorFilter);
        dest.writeShort(unknown4);
        dest.writeShort(unknown5);
        
        dest.writeShort(unknown6);
        dest.writeShort(unknown7);
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        
        dest.writeByteArray(data1);
        dest.writeByteArray(data2);
    }
}
