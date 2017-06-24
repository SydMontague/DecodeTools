package de.phoenixstaffel.lostevotools.speakers;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class LE31Instruction extends LEInstruction {
    public LE31Instruction(Access source) {
        super(Instruction.U31);
        
        source.readInteger();
        
        int b = source.readInteger();
        
        for(int i = 0; i < b; i++) {
            source.readInteger();
            source.readInteger();
        }
    }
}
