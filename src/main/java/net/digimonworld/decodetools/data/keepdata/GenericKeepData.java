package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;

public interface GenericKeepData {
    public void write(Access access);
    
    public int getSize();
}
