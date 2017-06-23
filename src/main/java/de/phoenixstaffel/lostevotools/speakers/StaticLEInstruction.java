package de.phoenixstaffel.lostevotools.speakers;

import de.phoenixstaffel.decodetools.dataminer.FileAccess;

public class StaticLEInstruction extends LEInstruction {

    private int[] params;
    
    public StaticLEInstruction(FileAccess file, Instruction instr) {
        super(instr);
        
        params = new int[instr.getCount()];
        
        for(int i = 0; i < instr.getCount(); ++i)
            params[i] = file.readInteger();
    }
    
    public int getParam(int id) {
        return params[id];
    }
    
}
