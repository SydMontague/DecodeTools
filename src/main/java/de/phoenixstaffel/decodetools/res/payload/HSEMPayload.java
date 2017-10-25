package de.phoenixstaffel.decodetools.res.payload;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEM03Entry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEM07Entry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMDrawEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMJointEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMMaterialEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMTextureEntry;
import de.phoenixstaffel.decodetools.res.payload.xdio.XDIOFace;
import de.phoenixstaffel.decodetools.res.payload.xtvo.XTVORegisterType;

/*
 * HSEM "head" (0x40 byte)
 *  ID (4 byte)
 *  size (4 byte)
 *  unk1 (4 byte)
 *  unk2 (4 byte)
 *  10x unk3 (float)
 *  unk3 (4 byte)
 *  unk4 (4 byte)
 * HSEM "payload" (size - 0x40 byte)
 *  variable amount of payload entries
 *   id (short)
 *   size (short)
 *   data (size - 4)
 * 
 * 
 * 0x58                     HSEM into data
 * 0x14 byte                intro data
 * 0x04 * short(0x12) byte  joint data
 * 0x14 byte                outro data
 */
public class HSEMPayload extends ResPayload {
    private int id;
    private int size;
    private int unknown1;
    private int unknown2;
    
    private float[] headerData = new float[10];
    private int unknown3;
    private int unknown4;
    
    private List<HSEMEntry> entries = new ArrayList<>();
    
    public HSEMPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        super(parent);
        
        long start = source.getPosition();
        
        this.id = source.readInteger();
        this.size = source.readInteger();
        this.unknown1 = source.readInteger();
        this.unknown2 = source.readInteger();
        
        for (int i = 0; i < 10; i++) {
            this.headerData[i] = source.readFloat();
        }
        
        this.unknown3 = source.readInteger();
        this.unknown4 = source.readInteger();
        
        while (source.getPosition() - start < size) {
            entries.add(new HSEMEntry(source));
        }
    }
    
    @Override
    public int getSize() {
        return 0x40 + entries.stream().collect(Collectors.summingInt(HSEMEntry::getSize));
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.HSEM;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(id);
        dest.writeInteger(size);
        
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        
        for (float f : headerData)
            dest.writeFloat(f);
        
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        
        entries.forEach(a -> a.writeKCAP(dest));
    }
    
    // FIXME add COLLADA im-/exporter
    // FIXME make cleaner and stuff
    public void toObj(PrintStream out) {
        int vertexOffset = 1;
        int normalOffset = 1;
        int uvOffset = 1;
        int groupIndex = 0;
        
        List<ResPayload> xtvos = getParent().getParent().getElementsWithType(Payload.XTVO);
        List<ResPayload> xdios = getParent().getParent().getElementsWithType(Payload.XDIO);
        
        for (HSEMEntry entry : entries) {
            if (entry.getPayload() instanceof HSEMDrawEntry) {
                HSEMDrawEntry e = (HSEMDrawEntry) entry.getPayload();
                XTVOPayload xtvo = (XTVOPayload) xtvos.get(e.getVertexId());
                XDIOPayload xdio = (XDIOPayload) xdios.get(e.getIndexId());
                
                boolean hasNorm = xtvo.getAttributes().stream().anyMatch(a -> a.getRegisterId() == XTVORegisterType.NORMAL);
                boolean hasUV = xtvo.getAttributes().stream().anyMatch(a -> a.getRegisterId() == XTVORegisterType.TEXTURE0);
                
                out.println("g group_" + groupIndex++);
                
                // vertex, normal, uv
                xtvo.getVertices().forEach(a -> Utils.VERTEX_TO_OBJ_FUNCTION.apply(a, XTVORegisterType.POSITION, "v ", out));
                xtvo.getVertices().forEach(a -> Utils.VERTEX_TO_OBJ_FUNCTION.apply(a, XTVORegisterType.NORMAL, "vn ", out));
                xtvo.getVertices().forEach(a -> Utils.VERTEX_TO_OBJ_FUNCTION.apply(a, XTVORegisterType.TEXTURE0, "vt ", out));
                
                for (XDIOFace a : xdio.getFaces())
                    out.println(new Face(a, vertexOffset, hasNorm ? normalOffset : -1, hasUV ? uvOffset : -1).toObj());
                
                vertexOffset += xtvo.getVertices().size();
                normalOffset += hasNorm ? xtvo.getVertices().size() : 0;
                uvOffset += hasUV ? xtvo.getVertices().size() : 0;
            }
            // FIXME implement texture/material export
            // FIXME implement UV import
            // TODO build model export GUI
            if (entry.getPayload() instanceof HSEMTextureEntry)
                continue; // TODO activate texture
            if (entry.getPayload() instanceof HSEMMaterialEntry)
                continue; // TODO store/activate material
                
            if (entry.getPayload() instanceof HSEM07Entry)
                continue; // TODO figure out use?
            if (entry.getPayload() instanceof HSEM03Entry)
                continue; // not needed/supported?
            if (entry.getPayload() instanceof HSEMJointEntry)
                continue; // Not supported by OBJ
        }
    }
}

class Face {
    private int vert1;
    private int norm1;
    private int uv1;
    
    private int vert2;
    private int norm2;
    private int uv2;
    
    private int vert3;
    private int norm3;
    private int uv3;
    
    public Face(XDIOFace source, int vertOffset, int normalOffset, int uvOffset) {
        this.vert1 = source.getVert1() + vertOffset;
        this.vert2 = source.getVert2() + vertOffset;
        this.vert3 = source.getVert3() + vertOffset;
        
        this.norm1 = normalOffset == -1 ? -1 : source.getVert1() + normalOffset;
        this.norm2 = normalOffset == -1 ? -1 : source.getVert2() + normalOffset;
        this.norm3 = normalOffset == -1 ? -1 : source.getVert3() + normalOffset;
        
        this.uv1 = uvOffset == -1 ? -1 : source.getVert1() + uvOffset;
        this.uv2 = uvOffset == -1 ? -1 : source.getVert2() + uvOffset;
        this.uv3 = uvOffset == -1 ? -1 : source.getVert3() + uvOffset;
    }
    
    public String toObj() {
        if (norm1 == -1 && uv1 == -1)
            return "f " + vert1 + " " + vert2 + " " + vert3;
        if (norm1 == -1)
            return "f " + vert1 + "/" + uv1 + " " + vert2 + "/" + uv2 + " " + vert3 + "/" + uv3;
        if (uv1 == -1)
            return "f " + vert1 + "//" + norm1 + " " + vert2 + "//" + norm2 + " " + vert3 + "//" + norm3;
        
        return "f " + vert1 + "/" + uv1 + "/" + norm1 + " " + vert2 + "/" + uv2 + "/" + norm2 + " " + vert3 + "/" + uv3 + "/" + norm3;
    }
}
