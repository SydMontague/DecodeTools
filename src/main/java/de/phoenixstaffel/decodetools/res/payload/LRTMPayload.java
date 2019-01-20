package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.NameablePayload;

//FIXME implements colors and shininess
public class LRTMPayload extends NameablePayload {
    private int index;
    private short unknown1; //shading type? 4 = unshaded?
    private short unknown2; //??? type?
    private int colorFilter; //?
    private short lightingSize; //lighting size
    private short lightingPointer; //lighting point
    
    private short unknownSize; //??? size
    private short unknownPointer; //??? pointer
    
    private byte[] data1; //more lighting  diffuse - specular - constant? always 0xC
    //ambient
    //specular
    //emit??
    private byte[] data2; //??? always 0x8
    //color
    //shininess?
    
    public LRTMPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        super(parent, name);
        
        index = source.readInteger();
        unknown1 = source.readShort();
        unknown2 = source.readShort();
        colorFilter = source.readInteger();
        lightingSize = source.readShort();
        lightingPointer = source.readShort();
        
        unknownSize = source.readShort();
        unknownPointer = source.readShort();
        
        source.readInteger(); // padding
        source.readInteger(); // padding
        source.readInteger(); // padding
        
        data1 = source.readByteArray(lightingSize);
        data2 = source.readByteArray(unknownSize);
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
        dest.writeShort(lightingSize);
        dest.writeShort(lightingPointer);
        
        dest.writeShort(unknownSize);
        dest.writeShort(unknownPointer);
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        
        dest.writeByteArray(data1);
        dest.writeByteArray(data2);
    }
}
