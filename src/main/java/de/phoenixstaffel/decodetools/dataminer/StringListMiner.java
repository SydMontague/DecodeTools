package de.phoenixstaffel.decodetools.dataminer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import de.phoenixstaffel.decodetools.core.Access;

/*
 * TYPE: Map
 * 
 * read Offset
 * read Length
 * 
 * 
 * 
 */
public class StringListMiner extends Miner{
    
    public StringListMiner(Access source, Structure structure) {
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
            ByteBuffer buff = ByteBuffer.allocate(1000);
            
            while (buff.hasRemaining()) {

                short value = getSource().readShort();
                if (value != 0) {
                    buff.putShort(value);
                }
                else if (getSource().readShort() == 0) {
                    buff.flip();
                    b.append(new String(buff.array(), Charset.forName("UTF-16BE")).trim());
                    b.append("\n");
                    break;
                }
            }
        }
        
        return b.toString();
    }
}
