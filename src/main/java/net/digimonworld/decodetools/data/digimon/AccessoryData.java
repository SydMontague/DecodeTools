package net.digimonworld.decodetools.data.digimon;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.data.DecodeData;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class AccessoryData implements DecodeData {
    private int unk1;
    private int unk2;
    private float unk3;
    private float unk4;
    private float unk5;
    private int unk6;
    private int unk7;
    private float unk8;
    private float unk9;
    private float unk10;
    
    public AccessoryData(Access access) {
        this.unk1 = access.readInteger();
        this.unk2 = access.readInteger();
        this.unk3 = access.readFloat();
        this.unk4 = access.readFloat();
        this.unk5 = access.readFloat();
        this.unk6 = access.readInteger();
        this.unk7 = access.readInteger();
        this.unk8 = access.readFloat();
        this.unk9 = access.readFloat();
        this.unk10 = access.readFloat();
    }
    
    @Override
    public GenericPayload toPayload() {
        byte[] buffer = new byte[0x28];
        
        try (StreamAccess access = new StreamAccess(buffer)) {
            write(access);
        }
        
        return new GenericPayload(null, buffer);
    }
    
    public void write(Access access) {
        access.writeInteger(unk1);
        access.writeInteger(unk2);
        access.writeFloat(unk3);
        access.writeFloat(unk4);
        access.writeFloat(unk5);
        access.writeInteger(unk6);
        access.writeInteger(unk7);
        access.writeFloat(unk8);
        access.writeFloat(unk9);
        access.writeFloat(unk10);
    }
}