package de.phoenixstaffel.decodetools.keepdata;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import de.phoenixstaffel.decodetools.core.Access;

public class MapEnemyData implements GenericKeepData {
    private String map;
    private int spawnPoint;
    private short digimonId1;
    private short digimonId2;
    private short digimonId3;
    private short digimonId4;
    private short digimonId5;
    private short digimonId6;
    private short digimonId7;
    private short digimonId8;
    private float weight1;
    private float weight2;
    private float weight3;
    private float weight4;
    private float weight5;
    private float weight6;
    private float weight7;
    private float weight8;
    private short unk1;
    private short unk2;
    private short unk3;
    private short unk4;
    private short unk5;
    private short unk6;
    private short unk7;
    private short unk8;
    private short unk9;
    private short unk10;
    private short unk11;
    private short unk12;
    private short unk13;
    private short unk14;
    private short unk15;
    private short unk16;
    private short unk17;
    private short unk18;
    private float engageDistance;
    private float huntDistance;
    private float fleeDistance;
    private float trackingDistance;
    
    public MapEnemyData(Access access) {
        this.map = access.readString(8, "ASCII");
        this.spawnPoint = access.readInteger();
        this.digimonId1 = access.readShort();
        this.digimonId2 = access.readShort();
        this.digimonId3 = access.readShort();
        this.digimonId4 = access.readShort();
        this.digimonId5 = access.readShort();
        this.digimonId6 = access.readShort();
        this.digimonId7 = access.readShort();
        this.digimonId8 = access.readShort();
        this.weight1 = access.readFloat();
        this.weight2 = access.readFloat();
        this.weight3 = access.readFloat();
        this.weight4 = access.readFloat();
        this.weight5 = access.readFloat();
        this.weight6 = access.readFloat();
        this.weight7 = access.readFloat();
        this.weight8 = access.readFloat();
        this.unk1 = access.readShort();
        this.unk2 = access.readShort();
        this.unk3 = access.readShort();
        this.unk4 = access.readShort();
        this.unk5 = access.readShort();
        this.unk6 = access.readShort();
        this.unk7 = access.readShort();
        this.unk8 = access.readShort();
        this.unk9 = access.readShort();
        this.unk10 = access.readShort();
        this.unk11 = access.readShort();
        this.unk12 = access.readShort();
        this.unk13 = access.readShort();
        this.unk14 = access.readShort();
        this.unk15 = access.readShort();
        this.unk16 = access.readShort();
        this.unk17 = access.readShort();
        this.unk18 = access.readShort();
        this.engageDistance = access.readFloat();
        this.huntDistance = access.readFloat();
        this.fleeDistance = access.readFloat();
        this.trackingDistance = access.readFloat();
    }
    
    @Override
    public void write(Access access) {
        access.writeByteArray(Arrays.copyOf(map.getBytes(StandardCharsets.US_ASCII), 8));
        access.writeInteger(spawnPoint);
        access.writeShort(digimonId1);
        access.writeShort(digimonId2);
        access.writeShort(digimonId3);
        access.writeShort(digimonId4);
        access.writeShort(digimonId5);
        access.writeShort(digimonId6);
        access.writeShort(digimonId7);
        access.writeShort(digimonId8);
        access.writeFloat(weight1);
        access.writeFloat(weight2);
        access.writeFloat(weight3);
        access.writeFloat(weight4);
        access.writeFloat(weight5);
        access.writeFloat(weight6);
        access.writeFloat(weight7);
        access.writeFloat(weight8);
        access.writeShort(unk1);
        access.writeShort(unk2);
        access.writeShort(unk3);
        access.writeShort(unk4);
        access.writeShort(unk5);
        access.writeShort(unk6);
        access.writeShort(unk7);
        access.writeShort(unk8);
        access.writeShort(unk9);
        access.writeShort(unk10);
        access.writeShort(unk11);
        access.writeShort(unk12);
        access.writeShort(unk13);
        access.writeShort(unk14);
        access.writeShort(unk15);
        access.writeShort(unk16);
        access.writeShort(unk17);
        access.writeShort(unk18);
        access.writeFloat(engageDistance);
        access.writeFloat(huntDistance);
        access.writeFloat(fleeDistance);
        access.writeFloat(trackingDistance);
    }
    
    @Override
    public int getSize() {
        return 0x70;
    }
    
    public String getMap() {
        return map;
    }
    
    public void setMap(String map) {
        this.map = map;
    }
    
    public int getSpawnPoint() {
        return spawnPoint;
    }
    
    public void setSpawnPoint(int spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
    
    public short getDigimonId1() {
        return digimonId1;
    }
    
    public void setDigimonId1(short digimonId1) {
        this.digimonId1 = digimonId1;
    }
    
    public short getDigimonId2() {
        return digimonId2;
    }
    
    public void setDigimonId2(short digimonId2) {
        this.digimonId2 = digimonId2;
    }
    
    public short getDigimonId3() {
        return digimonId3;
    }
    
    public void setDigimonId3(short digimonId3) {
        this.digimonId3 = digimonId3;
    }
    
    public short getDigimonId4() {
        return digimonId4;
    }
    
    public void setDigimonId4(short digimonId4) {
        this.digimonId4 = digimonId4;
    }
    
    public short getDigimonId5() {
        return digimonId5;
    }
    
    public void setDigimonId5(short digimonId5) {
        this.digimonId5 = digimonId5;
    }
    
    public short getDigimonId6() {
        return digimonId6;
    }
    
    public void setDigimonId6(short digimonId6) {
        this.digimonId6 = digimonId6;
    }
    
    public short getDigimonId7() {
        return digimonId7;
    }
    
    public void setDigimonId7(short digimonId7) {
        this.digimonId7 = digimonId7;
    }
    
    public short getDigimonId8() {
        return digimonId8;
    }
    
    public void setDigimonId8(short digimonId8) {
        this.digimonId8 = digimonId8;
    }
    
    public float getWeight1() {
        return weight1;
    }
    
    public void setWeight1(float weight1) {
        this.weight1 = weight1;
    }
    
    public float getWeight2() {
        return weight2;
    }
    
    public void setWeight2(float weight2) {
        this.weight2 = weight2;
    }
    
    public float getWeight3() {
        return weight3;
    }
    
    public void setWeight3(float weight3) {
        this.weight3 = weight3;
    }
    
    public float getWeight4() {
        return weight4;
    }
    
    public void setWeight4(float weight4) {
        this.weight4 = weight4;
    }
    
    public float getWeight5() {
        return weight5;
    }
    
    public void setWeight5(float weight5) {
        this.weight5 = weight5;
    }
    
    public float getWeight6() {
        return weight6;
    }
    
    public void setWeight6(float weight6) {
        this.weight6 = weight6;
    }
    
    public float getWeight7() {
        return weight7;
    }
    
    public void setWeight7(float weight7) {
        this.weight7 = weight7;
    }
    
    public float getWeight8() {
        return weight8;
    }
    
    public void setWeight8(float weight8) {
        this.weight8 = weight8;
    }
    
    public short getUnk1() {
        return unk1;
    }
    
    public void setUnk1(short unk1) {
        this.unk1 = unk1;
    }
    
    public short getUnk2() {
        return unk2;
    }
    
    public void setUnk2(short unk2) {
        this.unk2 = unk2;
    }
    
    public short getUnk3() {
        return unk3;
    }
    
    public void setUnk3(short unk3) {
        this.unk3 = unk3;
    }
    
    public short getUnk4() {
        return unk4;
    }
    
    public void setUnk4(short unk4) {
        this.unk4 = unk4;
    }
    
    public short getUnk5() {
        return unk5;
    }
    
    public void setUnk5(short unk5) {
        this.unk5 = unk5;
    }
    
    public short getUnk6() {
        return unk6;
    }
    
    public void setUnk6(short unk6) {
        this.unk6 = unk6;
    }
    
    public short getUnk7() {
        return unk7;
    }
    
    public void setUnk7(short unk7) {
        this.unk7 = unk7;
    }
    
    public short getUnk8() {
        return unk8;
    }
    
    public void setUnk8(short unk8) {
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
    
    public short getUnk11() {
        return unk11;
    }
    
    public void setUnk11(short unk11) {
        this.unk11 = unk11;
    }
    
    public short getUnk12() {
        return unk12;
    }
    
    public void setUnk12(short unk12) {
        this.unk12 = unk12;
    }
    
    public short getUnk13() {
        return unk13;
    }
    
    public void setUnk13(short unk13) {
        this.unk13 = unk13;
    }
    
    public short getUnk14() {
        return unk14;
    }
    
    public void setUnk14(short unk14) {
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
    
    public float getEngageDistance() {
        return engageDistance;
    }
    
    public void setEngageDistance(float engageDistance) {
        this.engageDistance = engageDistance;
    }
    
    public float getHuntDistance() {
        return huntDistance;
    }
    
    public void setHuntDistance(float huntDistance) {
        this.huntDistance = huntDistance;
    }
    
    public float getFleeDistance() {
        return fleeDistance;
    }
    
    public void setFleeDistance(float fleeDistance) {
        this.fleeDistance = fleeDistance;
    }
    
    public float getTrackingDistance() {
        return trackingDistance;
    }
    
    public void setTrackingDistance(float trackingDistance) {
        this.trackingDistance = trackingDistance;
    }
}
