package de.phoenixstaffel.lostevotools.speakers;

import de.phoenixstaffel.decodetools.core.Access;

public class LE5AInstruction extends LEInstruction {

    public LE5AInstruction(Access source) {
        super(Instruction.U5A);
        
        source.readInteger();
        source.readInteger();
        
        int b = source.readInteger();
        
        if((b & 0x1) != 0) { //for(int i = 0; i < b; i++) {
            source.readInteger();
            source.readInteger();
        }
    }
    
}
