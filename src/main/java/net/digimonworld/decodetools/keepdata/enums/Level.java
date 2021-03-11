package net.digimonworld.decodetools.keepdata.enums;

public enum Level {
    NONE("None"),
    BABY1("Baby 1"),
    BABY2("Baby 2"),
    CHILD("Child"),
    ADULT("Adult"),
    PERFECT("Perfect"),
    ULTIMATE("Ultimate"),
    UNUSED("Unused");
    
    private final String displayName;
    
    private Level(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
