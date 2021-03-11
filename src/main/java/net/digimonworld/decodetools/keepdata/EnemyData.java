package net.digimonworld.decodetools.keepdata;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.keepdata.enums.DropType;

public class EnemyData implements GenericKeepData {
    
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
    private final ItemDrop[] drops = new ItemDrop[] { new ItemDrop(), new ItemDrop(), new ItemDrop() };
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
        drops[0].type = DropType.getByValue(access.readShort());
        drops[1].type = DropType.getByValue(access.readShort());
        drops[2].type = DropType.getByValue(access.readShort());
        drops[0].id = access.readShort();
        drops[1].id = access.readShort();
        drops[2].id = access.readShort();
        drops[0].chance = access.readFloat();
        drops[1].chance = access.readFloat();
        drops[2].chance = access.readFloat();
        this.unk8 = access.readInteger();
        this.unk9 = access.readShort();
        this.unk10 = access.readShort();
        this.accessory = access.readShort();
        this.unk11 = access.readShort();
        this.unk12 = access.readInteger();
    }
    
    public EnemyData(short enemyId) {
        this.enemyId = enemyId;
    }
    
    public EnemyData(EnemyData other) {
        this.enemyId = other.getEnemyId();
        this.digimonId = other.getDigimonId();
        this.hp = other.getHp();
        this.mp = other.getMp();
        this.offense = other.getOffense();
        this.defense = other.getDefense();
        this.speed = other.getSpeed();
        this.brains = other.getBrains();
        this.unk1_1 = other.getUnk1_1();
        this.unk1_2 = other.getUnk1_2();
        this.unk2_1 = other.getUnk2_1();
        this.unk2_2 = other.getUnk2_2();
        this.unk3 = other.getUnk3();
        this.move1 = other.getMove1();
        this.move2 = other.getMove2();
        this.move3 = other.getMove3();
        this.unk4 = other.getUnk4();
        this.unk5 = other.getUnk5();
        this.unk6 = other.getUnk6();
        this.bits = other.getBits();
        this.decodeXP = other.getDecodeXP();
        this.drops[0].set(other.getDrop(0));
        this.drops[1].set(other.getDrop(1));
        this.drops[2].set(other.getDrop(2));
        this.unk8 = other.getUnk8();
        this.unk9 = other.getUnk9();
        this.unk10 = other.getUnk10();
        this.accessory = other.getAccessory();
        this.unk11 = other.getUnk11();
        this.unk12 = other.getUnk12();
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
        access.writeShort(drops[0].getType().getValue());
        access.writeShort(drops[1].getType().getValue());
        access.writeShort(drops[2].getType().getValue());
        access.writeShort(drops[0].getId());
        access.writeShort(drops[1].getId());
        access.writeShort(drops[2].getId());
        access.writeFloat(drops[0].getChance());
        access.writeFloat(drops[1].getChance());
        access.writeFloat(drops[2].getChance());
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

    public ItemDrop getDrop(int i) {
        return drops[i];
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
    
    public static class ItemDrop {
        private DropType type;
        private short id;
        private float chance;
        
        public void set(ItemDrop drop) {
            this.type = drop.getType();
            this.id = drop.getId();
            this.chance = drop.getChance();
        }
        
        public DropType getType() {
            return type;
        }
        
        public short getId() {
            return id;
        }
        
        public float getChance() {
            return chance;
        }
        
        public void setType(DropType type) {
            this.type = type;
        }
        
        public void setId(short id) {
            this.id = id;
        }
        
        public void setChance(float chance) {
            this.chance = chance;
        }
    }
}
