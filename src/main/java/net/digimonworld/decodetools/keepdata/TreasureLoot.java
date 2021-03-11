package net.digimonworld.decodetools.keepdata;

import net.digimonworld.decodetools.core.Access;

public class TreasureLoot implements GenericKeepData {
    private int item;
    private float chance;
    
    public TreasureLoot(Access access) {
        this.item = access.readInteger();
        this.chance = access.readFloat();
    }
    
    public TreasureLoot(int item, float chance) {
        this.item = item;
        this.chance = chance;
    }
    
    public int getItem() {
        return item;
    }
    
    public void setItem(int item) {
        this.item = item;
    }
    
    public float getChance() {
        return chance;
    }
    
    public void setChance(float chance) {
        this.chance = chance;
    }
    
    @Override
    public void write(Access access) {
        access.writeInteger(item);
        access.writeFloat(chance);
    }
    
    @Override
    public int getSize() {
        return 8;
    }
}
