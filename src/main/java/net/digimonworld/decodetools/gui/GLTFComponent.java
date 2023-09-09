package net.digimonworld.decodetools.gui;


public enum GLTFComponent {
    SIGNED_BYTE(5120),
    UNSIGNED_BYTE(5121),
    SIGNED_SHORT(5122),
    UNSIGNED_SHORT(5123),
    UNSIGNED_INT(5125),
    FLOAT(5126);

    private final int code;

    private GLTFComponent(int code) {
        this.code = code;
    }

    public int get() {
        return code;
    }
}
