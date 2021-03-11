package net.digimonworld.decodetools.keepdata;

import net.digimonworld.decodetools.core.Access;

public interface GenericKeepData {
    public void write(Access access);
    
    public int getSize();
}
