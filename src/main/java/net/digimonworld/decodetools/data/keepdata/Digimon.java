package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.data.DecodeData;
import net.digimonworld.decodetools.data.keepdata.enums.Attribute;
import net.digimonworld.decodetools.data.keepdata.enums.Level;
import net.digimonworld.decodetools.data.keepdata.enums.Special;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class Digimon implements DecodeData{
    private short id;
    private short chrId;
    private short evoListPos;
    private Level level;
    private Attribute attribute;
    private float scale;
    private float combatSpeed;
    private Special special1;
    private Special special2;
    private Special special3;
    private byte unk4;
    private float unk5;
    private float unk6;
    private float initialY;
    private float minY;
    private float maxY;
    private float initialZ;
    private float minZ;
    private float maxZ;
    private float initialRotation;
    private float digiviceScale;
    private float unk7;
    private float unk8;
    private short unk9;
    private short unk10;
    private float unk11;
    private float unk12;
    private byte unk13;
    private byte unk14;
    private short unk15;
    private short unk16;
    private short unk17;
    private short unk18;
    private short unk19;
    private byte[] skills;
    private short finisher;
    private short unk37;
    private short unk38;
    private short unk39;
    private short unk40;
    private short unk41;
    private float unk42;
    private float unk43;
    private float unk44;
    private float unk45;
    private float unk46;
    private float unk47;
    private float unk48;
    private float unk49;
    private float unk50;
    
    public Digimon(Access access) {
        this.id = access.readShort();
        this.chrId = access.readShort();
        this.evoListPos = access.readShort();
        this.level = Level.values()[access.readByte()];
        this.attribute = Attribute.values()[access.readByte()];
        this.scale = access.readFloat();
        this.combatSpeed = access.readFloat();
        this.special1 = Special.values()[access.readByte()];
        this.special2 = Special.values()[access.readByte()];
        this.special3 = Special.values()[access.readByte()];
        this.unk4 = access.readByte();
        this.unk5 = access.readFloat();
        this.unk6 = access.readFloat();
        this.initialY = access.readFloat();
        this.minY = access.readFloat();
        this.maxY = access.readFloat();
        this.initialZ = access.readFloat();
        this.minZ = access.readFloat();
        this.maxZ = access.readFloat();
        this.initialRotation = access.readFloat();
        this.digiviceScale = access.readFloat();
        this.unk7 = access.readFloat();
        this.unk8 = access.readFloat();
        this.unk9 = access.readShort();
        this.unk10 = access.readShort();
        this.unk11 = access.readFloat();
        this.unk12 = access.readFloat();
        this.unk13 = access.readByte();
        this.unk14 = access.readByte();
        this.unk15 = access.readShort();
        this.unk16 = access.readShort();
        this.unk17 = access.readShort();
        this.unk18 = access.readShort();
        this.unk19 = access.readShort();
        this.skills = access.readByteArray(16);
        this.finisher = access.readShort();
        this.unk37 = access.readShort();
        this.unk38 = access.readShort();
        this.unk39 = access.readShort();
        this.unk40 = access.readShort();
        this.unk41 = access.readShort();
        this.unk42 = access.readFloat();
        this.unk43 = access.readFloat();
        this.unk44 = access.readFloat();
        this.unk45 = access.readFloat();
        this.unk46 = access.readFloat();
        this.unk47 = access.readFloat();
        this.unk48 = access.readFloat();
        this.unk49 = access.readFloat();
        this.unk50 = access.readFloat();
    }
    
    @Override
    public GenericPayload toPayload() {
        byte[] buffer = new byte[0x9C];
        
        try(StreamAccess access = new StreamAccess(buffer)) {
            write(access);
        }
        
        return new GenericPayload(null, buffer);
    }
    
    private void write(Access access) {
        access.writeShort(id);
        access.writeShort(chrId);
        access.writeShort(evoListPos);
        access.writeByte((byte) level.ordinal());
        access.writeByte((byte) attribute.ordinal());
        access.writeFloat(scale);
        access.writeFloat(combatSpeed);
        access.writeByte((byte) special1.ordinal());
        access.writeByte((byte) special2.ordinal());
        access.writeByte((byte) special3.ordinal());
        access.writeByte(unk4);
        access.writeFloat(unk5);
        access.writeFloat(unk6);
        access.writeFloat(initialY);
        access.writeFloat(minY);
        access.writeFloat(maxY);
        access.writeFloat(initialZ);
        access.writeFloat(minZ);
        access.writeFloat(maxZ);
        access.writeFloat(initialRotation);
        access.writeFloat(digiviceScale);
        access.writeFloat(unk7);
        access.writeFloat(unk8);
        access.writeShort(unk9);
        access.writeShort(unk10);
        access.writeFloat(unk11);
        access.writeFloat(unk12);
        access.writeByte(unk13);
        access.writeByte(unk14);
        access.writeShort(unk15);
        access.writeShort(unk16);
        access.writeShort(unk17);
        access.writeShort(unk18);
        access.writeShort(unk19);
        access.writeByteArray(skills);
        access.writeShort(finisher);
        access.writeShort(unk37);
        access.writeShort(unk38);
        access.writeShort(unk39);
        access.writeShort(unk40);
        access.writeShort(unk41);
        access.writeFloat(unk42);
        access.writeFloat(unk43);
        access.writeFloat(unk44);
        access.writeFloat(unk45);
        access.writeFloat(unk46);
        access.writeFloat(unk47);
        access.writeFloat(unk48);
        access.writeFloat(unk49);
        access.writeFloat(unk50);
    }
    
    public short getId() {
        return id;
    }
    
    public void setId(short id) {
        this.id = id;
    }
    
    public short getChrId() {
        return chrId;
    }
    
    public void setChrId(short chrId) {
        this.chrId = chrId;
    }
    
    public short getEvoListPos() {
        return evoListPos;
    }
    
    public void setEvoListPos(short evoListPos) {
        this.evoListPos = evoListPos;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public void setLevel(Level level) {
        this.level = level;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    
    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    public float getCombatSpeed() {
        return combatSpeed;
    }
    
    public void setCombatSpeed(float combatSpeed) {
        this.combatSpeed = combatSpeed;
    }
    
    public Special getSpecial1() {
        return special1;
    }
    
    public void setSpecial1(Special special1) {
        this.special1 = special1;
    }
    
    public Special getSpecial2() {
        return special2;
    }
    
    public void setSpecial2(Special special2) {
        this.special2 = special2;
    }
    
    public Special getSpecial3() {
        return special3;
    }
    
    public void setSpecial3(Special special3) {
        this.special3 = special3;
    }
    
    public byte getUnk4() {
        return unk4;
    }
    
    public void setUnk4(byte unk4) {
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
    
    public float getInitialY() {
        return initialY;
    }
    
    public void setInitialY(float initialY) {
        this.initialY = initialY;
    }
    
    public float getMinY() {
        return minY;
    }
    
    public void setMinY(float minY) {
        this.minY = minY;
    }
    
    public float getMaxY() {
        return maxY;
    }
    
    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }
    
    public float getInitialZ() {
        return initialZ;
    }
    
    public void setInitialZ(float initialZ) {
        this.initialZ = initialZ;
    }
    
    public float getMinZ() {
        return minZ;
    }
    
    public void setMinZ(float minZ) {
        this.minZ = minZ;
    }
    
    public float getMaxZ() {
        return maxZ;
    }
    
    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }
    
    public float getInitialRotation() {
        return initialRotation;
    }
    
    public void setInitialRotation(float initialRotation) {
        this.initialRotation = initialRotation;
    }
    
    public float getDigiviceScale() {
        return digiviceScale;
    }
    
    public void setDigiviceScale(float digiviceScale) {
        this.digiviceScale = digiviceScale;
    }
    
    public float getUnk7() {
        return unk7;
    }
    
    public void setUnk7(float unk7) {
        this.unk7 = unk7;
    }
    
    public float getUnk8() {
        return unk8;
    }
    
    public void setUnk8(float unk8) {
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
    
    public float getUnk11() {
        return unk11;
    }
    
    public void setUnk11(float unk11) {
        this.unk11 = unk11;
    }
    
    public float getUnk12() {
        return unk12;
    }
    
    public void setUnk12(float unk12) {
        this.unk12 = unk12;
    }
    
    public byte getUnk13() {
        return unk13;
    }
    
    public void setUnk13(byte unk13) {
        this.unk13 = unk13;
    }
    
    public byte getUnk14() {
        return unk14;
    }
    
    public void setUnk14(byte unk14) {
        this.unk14 = unk14;
    }
    
    public short getUnk15() {
        return unk15;
    }
    
    public void setUnk15(short unk15) {
        this.unk15 = unk15;
    }
    
    public short getUnk16() {
        return unk16;
    }
    
    public void setUnk16(short unk16) {
        this.unk16 = unk16;
    }
    
    public short getUnk17() {
        return unk17;
    }
    
    public void setUnk17(short unk17) {
        this.unk17 = unk17;
    }
    
    public short getUnk18() {
        return unk18;
    }
    
    public void setUnk18(short unk18) {
        this.unk18 = unk18;
    }
    
    public short getUnk19() {
        return unk19;
    }
    
    public void setUnk19(short unk19) {
        this.unk19 = unk19;
    }
    
    public byte[] getSkills() {
        return skills;
    }
    
    public void setSkills(byte[] skills) {
        this.skills = skills;
    }
    
    public short getFinisher() {
        return finisher;
    }
    
    public void setFinisher(short finisher) {
        this.finisher = finisher;
    }
    
    public short getUnk37() {
        return unk37;
    }
    
    public void setUnk37(short unk37) {
        this.unk37 = unk37;
    }
    
    public short getUnk38() {
        return unk38;
    }
    
    public void setUnk38(short unk38) {
        this.unk38 = unk38;
    }
    
    public short getUnk39() {
        return unk39;
    }
    
    public void setUnk39(short unk39) {
        this.unk39 = unk39;
    }
    
    public short getUnk40() {
        return unk40;
    }
    
    public void setUnk40(short unk40) {
        this.unk40 = unk40;
    }
    
    public short getUnk41() {
        return unk41;
    }
    
    public void setUnk41(short unk41) {
        this.unk41 = unk41;
    }
    
    public float getUnk42() {
        return unk42;
    }
    
    public void setUnk42(float unk42) {
        this.unk42 = unk42;
    }
    
    public float getUnk43() {
        return unk43;
    }
    
    public void setUnk43(float unk43) {
        this.unk43 = unk43;
    }
    
    public float getUnk44() {
        return unk44;
    }
    
    public void setUnk44(float unk44) {
        this.unk44 = unk44;
    }
    
    public float getUnk45() {
        return unk45;
    }
    
    public void setUnk45(float unk45) {
        this.unk45 = unk45;
    }
    
    public float getUnk46() {
        return unk46;
    }
    
    public void setUnk46(float unk46) {
        this.unk46 = unk46;
    }
    
    public float getUnk47() {
        return unk47;
    }
    
    public void setUnk47(float unk47) {
        this.unk47 = unk47;
    }
    
    public float getUnk48() {
        return unk48;
    }
    
    public void setUnk48(float unk48) {
        this.unk48 = unk48;
    }
    
    public float getUnk49() {
        return unk49;
    }
    
    public void setUnk49(float unk49) {
        this.unk49 = unk49;
    }
    
    public float getUnk50() {
        return unk50;
    }
    
    public void setUnk50(float unk50) {
        this.unk50 = unk50;
    }
}
