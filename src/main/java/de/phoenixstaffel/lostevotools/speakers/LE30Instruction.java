package de.phoenixstaffel.lostevotools.speakers;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class LE30Instruction extends LEInstruction {
    public LE30Instruction(Access source) {
        super(Instruction.U30);
        
        source.readInteger();
        int b = source.readInteger();
        
        for(int i = 0; i < b; i++) {
            source.readInteger();
            source.readInteger();
        }
    }
}
