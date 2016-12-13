package de.phoenixstaffel.decodetools.dataminer;

/*-
 * Read linear data sheets
 * -> Fixed order and size of the entry
 * 
 * Offset: <start address as hex> (no starting 0x !)
 * Entries: <number of entries>
 * Structure:
 *      Value: <Type>
 *      Name: 
 *          Type: String
 *          Length: 20 //in bytes
 *          Encoding: <String encoding>
 * 
 * <Identifier>: <Type>
 *      <Additional Info>: <value>   <-- optional, depending on the Type
 * 
 * Types:
 * byte, short, integer, long, float, double, String
 * 
 * array? reference to other structures?
 * 
 * 
 */
public class DataMiner extends Miner {
    public DataMiner(Access source, Structure structure) {
        super(source, structure);
    }
    
    @Override
    public String asCSV() {
        getSource().setPosition(getStructure().getOffset());
        
        StringBuilder b = new StringBuilder();
        
        getStructure().getStructureElements().forEach(a -> b.append(a.getIdentifier()).append(","));
        b.deleteCharAt(b.length() - 1);
        b.append("\n");
        
        for (int i = 0; i < getStructure().getNumEntries(); i++) {
            getStructure().getStructureElements().forEach(a -> b.append(getValue(a)).append(","));
            b.deleteCharAt(b.length() - 1);
            b.append("\n");
        }
        
        getSource().setPosition(0);
        return b.toString();
    }
    
}
