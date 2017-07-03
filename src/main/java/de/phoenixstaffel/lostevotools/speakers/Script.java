package de.phoenixstaffel.lostevotools.speakers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.lostevotools.speakers.LEInstruction.Instruction;

public class Script {
    private static Map<Integer, Integer> speakerMap = new HashMap<>();
    
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 350; i++)
            try (FileAccess file = new FileAccess(new File("F:\\Lost Evo Hacking\\garbage\\file_" + i + ".bin"))) {
                //System.out.println("F:\\Lost Evo Hacking\\garbage\\file_" + i + ".bin");
                new Script(file);
            }
        
        speakerMap.forEach((a, b) -> System.out.println(a + "," + b));
    }
    
    public Script(FileAccess file) throws IOException {
        int end = (int) file.getSize();
        if (file.readInteger(0) == 0x68656164) { // head
            file.readInteger(); // head
            file.readInteger(); // start script
            end = file.readInteger(); // start unknown
            file.readInteger(); // end?
            
            file.readInteger(); // unknown
            file.readInteger(); // unknown
            file.readInteger(); // unknown
            file.readInteger(); // unknown
        }
        
        while (file.getPosition() < end) {
            LEInstruction instr = LEInstruction.craft(file);
            
            if (instr.getType() == Instruction.SHOW_TEXTBOX) {
                int speakerId = ((StaticLEInstruction) instr).getParam(1);
                int messageId = ((StaticLEInstruction) instr).getParam(2);
                
                if (speakerMap.containsKey(messageId) && speakerMap.get(messageId) != speakerId) {
                    // System.out.println("Message " + messageId + " got used multiple times by different speakers.");
                }
                speakerMap.put(messageId, speakerId);
            }
            /*
             * System.out.print(Integer.toHexString(instr.getType().getValue()));
             * if (instr.getType() == Instruction.SHOW_TEXTBOX)
             * System.out.print(" " + ((StaticLEInstruction) instr).getParam(1) + " " + ((StaticLEInstruction) instr).getParam(2));
             * System.out.println(" | " + Long.toHexString(file.getPosition()));
             */
        }
    }
}
