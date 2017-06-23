package de.phoenixstaffel.lostevotools.speakers;

import java.io.File;
import java.io.IOException;

import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.lostevotools.speakers.LEInstruction.Instruction;


public class Script {
    public static void main(String[] args) throws IOException {
        try (FileAccess file = new FileAccess(new File("F:\\Lost Evo Hacking\\garbage\\file_20.bin"))) {
            new Script(file);
        }
    }
    
    public Script(FileAccess file) throws IOException {
        
        file.readInteger(); //head
        file.readInteger(); //start script
        int end = file.readInteger(); //start unknown
        file.readInteger(); //end?

        file.readInteger(); //unknown
        file.readInteger(); //unknown
        file.readInteger(); //unknown
        file.readInteger(); //unknown
        
        while (file.getPosition() < end) {
            LEInstruction instr = LEInstruction.craft(file);
            System.out.print(Integer.toHexString(instr.getType().getValue()));
            if(instr.getType() == Instruction.SHOW_TEXTBOX)
                System.out.print(" " + ((StaticLEInstruction) instr).getParam(1) + " " + ((StaticLEInstruction) instr).getParam(2));
            System.out.println(" | " + Long.toHexString(file.getPosition()));
        }
    }
}
