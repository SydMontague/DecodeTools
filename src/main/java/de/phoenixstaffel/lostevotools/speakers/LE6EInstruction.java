package de.phoenixstaffel.lostevotools.speakers;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class LE6EInstruction extends LEInstruction {

    public LE6EInstruction(Access source) {
        super(Instruction.U6E);

        source.readInteger();
        source.readInteger();
        int b = source.readInteger();

        source.readInteger();
        source.readInteger();
        source.readInteger();
        source.readInteger();
        
        if((b & 0x7C0) != 0)
        {
            System.out.println("SPECIAL CASE!");
            source.readInteger();
            source.readInteger();
        }
    }
    
}
