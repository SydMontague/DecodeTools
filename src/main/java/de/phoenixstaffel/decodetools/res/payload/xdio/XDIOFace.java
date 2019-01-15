package de.phoenixstaffel.decodetools.res.payload.xdio;

import java.nio.ByteBuffer;

public class XDIOFace {
    private Integer vert1;
    private Integer vert2;
    private Integer vert3;
    
    public XDIOFace(Integer vert1, Integer vert2, Integer vert3) {
        this.vert1 = vert1;
        this.vert2 = vert2;
        this.vert3 = vert3;
    }
    
    public XDIOFace(ByteBuffer source, XDIOModes mode) {
        vert1 = mode.getReadFunction().apply(source);
        vert2 = mode.getReadFunction().apply(source);
        vert3 = mode.getReadFunction().apply(source);
    }
    
    public void write(ByteBuffer buff, XDIOModes mode) {
        buff.put(mode.getWriteFunction().apply(vert1));
        buff.put(mode.getWriteFunction().apply(vert2));
        buff.put(mode.getWriteFunction().apply(vert3));
    }
    
    public Integer getVert1() {
        return vert1;
    }
    
    public Integer getVert2() {
        return vert2;
    }
    
    public Integer getVert3() {
        return vert3;
    }
}
