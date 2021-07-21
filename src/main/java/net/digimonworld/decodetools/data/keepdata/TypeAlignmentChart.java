package net.digimonworld.decodetools.data.keepdata;

import java.util.EnumMap;
import java.util.Map;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.data.keepdata.enums.Special;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class TypeAlignmentChart {
    
    public static final Special[] VALUES = new Special[] { Special.FIRE, Special.WATER, Special.AIR, Special.NATURE, Special.COMBAT, Special.LIGHT,
            Special.DARK, Special.MECH, Special.FILTH, Special.NEUTRAL };
    
    private static final int SIZE = 0x78;
    private Map<Special, Map<Special, Integer>> map = new EnumMap<>(Special.class);
    
    public TypeAlignmentChart(Access access) {
        for (Special attacker : VALUES) {
            Map<Special, Integer> localMap = map.computeIfAbsent(attacker, a -> new EnumMap<>(Special.class));
            for (Special victim : VALUES)
                localMap.put(victim, Byte.toUnsignedInt(access.readByte()));
            access.readShort(); // padding
        }
    }
    
    @SuppressWarnings("resource") // doesn't have to be closed
    public TypeAlignmentChart(GenericPayload genericPayload) {
        this(new StreamAccess(genericPayload.getData()));
    }
    
    public GenericPayload toPayload() {
        byte[] buffer = new byte[SIZE];
        
        try (StreamAccess access = new StreamAccess(buffer)) {
            map.forEach((a, b) -> {
                b.forEach((c, d) -> access.writeByte(d.byteValue()));
                access.writeShort((short) 0);
            });
        }
        
        return new GenericPayload(null, buffer);
    }

    public void set(Special attacker, Special victim, int value) {
        map.get(attacker).put(victim, value);
    }

    public byte get(Special attacker, Special victim) {
        return map.get(attacker).get(victim).byteValue();
    }
}
