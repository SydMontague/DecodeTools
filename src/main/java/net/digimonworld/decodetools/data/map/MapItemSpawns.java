package net.digimonworld.decodetools.data.map;

import java.util.ArrayList;
import java.util.List;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;

public class MapItemSpawns {
    private List<MapItemSpawn> spawns = new ArrayList<>();
    
    public MapItemSpawns(Access access) {
        int count = (int) (access.getSize() / 0x4C);
        
        for(int i = 0; i < count; i++)
            spawns.add(new MapItemSpawn(access));
    }
    
    public List<MapItemSpawn> getSpawns() {
        return spawns;
    }
    
    public byte[] toByteArray() {
        byte[] arr = new byte[spawns.size() * 0x4C];
        
        try(StreamAccess access = new StreamAccess(arr)) {
            spawns.forEach(a -> a.write(access));
        }
        
        return arr;
    }
    
    public class MapItemSpawn {
        private float posX;
        private float posY;
        private float posZ;
        
        // 8 item spawn slots
        private final ItemSpawn[] spawns = new ItemSpawn[] { new ItemSpawn(), new ItemSpawn(), new ItemSpawn(), new ItemSpawn(), new ItemSpawn(),
                new ItemSpawn(), new ItemSpawn(), new ItemSpawn() };
        
        public MapItemSpawn(Access access) {
            this.posX = access.readFloat();
            this.posY = access.readFloat();
            this.posZ = access.readFloat();
            
            for (ItemSpawn spawn : spawns) {
                spawn.setItemId(access.readInteger());
                spawn.setChance(access.readFloat());
            }
        }
        
        public void write(Access access) {
            access.writeFloat(posX);
            access.writeFloat(posY);
            access.writeFloat(posZ);

            for (ItemSpawn spawn : spawns) {
                access.writeInteger(spawn.getItemId());
                access.writeFloat(spawn.getChance());
            }
        }

        public float getPosX() {
            return posX;
        }
        
        public float getPosY() {
            return posY;
        }
        
        public float getPosZ() {
            return posZ;
        }
        
        public ItemSpawn[] getSpawns() {
            return spawns;
        }
    }
    
    public static class ItemSpawn {
        private int itemId = 0;
        private float chance = 0f;
        
        public float getChance() {
            return chance;
        }
        
        public int getItemId() {
            return itemId;
        }
        
        public void setChance(float chance) {
            this.chance = chance;
        }
        
        public void setItemId(int itemId) {
            this.itemId = itemId;
        }
    }
}
