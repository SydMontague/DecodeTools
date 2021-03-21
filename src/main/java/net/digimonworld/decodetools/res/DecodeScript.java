package net.digimonworld.decodetools.res;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.FileAccess;
import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;
import net.digimonworld.decodetools.res.payload.GenericPayload;

/*
 * Very rough script decoder. Not ready for production.
 */
public class DecodeScript {
    
    /*
     * END
     * MOV <VALUE>
     * FPADD <VALUE>
     * LD.<TYPE> <ADDRESS>
     * FPLD.<TYPE> <OFFSET>
     * CVT.<TO>.<FROM>
     * STR.<TYPE>
     * MEMCOPY <COUNT>
     * FILLZ <COUNT>
     * ADD.<TYPE>
     * SUB.<TYPE>
     * MUL.<TYPE>
     * DIV.<TYPE>
     * UMOD
     * MOD
     * LSR
     * ASR
     * LSL
     * AND
     * XOR
     * OR
     * NEG.<TYPE>
     * NOT
     * UNK53
     * PADD.<TYPE> <VALUE>
     * LPADD.<TYPE> <VALUE>
     * EQ.<TYPE>
     * NE.<TYPE>
     * GT.<TYPE>
     * GTE.<TYPE>
     * LT.<TYPE>
     * LTE.<TYPE>
     * EQZ
     * J <OFFSET>
     * JZ <OFFSET
     * JNZ <OFFSET>
     * CALL <ADDRESS>
     * SWITCH <VALUE>
     * RET
     * NATIVECALL <ADDRESS>
     * ENDNZ
     * ALLOC <SIZE>
     * PUSHW
     * PUSHL
     * PUSH <COUNT>
     * POPW
     * POPL
     * POP <COUNT>
     * ADVANCE <VALUE>
     */
    
    private enum Instruction {
        END("END"),
        MOV_BYTE("MOV", ParamType.VAL8),
        MOV_SHORT("MOV", ParamType.VAL16),
        MOV_WORD("MOV", ParamType.VAL32),
        MOV_LONG("MOV", ParamType.VAL64),
        MVN_UBYTE("MOV", ParamType.VAL8),
        MVN_USHORT("MOV", ParamType.VAL16),
        MOV_WORD2("MOV", ParamType.VAL32),
        FPADD_WORD("FPADD", ParamType.VAL32),
        FPADD_BYTE("FPADD", ParamType.VAL8),
        LD_UBYTE("LD.UB", ParamType.VAL32),
        LD_USHORT("LD.UH", ParamType.VAL32),
        LD_WORD("LD.W", ParamType.VAL32),
        LD_LONG("LD.L", ParamType.VAL32),
        LD_BYTE("LD.B", ParamType.VAL32),
        LD_SHORT("LD.H", ParamType.VAL32),
        FPLD32_UBYTE("FPLD.UB", ParamType.VAL32),
        FPLD32_USHORT("FPLD.UH", ParamType.VAL32),
        FPLD32_WORD("FPLD.W", ParamType.VAL32),
        FPLD32_LONG("FPLD.L", ParamType.VAL32),
        FPLD32_BYTE("FPLD.B", ParamType.VAL32),
        FPLD32_SHORT("FPLD.H", ParamType.VAL32),
        FPLD8_UBYTE("FPLD.UB", ParamType.VAL8),
        FPLD8_USHORT("FPLD.UH", ParamType.VAL8),
        FPLD8_WORD("FPLD.W", ParamType.VAL8),
        FPLD8_LONG("FPLD.L", ParamType.VAL8),
        FPLD8_BYTE("FPLD.B", ParamType.VAL8),
        FPLD8_SHORT("FPLD.H", ParamType.VAL8),
        CVT_UB_W("CVT.UB.W"),
        CVT_UH_W("CVT.UH.W"),
        CVT_UW_W("CVT.UW.W"),
        CVT_L_W("CVT.L.W"),
        CVT_B_W("CVT.B.W"),
        CVT_H_W("CVT.H.W"),
        STR_UBYTE("STR.UB"),
        STR_USHORT("STR.UH"),
        STR_UINT("STR.UW"),
        STR_LONG("STR.L"),
        MEMCOPY32("MEMCOPY", ParamType.VAL32),
        MEMCOPY8("MEMCOPY", ParamType.VAL8),
        FILLZ_1("FILLZ 1"),
        FILLZ_2("FILLZ 2"),
        FILLZ_4("FILLZ 4"),
        FILLZ_8("FILLZ 8"),
        FILLZ_V32("FILLZ", ParamType.VAL32),
        FILLZ_V8("FILLZ", ParamType.VAL8),
        CVT_F_W("CVT.F.W"),
        CVT_D_W("CVT.D.W"),
        CVT_F_UW("CVT.F.UW"),
        CVT_D_UW("CVT.D.UW"),
        CVT_W_F("CVT.W.F"),
        CVT_D_F("CVT.D.F"),
        CVT_W_D("CVT.W.D"),
        CVT_F_D("CVT.F.D"),
        CVT_BOOL_W("CVT.BOOL.W"),
        CVT_BOOL_D("CVT.BOOL.D"),
        ADD_W("ADD.W"),
        ADD_F("ADD.F"),
        ADD_D("ADD.D"),
        SUB_W("SUB.W"),
        SUB_F("SUB.F"),
        SUB_D("SUB.D"),
        MUL_W("MUL.W"),
        MUL_W2("MUL.W"),
        MUL_F("MUL.F"),
        MUL_D("MUL.D"),
        DIV_UW("DIV.UW"),
        DIV_W("DIV.W"),
        DIV_F("DIV.F"),
        DIV_D("DIV.D"),
        UMOD("UMOD"),
        MOD("MOD"),
        LSR("LSR"),
        ASR("ASR"),
        LSL("LSL"),
        AND("AND"),
        XOR("XOR"),
        OR("OR"),
        NEG_INT("NEG.W"),
        NEG_FLOAT("NEG.F"),
        NEG_DOUBLE("NEG.D"),
        NOT("NOT"),
        UNK53("UNK53"),
        PADD_UBYTE("PADD.UB", ParamType.VAL8),
        PADD_USHORT("PADD.UH", ParamType.VAL8),
        PADD_UINT("PADD.UW", ParamType.VAL8),
        PADD_FLOAT("PADD.F", ParamType.VAL8),
        PADD_DOUBLE("PADD.D", ParamType.VAL8),
        PADD_BYTE("PADD.B", ParamType.VAL8),
        PADD_SHORT("PADD.H", ParamType.VAL8),
        PADD("PADD", ParamType.VAL32),
        LPADD_UBYTE("LPADD.UB", ParamType.VAL8),
        LPADD_USHORT("LPADD.UH", ParamType.VAL8),
        LPADD_UINT("LPADD.UW", ParamType.VAL8),
        LPADD_FLOAT("LPADD.F", ParamType.VAL8),
        LPADD_DOUBLE("LPADD.D", ParamType.VAL8),
        LPADD_BYTE("LPADD.B", ParamType.VAL8),
        LPADD_SHORT("LPADD.H", ParamType.VAL8),
        LPADD("LPADD", ParamType.VAL32),
        EQ_WORD("EQ.W"),
        EQ_FLOAT("EQ.F"),
        EQ_DOUBLE("EQ.D"),
        NE_WORD("NE.W"),
        NE_FLOAT("NE.F"),
        NE_DOUBLE("NE.D"),
        GT_WORD("GT.W"),
        GT_FLOAT("GT.F"),
        GT_DOUBLE("GT.D"),
        GTE_INT("GTE.W"),
        GTE_FLOAT("GTE.F"),
        GTE_DOUBLE("GTE.D"),
        GT_UINT("GT.UW"),
        GTE_UINT("GTE.UW"),
        LT_WORD("LT.W"),
        LT_FLOAT("LT.F"),
        LT_DOUBLE("LT.D"),
        LTE_INT("LTE.W"),
        LTE_FLOAT("LTE.F"),
        LTE_DOUBLE("LTE.D"),
        LT_UINT("LT.UW"),
        LTE_UINT("LTE.UW"),
        EQZ("EQZ"),
        J32("J", ParamType.VAL32),
        JZ32("JZ", ParamType.VAL32),
        JNZ32("JNT", ParamType.VAL32),
        J8("J", ParamType.VAL8),
        JZ8("JZ", ParamType.VAL8),
        JNZ8("JNZ", ParamType.VAL8),
        CALL("CALL", ParamType.VAL32),
        SWITCH("SWITCH", ParamType.VAL32),
        RET("RET"),
        NATIVECALL("NATIVECALL", ParamType.VAL32),
        ENDNZ("ENDNZ"),
        ALLOC32("ALLOC", ParamType.VAL32),
        ALLOC8("ALLOC", ParamType.VAL8),
        PUSHW("PUSHW"),
        PUSHL("PUSHL"),
        PUSH32("PUSH", ParamType.VAL32),
        PUSH8("PUSH", ParamType.VAL8),
        POPW("POPW"),
        POPL("POPL"),
        POP32("POP", ParamType.VAL32),
        POP8("POP", ParamType.VAL8),
        ADVANCE("ADVANCE", ParamType.VAL32);
        
        private final String code;
        private final ParamType paramType;
        
        private Instruction(String code) {
            this(code, ParamType.NONE);
        }
        
        private Instruction(String code, ParamType paramType) {
            this.code = code;
            this.paramType = paramType;
        }
    }
    
    private enum ParamType {
        NONE,
        VAL8,
        VAL16,
        VAL32,
        VAL64;
    }
    
    public static void disassemble(Access access) {
        // header 0x18
        // code
        // data?
        // string table?
        
        int magicValue = access.readInteger();
        int unk1 = access.readInteger();
        int unk2 = access.readInteger();
        int totalSize = access.readInteger(); // total size
        int codeSize = access.readInteger(); // code+data size?
        int stringSize = access.readInteger(); // string table size
        
        /*- 0x2CA3D8
        // after string table:
        // int - number of internal pointer
        //   int per entry, pointing to an internal address (starting from 0x00)
        // int - number of string pointer
        //   int per entry, pointing to a string address (starting from code+data+0x18)
        // int - number of native pointer | mapped at 0x4EB89C
        //   int function ID
        //   int data offset
        // int - number of lib.e/external script pointer -> 0x2F80B0
        //   int function ID 
        //   int data offset
        // int - number of exported functions | mapped at 0x4EB8A0
        //   int function ID
        //   int function Ptr
        // int - number of unknown
        */
        
        byte[] code = access.readByteArray(codeSize);
        byte[] string = access.readByteArray(stringSize);
        byte[] rest = access.readByteArray(totalSize - codeSize - stringSize - 0x18);
        
        StreamAccess codeAccess = new StreamAccess(code);
        StreamAccess restAccess = new StreamAccess(rest);
        
        Map<Integer, String> labelMap = new HashMap<>();
        
        int codePtr = restAccess.readInteger();
        for(int i = 0; i < codePtr; i++) {
            int offset = restAccess.readInteger();
            int initVal = codeAccess.readInteger(offset - 0x18L);
            codeAccess.writeInteger(initVal - 0x18, offset - 0x18L);
            
            labelMap.put(offset - 0x18, String.format("local_0x%X", initVal - 0x18L));
        }
        
        int stringPtr = restAccess.readInteger();
        for(int i = 0; i < stringPtr; i++) {
            int offset = restAccess.readInteger();
            int initVal = codeAccess.readInteger(offset - 0x18L);
            codeAccess.writeInteger(initVal + codeSize, offset - 0x18L);

            labelMap.put(offset - 0x18, String.format("string_0x%X", initVal + codeSize));
        }

        int nativePtr = restAccess.readInteger();
        for(int i = 0; i < nativePtr; i++) {
            int functionId = restAccess.readInteger();
            int offset = restAccess.readInteger();
            
            int functionAddress = map.get(functionId);

            int initVal = codeAccess.readInteger(offset - 0x18L);
            if(initVal != 0)
                System.out.println("Non-Zero Nativecall detected");
            
            codeAccess.writeInteger(functionAddress, offset - 0x18L);

            labelMap.put(offset - 0x18, String.format("native_0x%X", functionAddress));
        }

        int externalPtr = restAccess.readInteger();
        for(int i = 0; i < externalPtr; i++) {
            int functionId = restAccess.readInteger();
            int offset = restAccess.readInteger();
            
            int functionAddress = map.getOrDefault(functionId, -1);

            int initVal = codeAccess.readInteger(offset - 0x18L);
            if(initVal != 0)
                System.out.println("Non-Zero external call detected");
            
            codeAccess.writeInteger(functionAddress, offset - 0x18L);

            labelMap.put(offset - 0x18, String.format("external_%d", functionId));
        }
        int exportsPtr = restAccess.readInteger();
        for(int i = 0; i < exportsPtr; i++) {
            int functionId = restAccess.readInteger();
            int offset = restAccess.readInteger();
        }
        int unkPtr = restAccess.readInteger();
        for(int i = 0; i < unkPtr; i++) {
            int functionId = restAccess.readInteger();
            int offset = restAccess.readInteger();
        }
        
        if(restAccess.getPosition() != restAccess.getSize())
            System.out.println("Not at end");

        Instruction last = Instruction.RET;
        while (codeAccess.getPosition() < codeAccess.getSize()) {
            
            Instruction instr = Instruction.values()[Byte.toUnsignedInt(codeAccess.readByte())];
            
            if(instr == Instruction.END && last == Instruction.RET)
                break;
            
            last = instr;
            
            String label = labelMap.get((int) codeAccess.getPosition());
            
            if(label != null && instr.paramType != ParamType.VAL32)
                System.out.println("label found for non-32bit value");
            
            switch (instr.paramType) {
                case NONE:
                    System.out.println(Long.toHexString(codeAccess.getPosition() - 1) + " " + instr.code);
                    break;
                case VAL8:
                    System.out.println(Long.toHexString(codeAccess.getPosition() - 1) + " " + instr.code + " " + codeAccess.readByte());
                    break;
                case VAL16:
                    System.out.println(Long.toHexString(codeAccess.getPosition() - 1) + " " + instr.code + " " + codeAccess.readShort());
                    break;
                case VAL32:
                    int value = codeAccess.readInteger();
                    String str = label == null ? Integer.toString(value) : label;
                    
                    System.out.println(Long.toHexString(codeAccess.getPosition() - 5) + " " + instr.code + " " + str);
                    break;
                case VAL64:
                    System.out.println(Long.toHexString(codeAccess.getPosition() - 1) + " " + instr.code + " " + codeAccess.readLong());
                    break;
            }
        }
    }
    
    private static Map<Integer, Integer> map = new HashMap<>();
    
    public static void main(String[] args) throws IOException {
        try (Access acc = new FileAccess(new File("./code.bin"), true)) {
            for (ScriptFunction f : ScriptFunction.values()) {
                acc.setPosition(f.getOffset() - 0x100000L);
                
                for (int i = 0; i < f.getNumFunctions(); i++) {
                    int val = acc.readInteger();
                    map.put(f.getStartingId() + i, val);
                }
            }
        }
        
        try (Access acc = new FileAccess(new File("./Input/map/fie01.res"), true)) {
            AbstractKCAP kcap = (AbstractKCAP) ResPayload.craft(acc);
            GenericPayload pl = (GenericPayload) kcap.get(0);
            //GenericPayload pl = (GenericPayload) ResPayload.craft(acc);
            try (StreamAccess access = new StreamAccess(pl.getData())) {
                disassemble(access);
            }
        }
    }
    
    enum ScriptFunction {
        FUNCTIONS_1(1, 27, 0x4EB8C0),
        FUNCTIONS_1000(1000, 20, 0x4EB92C),
        FUNCTIONS_10000(10000, 6, 0x4EB97C),
        FUNCTIONS_20000(20000, 58, 0x4E70D8),
        FUNCTIONS_20300(20300, 34, 0x4E71C0),
        FUNCTIONS_20500(20500, 10, 0x4E7248),
        FUNCTIONS_20600(20600, 29, 0x4E7270),
        FUNCTIONS_20700(20700, 35, 0x4E72E4),
        FUNCTIONS_20800(20800, 55, 0x4E7370),
        FUNCTIONS_20900(20900, 11, 0x4E744C),
        FUNCTIONS_21000(21000, 20, 0x4E7478),
        FUNCTIONS_21200(21200, 59, 0x4E74C8),
        FUNCTIONS_21400(21400, 34, 0x4E75B4),
        FUNCTIONS_21500(21500, 13, 0x4E763C),
        FUNCTIONS_21900(21900, 1, 0x4E70C4),
        FUNCTIONS_22000(22000, 55, 0x4E65F0),
        FUNCTIONS_22200(22200, 6, 0x4E66CC),
        FUNCTIONS_22250(22250, 25, 0x4E66E4),
        FUNCTIONS_22300(22300, 61, 0x4E6748),
        FUNCTIONS_22400(22400, 15, 0x4E683C),
        FUNCTIONS_22500(22500, 17, 0x4E6878),
        FUNCTIONS_22600(22600, 29, 0x4E68BC),
        FUNCTIONS_22650(22650, 17, 0x4E6930),
        FUNCTIONS_22700(22700, 32, 0x4E6974),
        FUNCTIONS_22800(22800, 31, 0x4E69F4),
        FUNCTIONS_22900(22900, 15, 0x4E6A70),
        FUNCTIONS_23000(23000, 67, 0x4E6AAC),
        FUNCTIONS_23300(23300, 142, 0x4E6BB8),
        FUNCTIONS_23600(23600, 13, 0x4E6DF0),
        FUNCTIONS_23650(23650, 17, 0x4E6E24),
        FUNCTIONS_23700(23700, 22, 0x4E6E68),
        FUNCTIONS_24000(24000, 35, 0x4E6538),
        FUNCTIONS_25000(25000, 32, 0x4E7698),
        FUNCTIONS_26000(26000, 85, 0x4E6EE0),
        FUNCTIONS_26100(26100, 32, 0x4E7034);
        
        private final int startingId;
        private final int numFunctions;
        private final int offset;
        
        private ScriptFunction(int startingId, int numFunctions, int offset) {
            this.startingId = startingId;
            this.numFunctions = numFunctions;
            this.offset = offset;
        }
        
        public int getNumFunctions() {
            return numFunctions;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getStartingId() {
            return startingId;
        }
    }
}
