package net.digimonworld.decodetools.res.payload.qstm;

public enum Axis {
    X,
    Y,
    Z,
    W,
    NONE;
    
    public static Axis fromByte(byte b) {
        switch (b) {
            case 0:
                return X;
            case 1:
                return Y;
            case 2:
                return Z;
            case 3:
                return W;
            default:
                return NONE;
        }
    }

    public byte byteValue() {
        return (byte) ordinal();
    }
}