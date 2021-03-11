package net.digimonworld.decodetools.res.payload.hsem;

import net.digimonworld.decodetools.core.Access;

public interface HSEMEntry {
    
    public void writeKCAP(Access dest);
    
    public int getSize();
    
    public HSEMEntryType getHSEMType();
    
    public static HSEMEntry loadEntry(Access source) {
        short id = source.readShort();
        source.readShort(); // size
        
        switch (id) {
            case 2:
                return new HSEMDrawEntry(source);
            case 3:
                return new HSEM03Entry(source);
            case 4:
                return new HSEMJointEntry(source);
            case 5:
                return new HSEMMaterialEntry(source);
            case 6:
                return new HSEMTextureEntry(source);
            case 7:
                return new HSEM07Entry(source);
            default:
                throw new IllegalArgumentException("Unknown HSEMEntry: " + id + " at " + Long.toHexString(source.getPosition()));
        }
    }
}
