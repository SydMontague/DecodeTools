package net.digimonworld.decodetools.keepdata;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import net.digimonworld.decodetools.core.Access;

public class MapEnemyData implements GenericKeepData {
    private String map;
    private int spawnPoint;
    private MapEnemyEntry[] entries = new MapEnemyEntry[8];
    private short unk17;
    private short unk18;
    private float engageDistance;
    private float huntDistance;
    private float fleeDistance;
    private float trackingDistance;
    
    public MapEnemyData(Access access) {
        for(int i = 0; i < 8; i++)
            this.entries[i] = new MapEnemyEntry();
        
        this.map = access.readString(8, "ASCII");
        this.spawnPoint = access.readInteger();
        for(int i = 0; i < 8; i++)
            this.entries[i].enemyId = access.readShort();
        for(int i = 0; i < 8; i++)
            this.entries[i].weight = access.readFloat();
        for(int i = 0; i < 8; i++)
            this.entries[i].unk1 = access.readShort();
        for(int i = 0; i < 8; i++)
            this.entries[i].unk2 = access.readShort();
        
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
        
        for(MapEnemyEntry entry : entries)
            access.writeShort(entry.enemyId);
        for(MapEnemyEntry entry : entries)
            access.writeFloat(entry.weight);
        for(MapEnemyEntry entry : entries)
            access.writeShort(entry.unk1);
        for(MapEnemyEntry entry : entries)
            access.writeShort(entry.unk2);
        
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
    
    public MapEnemyEntry[] getEntries() {
        return entries;
    }
    
    public MapEnemyEntry getEntry(int index) {
        return entries[index];
    }

    public void setEntry(int index, MapEnemyEntry entry) {
        entries[index] = entry;
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
    
    public static class MapEnemyEntry {
        private short enemyId = 0;
        private float weight = 0f;
        private short unk1 = 0;
        private short unk2 = 0;
        
        public short getEnemyId() {
            return enemyId;
        }
        
        public void setEnemyId(short digimonId) {
            this.enemyId = digimonId;
        }
        
        public float getWeight() {
            return weight;
        }
        
        public void setWeight(float weight) {
            this.weight = weight;
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
    }
}
