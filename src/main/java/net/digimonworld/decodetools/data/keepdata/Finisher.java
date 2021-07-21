package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.data.DecodeData;
import net.digimonworld.decodetools.data.keepdata.enums.Special;
import net.digimonworld.decodetools.data.keepdata.enums.Status;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class Finisher implements DecodeData {
    private short unk1;
    private Special type;
    private int unk2;
    private short unk3;
    private Status effect;
    private int unk4;
    private int power;
    private int unk5;
    private int unk6;
    private int unk7;
    private short chance;
    private short range;
    private float damageMod;
    
    public Finisher(Access access) {
        this.unk1 = access.readShort();
        this.type = Special.values()[access.readShort()];
        this.unk2 = access.readInteger();
        this.unk3 = access.readShort();
        this.effect = Status.values()[access.readShort()];
        this.unk4 = access.readInteger();
        this.power = access.readInteger();
        this.unk5 = access.readInteger();
        this.unk6 = access.readInteger();
        this.unk7 = access.readInteger();
        this.chance = access.readShort();
        this.range = access.readShort();
        this.damageMod = access.readFloat();
    }
    
    @Override
    public GenericPayload toPayload() {
        byte[] buffer = new byte[0x28];
        
        try(StreamAccess access = new StreamAccess(buffer)) {
            write(access);
        }
        
        return new GenericPayload(null, buffer);
    }
    
    private void write(Access access) {
        access.writeShort(this.unk1);
        access.writeShort((short) this.type.ordinal());
        access.writeInteger(this.unk2);
        access.writeShort(this.unk3);
        access.writeShort((short) this.effect.ordinal());
        access.writeInteger(this.unk4);
        access.writeInteger(this.power);
        access.writeInteger(this.unk5);
        access.writeInteger(this.unk6);
        access.writeInteger(this.unk7);
        access.writeShort(this.chance);
        access.writeShort(this.range);
        access.writeFloat(this.damageMod);
    }

    public short getUnk1() {
        return unk1;
    }

    public void setUnk1(short unk1) {
        this.unk1 = unk1;
    }

    public Special getType() {
        return type;
    }

    public void setType(Special type) {
        this.type = type;
    }

    public int getUnk2() {
        return unk2;
    }

    public void setUnk2(int unk2) {
        this.unk2 = unk2;
    }

    public short getUnk3() {
        return unk3;
    }

    public void setUnk3(short unk3) {
        this.unk3 = unk3;
    }

    public Status getEffect() {
        return effect;
    }

    public void setEffect(Status effect) {
        this.effect = effect;
    }

    public int getUnk4() {
        return unk4;
    }

    public void setUnk4(int unk4) {
        this.unk4 = unk4;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getUnk5() {
        return unk5;
    }

    public void setUnk5(int unk5) {
        this.unk5 = unk5;
    }

    public int getUnk6() {
        return unk6;
    }

    public void setUnk6(int unk6) {
        this.unk6 = unk6;
    }

    public int getUnk7() {
        return unk7;
    }

    public void setUnk7(int unk7) {
        this.unk7 = unk7;
    }

    public short getChance() {
        return chance;
    }

    public void setChance(short chance) {
        this.chance = chance;
    }

    public short getRange() {
        return range;
    }

    public void setRange(short range) {
        this.range = range;
    }

    public float getDamageMod() {
        return damageMod;
    }

    public void setDamageMod(float damageMod) {
        this.damageMod = damageMod;
    }
}
