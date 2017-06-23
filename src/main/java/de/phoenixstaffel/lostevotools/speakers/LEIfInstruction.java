package de.phoenixstaffel.lostevotools.speakers;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class LEIfInstruction extends LEInstruction {
    
    public LEIfInstruction(Access source) {
        super(Instruction.U2F);
        
        source.readInteger();
        source.readInteger();
        source.readInteger();
        
        while (true) {
            int value = source.readInteger();
            
            switch (value) {
                case 0x2F:
                    source.readInteger();
                    source.readInteger();
                    source.readInteger();
                    break;
                case 0x6:
                case 0xA:
                    source.readInteger();
                    break;
                default:
                    source.setPosition(source.getPosition() - 4);
                    return;
            }
        }
    }
    
}
