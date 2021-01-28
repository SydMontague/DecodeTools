package de.phoenixstaffel.decodetools.dataminer;

import de.phoenixstaffel.decodetools.core.Access;

public class RDRequirements implements StructureClass {
    static int a = 1;
    
    private Access source;
    private long offset;
    private ListMiner.MapEntry entry;
    
    public RDRequirements(Access source, long offset, ListMiner.MapEntry entry) {
        this.source = source;
        this.offset = offset;
        this.entry = entry;
        
        load();
    }

    private void load() {
        source.setPosition(offset + entry.getOffset());
        
        System.out.println("ID " + a++ + " " + Long.toHexString(source.getPosition()));
        for(int i = 0; i < entry.getLength() / 12; i++)
            System.out.println(new Requirement(source));
        
        System.out.println();
    }
    
    /*-
     * Format:
     * LL GG OO CC RR RR RR RR XX XX XX XX
     * 
     * L - Level of Requirement
     *      00 - Quota
     *      01 - Mandatory
     *      02 - Normal
     *      03 - Bonus
     * G - Requirement Group ID
     * O - Operator, whether a group is AND (1) or OR (2). Always 0 for QUOTA
     * C - Comparison operation
     *      00 - Quota
     *      01 - =
     *      02 - >=
     *      03 - <=
     *      04 - Unknown, used for Rookies (-> highest stat comparison mode)
     * R - Requirement Type
     *      00 - Quota
     *      01 - HP
     *      02 - HP / 10
     *      03 - MP
     *      04 - MP / 10
     *      05 - Offense
     *      06 - Defense
     *      07 - Speed
     *      08 - Brains
     *      09 - Weight
     *      0A - Care
     *      0B - Happiness
     *      0C - Discipline
     *      0D - Battles
     *      0E - Techs
     *      0F - Digimon
     *      10 - Digimemory Equipped?
     *      11 - Reincarnated from?
     *      13 - Unlocked Trigger?
     *      15 - Evolution Item Type?
     *      16 - Decode Level
     * V - Value, used for comparison
     */
    class Requirement {
        private Level level;
        private int group;
        private Operator operator;
        private Comperator comperator;
        
        private Type type;
        private int value;
        
        public Requirement(Access source) {
            
            level = Level.valueOf(Byte.toUnsignedInt(source.readByte()));
            group = Byte.toUnsignedInt(source.readByte());
            operator = Operator.valueOf(source.readByte());
            comperator = Comperator.valueOf(Byte.toUnsignedInt(source.readByte()));
            
            type = Type.valueOf(source.readInteger());
            value = source.readInteger();
        }
        
        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            
            b.append(level).append(" ");
            b.append(group).append(" ");
            b.append(operator).append(" ");
            b.append(comperator).append(" ");
            b.append(type).append(" ");
            b.append(value);
            
            return b.toString();
        }
    }
    
    enum Operator {
        QUOTA(0),
        AND(1),
        OR(2);

        private final int value;

        private Operator(int value) {
            this.value = value;
        }
        
        public static Operator valueOf(int i) {
            for(Operator t : values())
                if(t.value == i)
                    return t;
            
            throw new IllegalArgumentException("Undefined Type: " + i);
        }
    }
    
    enum Level {
        QUOTA(0),
        MANDATORY(1),
        NORMAL(2),
        BONUS(3);
        
        private final int value;
        
        private Level(int value) {
            this.value = value;
        }
        
        public static Level valueOf(int i) {
            for(Level t : values())
                if(t.value == i)
                    return t;
            
            throw new IllegalArgumentException("Undefined Type: " + i);
        }
    }
    
    enum Comperator {
        QUOTA(0),
        EQUALS(1),
        GREATER_THAN(2),
        LESS_THAN(3),
        UNKNOWN_COM_4(4); // highest
        
        private final int value;
        
        private Comperator(int value) {
            this.value = value;
        }
        
        public static Comperator valueOf(int i) {
            for(Comperator t : values())
                if(t.value == i)
                    return t;
            
            throw new IllegalArgumentException("Undefined Type: " + i);
        }
    }
    
    enum Type {
        QUOTA(0),
        HP(1),
        HP_DIV10(2),
        MP(3),
        MP_DIV10(4),
        OFFENSE(5),
        DEFENSE(6),
        SPEED(7),
        BRAINS(8),
        WEIGHT(9),
        CARE(10),
        HAPPINESS(11),
        DISCIPLINE(12),
        BATTLES(13),
        TECHS(14),
        DIGIMON(15),
        UNKNOWN_TYPE_16(16), //Digimemory equipped
        UNKNOWN_TYPE_17(17), //Reincarnated from
        UNKNOWN_TYPE_19(19), //Unlocked Trigger?
        UNKNOWN_TYPE_21(21), //Evolution Item Type?
        DECODE(22);
        
        private final int value;
        
        private Type(int value) {
            this.value = value;
        }
        
        public static Type valueOf(int i) {
            for(Type t : values())
                if(t.value == i)
                    return t;
            
            throw new IllegalArgumentException("Undefined Type: " + i);
        }
    }
}
