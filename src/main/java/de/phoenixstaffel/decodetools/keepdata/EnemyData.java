package de.phoenixstaffel.decodetools.keepdata;

import de.phoenixstaffel.decodetools.core.Access;

public class EnemyData implements GenericKeepData  {
    
    private short enemyId;
    private short digimonId;
    private int hp;
    private int mp;
    private short offense;
    private short defense;
    private short speed;
    private short brains;
    private byte unk1_1;
    private byte unk1_2;
    private byte unk2_1;
    private byte unk2_2;
    private byte unk3;
    private byte move1;
    private byte move2;
    private byte move3;
    private float unk4;
    private float unk5;
    private float unk6;
    private short bits;
    private short decodeXP;
    private short dropType1;
    private short dropType2;
    private short dropType3;
    private short dropId1;
    private short dropId2;
    private short dropId3;
    private float dropChance1;
    private float dropChance2;
    private float dropChance3;
    private int unk8;
    private short unk9;
    private short unk10;
    private short accessory;
    private short unk11;
    private int unk12;
    
    public EnemyData(Access access) {
        this.enemyId = access.readShort();
        this.digimonId = access.readShort();
        this.hp = access.readInteger();
        this.mp = access.readInteger();
        this.offense = access.readShort();
        this.defense = access.readShort();
        this.speed = access.readShort();
        this.brains = access.readShort();
        this.unk1_1 = access.readByte();
        this.unk1_2 = access.readByte();
        this.unk2_1 = access.readByte();
        this.unk2_2 = access.readByte();
        this.unk3 = access.readByte();
        this.move1 = access.readByte();
        this.move2 = access.readByte();
        this.move3 = access.readByte();
        this.unk4 = access.readFloat();
        this.unk5 = access.readFloat();
        this.unk6 = access.readFloat();
        this.bits = access.readShort();
        this.decodeXP = access.readShort();
        this.dropType1 = access.readShort();
        this.dropType2 = access.readShort();
        this.dropType3 = access.readShort();
        this.dropId1 = access.readShort();
        this.dropId2 = access.readShort();
        this.dropId3 = access.readShort();
        this.dropChance1 = access.readFloat();
        this.dropChance2 = access.readFloat();
        this.dropChance3 = access.readFloat();
        this.unk8 = access.readInteger();
        this.unk9 = access.readShort();
        this.unk10 = access.readShort();
        this.accessory = access.readShort();
        this.unk11 = access.readShort();
        this.unk12 = access.readInteger();
    }

    @Override
    public void write(Access access) {
        access.writeShort(enemyId);
        access.writeShort(digimonId);
        access.writeInteger(hp);
        access.writeInteger(mp);
        access.writeShort(offense);
        access.writeShort(defense);
        access.writeShort(speed);
        access.writeShort(brains);
        access.writeByte(unk1_1);
        access.writeByte(unk1_2);
        access.writeByte(unk2_1);
        access.writeByte(unk2_2);
        access.writeByte(unk3);
        access.writeByte(move1);
        access.writeByte(move2);
        access.writeByte(move3);
        access.writeFloat(unk4);
        access.writeFloat(unk5);
        access.writeFloat(unk6);
        access.writeShort(bits);
        access.writeShort(decodeXP);
        access.writeShort(dropType1);
        access.writeShort(dropType2);
        access.writeShort(dropType3);
        access.writeShort(dropId1);
        access.writeShort(dropId2);
        access.writeShort(dropId3);
        access.writeFloat(dropChance1);
        access.writeFloat(dropChance2);
        access.writeFloat(dropChance3);
        access.writeInteger(unk8);
        access.writeShort(unk9);
        access.writeShort(unk10);
        access.writeShort(accessory);
        access.writeShort(unk11);
        access.writeInteger(unk12);        
    }

    @Override
    public int getSize() {
        return 0x54;
    }

    public short getEnemyId() {
        return enemyId;
    }

    public void setEnemyId(short enemyId) {
        this.enemyId = enemyId;
    }

    public short getDigimonId() {
        return digimonId;
    }

    public void setDigimonId(short digimonId) {
        this.digimonId = digimonId;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public short getOffense() {
        return offense;
    }

    public void setOffense(short offense) {
        this.offense = offense;
    }

    public short getDefense() {
        return defense;
    }

    public void setDefense(short defense) {
        this.defense = defense;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public short getBrains() {
        return brains;
    }

    public void setBrains(short brains) {
        this.brains = brains;
    }

    public byte getUnk1_1() {
        return unk1_1;
    }

    public void setUnk1_1(byte unk1_1) {
        this.unk1_1 = unk1_1;
    }

    public byte getUnk1_2() {
        return unk1_2;
    }

    public void setUnk1_2(byte unk1_2) {
        this.unk1_2 = unk1_2;
    }

    public byte getUnk2_1() {
        return unk2_1;
    }

    public void setUnk2_1(byte unk2_1) {
        this.unk2_1 = unk2_1;
    }

    public byte getUnk2_2() {
        return unk2_2;
    }

    public void setUnk2_2(byte unk2_2) {
        this.unk2_2 = unk2_2;
    }

    public byte getUnk3() {
        return unk3;
    }

    public void setUnk3(byte unk3) {
        this.unk3 = unk3;
    }

    public byte getMove1() {
        return move1;
    }

    public void setMove1(byte move1) {
        this.move1 = move1;
    }

    public byte getMove2() {
        return move2;
    }

    public void setMove2(byte move2) {
        this.move2 = move2;
    }

    public byte getMove3() {
        return move3;
    }

    public void setMove3(byte move3) {
        this.move3 = move3;
    }

    public float getUnk4() {
        return unk4;
    }

    public void setUnk4(float unk4) {
        this.unk4 = unk4;
    }

    public float getUnk5() {
        return unk5;
    }

    public void setUnk5(float unk5) {
        this.unk5 = unk5;
    }

    public float getUnk6() {
        return unk6;
    }

    public void setUnk6(float unk6) {
        this.unk6 = unk6;
    }

    public short getBits() {
        return bits;
    }

    public void setBits(short bits) {
        this.bits = bits;
    }

    public short getDecodeXP() {
        return decodeXP;
    }

    public void setDecodeXP(short decodeXP) {
        this.decodeXP = decodeXP;
    }

    public short getDropType1() {
        return dropType1;
    }

    public void setDropType1(short dropType1) {
        this.dropType1 = dropType1;
    }

    public short getDropType2() {
        return dropType2;
    }

    public void setDropType2(short dropType2) {
        this.dropType2 = dropType2;
    }

    public short getDropType3() {
        return dropType3;
    }

    public void setDropType3(short dropType3) {
        this.dropType3 = dropType3;
    }

    public short getDropId1() {
        return dropId1;
    }

    public void setDropId1(short dropId1) {
        this.dropId1 = dropId1;
    }

    public short getDropId2() {
        return dropId2;
    }

    public void setDropId2(short dropId2) {
        this.dropId2 = dropId2;
    }

    public short getDropId3() {
        return dropId3;
    }

    public void setDropId3(short dropId3) {
        this.dropId3 = dropId3;
    }

    public float getDropChance1() {
        return dropChance1;
    }

    public void setDropChance1(float dropChance1) {
        this.dropChance1 = dropChance1;
    }

    public float getDropChance2() {
        return dropChance2;
    }

    public void setDropChance2(float dropChance2) {
        this.dropChance2 = dropChance2;
    }

    public float getDropChance3() {
        return dropChance3;
    }

    public void setDropChance3(float dropChance3) {
        this.dropChance3 = dropChance3;
    }

    public int getUnk8() {
        return unk8;
    }

    public void setUnk8(int unk8) {
        this.unk8 = unk8;
    }

    public short getUnk9() {
        return unk9;
    }

    public void setUnk9(short unk9) {
        this.unk9 = unk9;
    }

    public short getUnk10() {
        return unk10;
    }

    public void setUnk10(short unk10) {
        this.unk10 = unk10;
    }

    public short getAccessory() {
        return accessory;
    }

    public void setAccessory(short accessory) {
        this.accessory = accessory;
    }

    public short getUnk11() {
        return unk11;
    }

    public void setUnk11(short unk11) {
        this.unk11 = unk11;
    }

    public int getUnk12() {
        return unk12;
    }

    public void setUnk12(int unk12) {
        this.unk12 = unk12;
    }
}
