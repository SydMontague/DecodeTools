package net.digimonworld.decodetools.data.keepdata.enums;

public enum DropType {
    ITEM(1),
    KEY_ITEM(2),
    CARD(3),
    ACCESSORY(4);
    
    private final short value;
    
    private DropType(int value) {
        this.value = (short) value;
    }
    
    public short getValue() {
        return value;
    }
    
    public static DropType getByValue(int val) {
        for(DropType a : values())
            if(a.getValue() == val)
                return a;
        
        return null;
    }
}
