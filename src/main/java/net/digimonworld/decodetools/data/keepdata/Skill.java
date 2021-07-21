package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.data.DecodeData;
import net.digimonworld.decodetools.data.keepdata.enums.MoveKind;
import net.digimonworld.decodetools.data.keepdata.enums.Special;
import net.digimonworld.decodetools.data.keepdata.enums.Status;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class Skill implements DecodeData {
    private short tier;
    private short mpCost;
    private short cooldown;
    private short unk1;
    private float unk0;
    private float learning;
    private Special special;
    private short range;
    private float unk2;
    private MoveKind kind;
    private int damage;
    private int unk3;
    private int unk4;
    private int unk5;
    private Status status;
    private short statusChance;
    private float unk6;
    
    public Skill(Access access) {
        this.tier = access.readShort();
        this.mpCost = access.readShort();
        this.cooldown = access.readShort();
        this.unk1 = access.readShort();
        this.unk0 = access.readFloat();
        this.learning = access.readFloat();
        this.special = Special.values()[access.readShort()];
        this.range = access.readShort();
        this.unk2 = access.readFloat();
        this.kind = MoveKind.values()[access.readInteger()];
        this.damage = access.readInteger();
        this.unk3 = access.readInteger();
        this.unk4 = access.readInteger();
        this.unk5 = access.readInteger();
        this.status = Status.values()[access.readShort()];
        this.statusChance = access.readShort();
        this.unk6 = access.readFloat();
    }
    
    @Override
    public GenericPayload toPayload() {
        byte[] buffer = new byte[0x34];
        
        try(StreamAccess access = new StreamAccess(buffer)) {
            write(access);
        }
        
        return new GenericPayload(null, buffer);
    }
    
    private void write(Access access) {
        access.writeShort(this.tier);
        access.writeShort(this.mpCost);
        access.writeShort(this.cooldown);
        access.writeShort(this.unk1);
        access.writeFloat(this.unk0);
        access.writeFloat(this.learning);
        access.writeShort((short) this.special.ordinal());
        access.writeShort(this.range);
        access.writeFloat(this.unk2);
        access.writeInteger(this.kind.ordinal());
        access.writeInteger(this.damage);
        access.writeInteger(this.unk3);
        access.writeInteger(this.unk4);
        access.writeInteger(this.unk5);
        access.writeShort((short) this.status.ordinal());
        access.writeShort(this.statusChance);
        access.writeFloat(this.unk6);
    }
    
    public short getTier() {
        return tier;
    }
    
    public void setTier(short tier) {
        this.tier = tier;
    }
    
    public short getMpCost() {
        return mpCost;
    }
    
    public void setMpCost(short mpCost) {
        this.mpCost = mpCost;
    }
    
    public short getCooldown() {
        return cooldown;
    }
    
    public void setCooldown(short cooldown) {
        this.cooldown = cooldown;
    }
    
    public short getUnk1() {
        return unk1;
    }
    
    public void setUnk1(short unk1) {
        this.unk1 = unk1;
    }
    
    public float getUnk0() {
        return unk0;
    }
    
    public void setUnk0(float unk0) {
        this.unk0 = unk0;
    }
    
    public float getLearning() {
        return learning;
    }
    
    public void setLearning(float learning) {
        this.learning = learning;
    }
    
    public Special getSpecial() {
        return special;
    }
    
    public void setSpecial(Special special) {
        this.special = special;
    }
    
    public short getRange() {
        return range;
    }
    
    public void setRange(short range) {
        this.range = range;
    }
    
    public float getUnk2() {
        return unk2;
    }
    
    public void setUnk2(float unk2) {
        this.unk2 = unk2;
    }
    
    public MoveKind getKind() {
        return kind;
    }
    
    public void setKind(MoveKind kind) {
        this.kind = kind;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public int getUnk3() {
        return unk3;
    }
    
    public void setUnk3(int unk3) {
        this.unk3 = unk3;
    }
    
    public int getUnk4() {
        return unk4;
    }
    
    public void setUnk4(int unk4) {
        this.unk4 = unk4;
    }
    
    public int getUnk5() {
        return unk5;
    }
    
    public void setUnk5(int unk5) {
        this.unk5 = unk5;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public short getStatusChance() {
        return statusChance;
    }
    
    public void setStatusChance(short statusChance) {
        this.statusChance = statusChance;
    }
    
    public float getUnk6() {
        return unk6;
    }
    
    public void setUnk6(float unk6) {
        this.unk6 = unk6;
    }
}
