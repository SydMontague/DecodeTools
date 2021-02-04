package de.phoenixstaffel.decodetools.res;

import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

/**
 * Represents a ResPayload that can have a name, but doesn't have to be.
 */
public abstract class NameablePayload extends ResPayload {

    private String name;
    
    protected NameablePayload(AbstractKCAP parent, String name) {
        super(parent);
        this.name = name;
    }

    /**
     * Returns whether the payload instance has a name.
     * Having a name is defined as name being not null.
     */
    public boolean hasName() {
        return name != null;
    }
    
    /**
     * Gets the name of this payload instance or null if there is none.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this payload instance.
     * 
     * @param name the new name of this instance
     */
    public void setName(String name) {
        this.name = name.isEmpty() ? null : name;
    }

    @Override
    public String toString() {
        return hasName() ? getName() : getType().name();
    }
}
