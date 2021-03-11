package net.digimonworld.decodetools.res.payload.hsem;

public enum HSEMEntryType {
    DRAW(2),
    UNK03(3),
    JOINT(4),
    MATERIAL(5),
    TEXTURE(6),
    UNK07(7);
    
    private int id;
    
    private HSEMEntryType(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
}
