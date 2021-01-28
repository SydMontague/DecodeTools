package de.phoenixstaffel.decodetools.dataminer;

import de.phoenixstaffel.decodetools.core.Access;

public abstract class Miner {
    private Access source;
    private Structure structure;

    protected Miner(Access source, Structure structure) {
        this.source = source;
        this.structure = structure;
    }

    protected Object getValue(StructureElement element) {
        return element.getType().readFrom(source, element.getExtra());
    }
    
    public abstract String asCSV();
    
    public Access getSource() {
        return source;
    }
    
    public Structure getStructure() {
        return structure;
    }
}
