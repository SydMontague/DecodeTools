package de.phoenixstaffel.decodetools.res.payload.hsem;

import de.phoenixstaffel.decodetools.core.Access;

//@formatter:off
/*
 * ID 2 -> 0x14 byte static //draw stuff?
 * ID 3 -> 0x04 byte + (0x05 * 4 byte) -> at least 8 bytes
 * ID 4 -> 0x08 byte + (0x06 * 4 byte)
 *          Joint assignment, (0x06) times
 *          mJoint register (short)
 *          joint ID
 * ID 5 -> 0x08 byte static
 *          Material Assignment
 *          unknown (short)
 *          materialId (short)
 * ID 6 -> 0x08 byte + (0x06 * 4 byte)
 *          Texture Assignment
 * ID 7 -> 0x0C byte static //something culling and transparency/render mode?
 */
// @formatter:on
public class HSEMEntry {
    private short id;
    private short size;
    
    private HSEMEntryPayload payload;
    
    public HSEMEntry(Access source) {
        id = source.readShort();
        size = source.readShort();
        
        payload = loadPayload(id, source);
    }
    
    public HSEMEntryPayload getPayload() {
        return payload;
    }
    
    public void writeKCAP(Access dest) {
        dest.writeShort(id);
        dest.writeShort(size);
        
        payload.writeKCAP(dest);
    }
    
    public int getSize() {
        return 0x4 + payload.getSize();
    }
    
    private static final HSEMEntryPayload loadPayload(short id, Access source) {
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
