package de.phoenixstaffel.decodetools.dataminer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * TYPE: LIST
 * Offset: 000000
 * Element: <Class>
 * 
 * 
 * 
 * int - KCAP Magic Value
 * int - Unknown
 * int - Size
 * int - Unknown
 * int - Number of Entries
 * int - Unknown
 * int - Header Size?
 * int - Unknown
 */
public class ListMiner extends Miner {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private int magicValue;
    private int unknown1;
    private int size;
    private int unknown2;
    private int numEntries;
    private int unknown3;
    private int headerSize;
    private int unknown4;
    
    private List<MapEntry> pointer = new ArrayList<>();
    
    private List<StructureClass> entries = new ArrayList<>();
    
    public ListMiner(Access source, Structure structure) {
        super(source, structure);
        source.setPosition(structure.getOffset());
        
        magicValue = source.readInteger();
        unknown1 = source.readInteger();
        size = source.readInteger();
        unknown2 = source.readInteger();
        numEntries = source.readInteger();
        unknown3 = source.readInteger();
        headerSize = source.readInteger();
        unknown4 = source.readInteger();
        
        for (int i = 0; i < numEntries; i++)
            pointer.add(new MapEntry(source.readInteger(), source.readInteger()));
        
        Class<?> clazz;
        try {
            clazz = Class.forName(getStructure().getStructureClass());
            @SuppressWarnings("unchecked")
            Constructor<? extends StructureClass> c = (Constructor<? extends StructureClass>) clazz.getConstructor(Access.class,
                                                                                                                   long.class,
                                                                                                                   MapEntry.class);
            
            for(MapEntry entry : pointer)
                entries.add(c.newInstance(source, structure.getOffset(), entry));
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.log(Level.INFO, "Error while loading structure", e);
        }
        
    }
    
    @Override
    public String asCSV() {
        throw new UnsupportedOperationException("Not implemented, yet!");
    }
    
    class MapEntry {
        private final int offset;
        private final int length;
        
        public MapEntry(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getLength() {
            return length;
        }
    }
}
