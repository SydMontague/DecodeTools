package net.digimonworld.decodetools.keepdata;

import java.util.ArrayList;
import java.util.List;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class EvoRequirement implements KeepData {
    private List<Requirement> requirements = new ArrayList<>(); 
    
    public EvoRequirement(Access access) {
        while (access.getPosition() < access.getSize())
            requirements.add(new Requirement(access));
    }
    
    public EvoRequirement(List<Requirement> requirements) {
        this.requirements = requirements;
    }
    
    @Override
    public GenericPayload toPayload() {
        byte[] buffer = new byte[requirements.size() * 0x0C];
        
        try(StreamAccess access = new StreamAccess(buffer)) {
            write(access);
        }
        
        return new GenericPayload(null, buffer);
    }
    
    public List<Requirement> getRequirements() {
        return requirements;
    }
    
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }
    
    private void write(Access access) {
        requirements.forEach(a -> a.write(access));
    }
    
    public static class Requirement {
        private SuperGroup level;
        private byte group;
        private Operator operator;
        private Comperator comperator;
        
        private Type type;
        private int value;
        
        public Requirement(Access source) {
            level = SuperGroup.valueOf(source.readByte());
            group = source.readByte();
            operator = Operator.valueOf(source.readByte());
            comperator = Comperator.valueOf(source.readByte());
            
            type = Type.valueOf(source.readInteger());
            value = source.readInteger();
        }
        
        public Requirement(SuperGroup level, byte group, Operator operator, Comperator comperator, Type type, int value) {
            this.level = level;
            this.group = group;
            this.operator = operator;
            this.comperator = comperator;
            this.type = type;
            this.value = value;
        }
        
        public void write(Access access) {
            access.writeByte(level.getValue());
            access.writeByte(group);
            access.writeByte(operator.getValue());
            access.writeByte(comperator.getValue());
            access.writeInteger(type.getValue());
            access.writeInteger(value);
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

        public SuperGroup getLevel() {
            return level;
        }

        public void setLevel(SuperGroup level) {
            this.level = level;
        }

        public byte getGroup() {
            return group;
        }

        public void setGroup(byte group) {
            this.group = group;
        }

        public Operator getOperator() {
            return operator;
        }

        public void setOperator(Operator operator) {
            this.operator = operator;
        }

        public Comperator getComperator() {
            return comperator;
        }

        public void setComperator(Comperator comperator) {
            this.comperator = comperator;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
    
    public enum Operator {
        QUOTA((byte) 0),
        AND((byte) 1),
        OR((byte) 2);
        
        private final byte value;
        
        private Operator(byte value) {
            this.value = value;
        }
        
        public byte getValue() {
            return value;
        }
        
        public static Operator valueOf(byte i) {
            for (Operator t : values())
                if (t.value == i)
                    return t;
                
            throw new IllegalArgumentException("Undefined Operator: " + i);
        }
    }
    
    public enum SuperGroup {
        QUOTA((byte) 0),
        MANDATORY((byte) 1),
        NORMAL((byte) 2),
        BONUS((byte) 3);
        
        private final byte value;
        
        private SuperGroup(byte value) {
            this.value = value;
        }
        
        public byte getValue() {
            return value;
        }
        
        public static SuperGroup valueOf(byte i) {
            for (SuperGroup t : values())
                if (t.value == i)
                    return t;
                
            throw new IllegalArgumentException("Undefined Level: " + i);
        }
    }
    
    public enum Comperator {
        QUOTA((byte) 0),
        EQUALS((byte) 1),
        GREATER_THAN((byte) 2),
        LESS_THAN((byte) 3),
        HIGHEST((byte) 4); // highest
        
        private final byte value;
        
        private Comperator(byte value) {
            this.value = value;
        }
        
        public byte getValue() {
            return value;
        }
        
        public static Comperator valueOf(byte i) {
            for (Comperator t : values())
                if (t.value == i)
                    return t;
                
            throw new IllegalArgumentException("Undefined Comperator: " + i);
        }
    }
    
    public enum Type {
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
        DIGIMEMORY(16),
        REINCARNATED(17),
        TRIGGER(19),
        EVO_ITEM(21),
        DECODE_LEVEL(22);
        
        private final int value;
        
        private Type(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        public static Type valueOf(int i) {
            for (Type t : values())
                if (t.value == i)
                    return t;
                
            throw new IllegalArgumentException("Undefined Type: " + i);
        }
    }
}
