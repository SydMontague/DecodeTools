package net.digimonworld.decodetools.keepdata;

import net.digimonworld.decodetools.core.Access;

public class Item implements GenericKeepData {
    private ItemType type;
    private short unk2;
    private short stackSize;
    private short icon;
    private int unk8;
    private short buyPrice;
    private short sellPrice;
    private EffectType[] effect = new EffectType[6];
    // 2 byte padding
    private int[] effectValues = new int[6];
    byte unk30;
    byte unk31;
    // 2 byte padding
    
    public Item(Access access) {
        this.type = ItemType.values()[access.readShort()];
        this.unk2 = access.readShort();
        this.stackSize = access.readShort();
        this.icon = access.readShort();
        this.unk8 = access.readInteger();
        this.buyPrice = access.readShort();
        this.sellPrice = access.readShort();
        
        for (int i = 0; i < 6; i++)
            effect[i] = EffectType.getByValue(access.readByte());
        
        access.readShort(); // padding
        
        for (int i = 0; i < 6; i++)
            effectValues[i] = access.readInteger();
        
        this.unk30 = access.readByte();
        this.unk31 = access.readByte();
        
        access.readShort(); // padding
    }
    
    @Override
    public void write(Access buffer) {
        buffer.writeShort((short) type.ordinal());
        buffer.writeShort(unk2);
        buffer.writeShort(stackSize);
        buffer.writeShort(icon);
        buffer.writeInteger(unk8);
        buffer.writeShort(buyPrice);
        buffer.writeShort(sellPrice);
        for (EffectType eff : effect)
            buffer.writeByte(eff.getValue());
        buffer.writeShort((short) 0); // padding
        for (int val : effectValues)
            buffer.writeInteger(val);
        buffer.writeByte(unk30);
        buffer.writeByte(unk31);
        buffer.writeShort((short) 0); // padding
    }
    
    @Override
    public int getSize() {
        return 0x34;
    }
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
    
    public short getUnk2() {
        return unk2;
    }
    
    public void setUnk2(short unk2) {
        this.unk2 = unk2;
    }
    
    public short getStackSize() {
        return stackSize;
    }
    
    public void setStackSize(short stackSize) {
        this.stackSize = stackSize;
    }
    
    public short getIcon() {
        return icon;
    }
    
    public void setIcon(short icon) {
        this.icon = icon;
    }
    
    public int getUnk8() {
        return unk8;
    }
    
    public void setUnk8(int unk8) {
        this.unk8 = unk8;
    }
    
    public short getBuyPrice() {
        return buyPrice;
    }
    
    public void setBuyPrice(short buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public short getSellPrice() {
        return sellPrice;
    }
    
    public void setSellPrice(short sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    public EffectType[] getEffect() {
        return effect;
    }
    
    public void setEffect(EffectType[] effect) {
        this.effect = effect;
    }
    
    public int[] getEffectValues() {
        return effectValues;
    }
    
    public void setEffectValues(int[] effectValues) {
        this.effectValues = effectValues;
    }
    
    public byte getUnk30() {
        return unk30;
    }
    
    public void setUnk30(byte unk30) {
        this.unk30 = unk30;
    }
    
    public byte getUnk31() {
        return unk31;
    }
    
    public void setUnk31(byte unk31) {
        this.unk31 = unk31;
    }
    
    public enum ItemType {
        BASE,
        HEALING,
        MEDICINE,
        BUFF,
        CHIP,
        STANDARD,
        FOOD,
        EVOLUTION;
    }
    
    public enum EffectType {
        NONE(0),
        HEAL_HP(1),
        HEAL_MP(2),
        HEAL_STATUS(3),
        HEAL_COMA(4),
        SATURATION(5),
        PREVENT_STATUS(6),
        HEAL_INJURY(7),
        HEAL_SICKNESS(8),
        BUFF_OFF(9),
        BUFF_DEF(10),
        BUFF_SPD(11),
        
        BOOST_OFF(15),
        BOOST_DEF(16),
        BOOST_SPD(17),
        BOOST_BRN(18),
        BOOST_HP(19),
        BOOST_MP(20),
        ADD_LIFETIME(21),
        PORTA_POTTY(22),
        REST_PILLOW(23),
        ENEMY_BELL(24),
        AMULET_CHARM(25),
        AUTOPILOT(26),
        REDUCE_TIREDNESS(27),
        WEIGHT(28),
        REDUCE_LIFETIME(29),
        BOOST_TRAINING(30),
        BOOST_HAPPINESS(31),
        BOOST_DISCIPLINE(32),
        BOOST_ALL_STATS(33),
        CARROT_MULTIPLIER(34),
        RADISH_MULTIPLIER(35),
        SICKNESS_CHANCE(37),
        RECOVER_HP(38),
        DIGIPINE_MULTIPLIER(39),
        EVOLUTION_TARGET(40),
        LEVEL_REQUIREMENT(41),
        DIGIMON_REQUIREMENT(42),
        
        TEMP_BOOST_OFF(44),
        TEMP_BOOST_DEF(45),
        TEMP_BOOST_SPD(46),
        TEMP_DEBOOST_OFF(47),
        TEMP_DEBOOST_DEF(48),
        TEMP_DEBOOST_SPD(49),
        TEMP_BUFF_BLOCKRATE(50),
        BUFF_SPECIAL(51),
        DEBUFF_SPECIAL(52),
        BOOST_TIME(53),
        SEDATE(54),
        HEAL_MAX_HP(55),
        HEAL_MAX_MP(56);
        
        private final byte value;
        
        private EffectType(int value) {
            this.value = (byte) value;
        }
        
        public byte getValue() {
            return value;
        }
        
        public static EffectType getByValue(int value) {
            for (EffectType type : values())
                if (type.getValue() == value)
                    return type;
                
            return NONE;
        }
    }
}
