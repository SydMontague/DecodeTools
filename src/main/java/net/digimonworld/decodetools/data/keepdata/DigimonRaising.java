package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.data.DecodeData;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class DigimonRaising implements DecodeData {
    private int sleepSchedule;
    private int favoriteFood;
    private byte energyCap;
    private byte trainingType;
    private short likedAreas;
    private float energyUsageMod;
    private short pooptimeMod;
    private short unk5;
    private short dislikedAreas;
    private short unk7;
    private short baseWeight;
    private short gainHP;
    private short gainMP;
    private short gainOFF;
    private short gainDEF;
    private short gainSPD;
    private short gainBRN;
    private short[] evolveFrom;
    private short[] evolveTo;
    
    public DigimonRaising(Access access) {
        this.sleepSchedule = access.readInteger();
        this.favoriteFood = access.readInteger();
        this.energyCap = access.readByte();
        this.trainingType = access.readByte();
        this.likedAreas = access.readShort();
        this.energyUsageMod = access.readFloat();
        this.pooptimeMod = access.readShort();
        this.unk5 = access.readShort();
        this.dislikedAreas = access.readShort();
        this.unk7 = access.readShort();
        this.baseWeight = access.readShort();
        this.gainHP = access.readShort();
        this.gainMP = access.readShort();
        this.gainOFF = access.readShort();
        this.gainDEF = access.readShort();
        this.gainSPD = access.readShort();
        this.gainBRN = access.readShort();
        this.evolveFrom = access.readShortArray(5);
        this.evolveTo = access.readShortArray(6);
    }
    
    @Override
    public GenericPayload toPayload() {
        byte[] buffer = new byte[0x3C];
        
        try(StreamAccess access = new StreamAccess(buffer)) {
            write(access);
        }
        
        return new GenericPayload(null, buffer);
    }
    
    private void write(Access access) {
        access.writeInteger(sleepSchedule);
        access.writeInteger(favoriteFood);
        access.writeByte(energyCap);
        access.writeByte(trainingType);
        access.writeShort(likedAreas);
        access.writeFloat(energyUsageMod);
        access.writeShort(pooptimeMod);
        access.writeShort(unk5);
        access.writeShort(dislikedAreas);
        access.writeShort(unk7);
        access.writeShort(baseWeight);
        access.writeShort(gainHP);
        access.writeShort(gainMP);
        access.writeShort(gainOFF);
        access.writeShort(gainDEF);
        access.writeShort(gainSPD);
        access.writeShort(gainBRN);
        access.writeShortArray(evolveFrom);
        access.writeShortArray(evolveTo);
    }
    
    public int getSleepSchedule() {
        return sleepSchedule;
    }
    
    public void setSleepSchedule(int sleepSchedule) {
        this.sleepSchedule = sleepSchedule;
    }
    
    public int getFavoriteFood() {
        return favoriteFood;
    }
    
    public void setFavoriteFood(int favoriteFood) {
        this.favoriteFood = favoriteFood;
    }
    
    public byte getEnergyCap() {
        return energyCap;
    }
    
    public void setEnergyCap(byte energyCap) {
        this.energyCap = energyCap;
    }
    
    public byte getTrainingType() {
        return trainingType;
    }
    
    public void setTrainingType(byte trainingType) {
        this.trainingType = trainingType;
    }
    
    public short getLikedAreas() {
        return likedAreas;
    }
    
    public void setLikedAreas(short likedAreas) {
        this.likedAreas = likedAreas;
    }
    
    public float getEnergyUsageMod() {
        return energyUsageMod;
    }
    
    public void setEnergyUsageMod(float energyUsageMod) {
        this.energyUsageMod = energyUsageMod;
    }
    
    public short getPooptimeMod() {
        return pooptimeMod;
    }
    
    public void setPooptimeMod(short pooptimeMod) {
        this.pooptimeMod = pooptimeMod;
    }
    
    public short getUnk5() {
        return unk5;
    }
    
    public void setUnk5(short unk5) {
        this.unk5 = unk5;
    }
    
    public short getDislikedAreas() {
        return dislikedAreas;
    }
    
    public void setDislikedAreas(short dislikedAreas) {
        this.dislikedAreas = dislikedAreas;
    }
    
    public short getUnk7() {
        return unk7;
    }
    
    public void setUnk7(short unk7) {
        this.unk7 = unk7;
    }
    
    public short getBaseWeight() {
        return baseWeight;
    }
    
    public void setBaseWeight(short baseWeight) {
        this.baseWeight = baseWeight;
    }
    
    public short getGainHP() {
        return gainHP;
    }
    
    public void setGainHP(short gainHP) {
        this.gainHP = gainHP;
    }
    
    public short getGainMP() {
        return gainMP;
    }
    
    public void setGainMP(short gainMP) {
        this.gainMP = gainMP;
    }
    
    public short getGainOFF() {
        return gainOFF;
    }
    
    public void setGainOFF(short gainOFF) {
        this.gainOFF = gainOFF;
    }
    
    public short getGainDEF() {
        return gainDEF;
    }
    
    public void setGainDEF(short gainDEF) {
        this.gainDEF = gainDEF;
    }
    
    public short getGainSPD() {
        return gainSPD;
    }
    
    public void setGainSPD(short gainSPD) {
        this.gainSPD = gainSPD;
    }
    
    public short getGainBRN() {
        return gainBRN;
    }
    
    public void setGainBRN(short gainBRN) {
        this.gainBRN = gainBRN;
    }
    
    public short[] getEvolveFrom() {
        return evolveFrom;
    }
    
    public void setEvolveFrom(short[] evolveFrom) {
        this.evolveFrom = evolveFrom;
    }
    
    public short[] getEvolveTo() {
        return evolveTo;
    }
    
    public void setEvolveTo(short[] evolveTo) {
        this.evolveTo = evolveTo;
    }
    
    public void setEvolveTo(int index, short evolveTo) {
        if(index < 0 || index >= 6)
            throw new IllegalArgumentException("Only index 0 to 5 are valid!");
        
        this.evolveTo[index] = evolveTo;
    }
    
    public boolean addEvolveTo(short to) {
        for(int i = 0; i < 6; i++) {
            if(this.evolveTo[i] == to)
                return true;
            
            if(this.evolveTo[i] == 0) {
                this.evolveTo[i] = to;
                return true;
            }
        }
        
        return false;
    }

    public short getEvolveTo(int index) {
        if(index < 0 || index >= 6)
            throw new IllegalArgumentException("Only index 0 to 5 are valid!");
        
        return evolveTo[index];
    }
    
    public void setEvolveFrom(int index, short evolveFrom) {
        if(index < 0 || index >= 5)
            throw new IllegalArgumentException("Only index 0 to 4 are valid!");
        
        this.evolveFrom[index] = evolveFrom;
    }
    
    
    public boolean addEvolveFrom(short from) {
        for(int i = 0; i < 6; i++) {
            if(this.evolveFrom[i] == from)
                return true;
            
            if(this.evolveFrom[i] == 0) {
                this.evolveFrom[i] = from;
                return true;
            }
        }
        
        return false;
    }

    public short getEvolveFrom(int index) {
        if(index < 0 || index >= 5)
            throw new IllegalArgumentException("Only index 0 to 4 are valid!");
        
        return evolveFrom[index];
    }
    
    public void setGains(int hp, int mp, int off, int def, int spd, int brn) {
        setGainHP((short) hp);
        setGainMP((short) mp);
        setGainOFF((short) off);
        setGainDEF((short) def);
        setGainSPD((short) spd);
        setGainBRN((short) brn);
    }
}
