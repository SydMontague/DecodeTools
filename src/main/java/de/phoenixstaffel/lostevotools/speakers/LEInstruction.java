package de.phoenixstaffel.lostevotools.speakers;

import de.phoenixstaffel.decodetools.dataminer.FileAccess;

public class LEInstruction {
    
    private Instruction ins;
    
    public Instruction getType() {
        return ins;
    }
    
    public LEInstruction(Instruction ins) {
        this.ins = ins;
    }
    
    public static LEInstruction craft(FileAccess file) {
        int instrCode = file.readInteger();
        Instruction instr;
        
        try {
            instr = Instruction.get(instrCode);
        }
        catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage() + " " + Long.toHexString(file.getPosition() - 4));
            throw e;
        }
        
        if (instr == Instruction.U2F)
            return new LEIfInstruction(file);
        else if (instr == Instruction.U5A)
            return new LE5AInstruction(file);
        
        return new StaticLEInstruction(file, instr);
    }
    
    // 209d020,r3=0x
    // 216B554
    enum Instruction {
        U01(0x01, 1), // unsure?
        U02(0x02, 1), // unsure?
        //U05(0x05, 1), // unverified
        U06(0x06, 1), // unsure?
        U07(0x07, 0),
        U0A(0x0A, 1), // unsure?
        U0B(0x0B, 1),
        U13(0x13, 2),
        U15(0x15, 3), // unsure?
        U16(0x16, 2),
        U1A(0x1A, 1),
        U29(0x29, 1),
        U2B(0x2B, 1),
        U2C(0x2C, 1),
        U2D(0x2D, 1),
        U2F(0x2F, 3), // Conditions?
        U32(0x32, 4), // Set Background
        U33(0x33, 1),
        U34(0x34, 4),
        U35(0x35, 11), // Spawn Actor
        U36(0x36, 3),
        U38(0x38, 0),
        U39(0x39, 0),
        U3A(0x3A, 0),
        U44(0x44, 4),
        U45(0x45, 1),
        //U46(0x46, 3), // unsure?
        U48(0x48, 4),
        U49(0x49, 1),
        U4A(0x4A, 5),
        U4B(0x4B, 6),
        U4D(0x4D, 5), // Walk To
        U4E(0x4E, 2),
        U4F(0x4F, 4), // Play Animation?
        U50(0x50, 2),
        U51(0x51, 3), // Emotion Bubble
        U52(0x52, 2),
        U53(0x53, 2),
        U56(0x56, 3),
        U57(0x57, 1),
        U58(0x58, 2),
        U5A(0x5A, 3),
        U5C(0x5C, 0),
        U5F(0x5F, 1),
        SHOW_TEXTBOX(0x5D, 3),
        U60(0x60, 0),
        U61(0x61, 0),
        U62(0x62, 0),
        U63(0x63, 2),
        U65(0x65, 5),
        U66(0x66, 1),
        U67(0x67, 1),
        U68(0x68, 1),
        U69(0x69, 0),
        U6E(0x6E, 7), // option box, has following bytes?
        U72(0x72, 4),
        U79(0x79, 2),
        U7C(0x7C, 2),
        U8A(0x8A, 0),
        U8E(0x8E, 2),
        U8F(0x8F, 2),
        U90(0x90, 1),
        U91(0x91, 0),
        U99(0x99, 2),
        U9D(0x9D, 1),
        U9E(0x9E, 0),
        UB0(0xB0, 0),
        UC0(0xC0, 2);
        
        private final int value;
        private final int count;
        
        private Instruction(int value, int count) {
            this.value = value;
            this.count = count;
        }
        
        public int getValue() {
            return value;
        }
        
        public static Instruction get(int value) {
            for (Instruction i : values())
                if (i.getValue() == value)
                    return i;
                
            throw new UnsupportedOperationException("No instruction for " + Integer.toHexString(value) + " found. ");
        }
        
        public int getCount() {
            return count;
        }
    }
    
}
