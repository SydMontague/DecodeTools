package net.digimonworld.decodetools.res.payload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class PADHPayload extends ResPayload {
    private static final int PADH_MAGIC_VALUE = 0x48444150;
    private static final int MNKC_MAGIC_VALUE = 0x434B4E4D;
    private static final int TEKC_MAGIC_VALUE = 0x434B4554;
    
    // TODO calculate?
    private float[] boundingBox = new float[6];
    
    private List<MNKCSection> mnkcSections = new ArrayList<>();
    private List<TEKCEntry> tekcEntries = new ArrayList<>();
    
    public PADHPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        
        long startOffset = source.getPosition();
        
        source.readInteger(); // magic value PADH
        source.readInteger(); // totalSize
        
        if (source.readInteger() != 0x434B4E4D) // magic value MNKC
            Main.LOGGER.warning("Found PADH without MNKC at 0x0C");
        
        source.readInteger(); // mnkcSize
        source.readInteger(); // always 5
        
        int sections = source.readInteger();
        
        for (int i = 0; i < 6; i++)
            boundingBox[i] = source.readFloat();
        
        for (int i = 0; i < sections; i++)
            source.readInteger(); // section ptr, not necessary for this use
            
        for (int i = 0; i < sections; i++) {
            mnkcSections.add(new MNKCSection(source));
        }
        
        if (startOffset + size != source.getPosition()) {
            if (source.readInteger() != 0x434B4554) // magic value TEKC
                Main.LOGGER.warning("Found PADH without TEKC, but expected one");
            
            source.readInteger(); // tekcSize
            source.readInteger(); // always 1
            
            int tekcCount = source.readInteger();
            
            for (int i = 0; i < tekcCount; i++)
                tekcEntries.add(new TEKCEntry(source));
        }
        
        if (startOffset + size != source.getPosition())
            Main.LOGGER.warning("Found PADH that didn't reach the end.");
    }
    
    public List<MNKCSection> getMNKCSections() {
        return mnkcSections;
    }
    
    public List<TEKCEntry> getTEKCEntries() {
        return tekcEntries;
    }
    
    public float[] getBoundingBox() {
        return boundingBox;
    }
    
    @Override
    public int getSize() {
        return 8 + getMNKCSize() + getTEKCSize();
    }
    
    @Override
    public Payload getType() {
        return Payload.PADH;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(PADH_MAGIC_VALUE);
        dest.writeInteger(getSize());
        
        dest.writeInteger(MNKC_MAGIC_VALUE);
        dest.writeInteger(getMNKCSize());
        dest.writeInteger(5); // always 5
        dest.writeInteger(mnkcSections.size());
        
        for (float f : boundingBox)
            dest.writeFloat(f);
        
        int sectionPtr = 0x20 + mnkcSections.size() * 4;
        for (MNKCSection section : mnkcSections) {
            dest.writeInteger(sectionPtr);
            sectionPtr += section.getSize();
        }
        
        for (int i = 0; i < mnkcSections.size(); i++) {
            MNKCSection section = mnkcSections.get(i);
            
            section.write(dest);
        }
        
        if (!tekcEntries.isEmpty()) {
            dest.writeInteger(TEKC_MAGIC_VALUE);
            dest.writeInteger(getTEKCSize());
            dest.writeInteger(1); // always 1
            dest.writeInteger(tekcEntries.size());
            tekcEntries.forEach(a -> a.write(dest));
        }
        
    }
    
    private int getMNKCSize() {
        return 0x28 + mnkcSections.stream().collect(Collectors.summingInt(a -> 4 + a.getSize()));
    }
    
    private int getTEKCSize() {
        return tekcEntries.isEmpty() ? 0 : 0x10 + tekcEntries.size() * TEKCEntry.SIZE;
    }
    
    public static class MNKCSection {
        private int unk;
        private List<MNKCFace> faces = new ArrayList<>();
        private List<MNKCVertex> vertices = new ArrayList<>();
        private List<MNKCEntry3> unknown = new ArrayList<>();
        
        public MNKCSection(Access access) {
            unk = access.readInteger();
            int type1Count = access.readInteger();
            int type2Count = access.readInteger();
            access.readInteger(); // entry3 size, always 0x20
            
            for (int i = 0; i < type1Count; i++)
                faces.add(new MNKCFace(access));
            for (int i = 0; i < type2Count; i++)
                vertices.add(new MNKCVertex(access));
            for (int i = 0; i < type1Count; i++)
                unknown.add(new MNKCEntry3(access));
        }
        
        public void write(Access dest) {
            dest.writeInteger(unk);
            dest.writeInteger(faces.size());
            dest.writeInteger(vertices.size());
            dest.writeInteger(0x20); // entry3 size, always 0x20
            
            faces.forEach(a -> a.write(dest));
            vertices.forEach(a -> a.write(dest));
            unknown.forEach(a -> a.write(dest));
        }
        
        public int getSize() {
            return 0x10 + faces.size() * MNKCFace.SIZE + vertices.size() * MNKCVertex.SIZE + unknown.size() * MNKCEntry3.SIZE;
        }
        
        public List<MNKCFace> getFaces() {
            return faces;
        }
        
        public List<MNKCVertex> getVertices() {
            return vertices;
        }
        
        public List<MNKCEntry3> getUnknown() {
            return unknown;
        }
    }
    
    public static class MNKCFace {
        private static final int SIZE = 0x18;
        
        private int vertex1;
        private int vertex2;
        private int vertex3;
        private float unk1;
        private float unk2;
        private float unk3;
        
        public MNKCFace(Access access) {
            this.vertex1 = access.readInteger();
            this.vertex2 = access.readInteger();
            this.vertex3 = access.readInteger();
            this.unk1 = access.readFloat();
            this.unk2 = access.readFloat();
            this.unk3 = access.readFloat();
        }
        
        public void write(Access dest) {
            dest.writeInteger(vertex1);
            dest.writeInteger(vertex2);
            dest.writeInteger(vertex3);
            dest.writeFloat(unk1);
            dest.writeFloat(unk2);
            dest.writeFloat(unk3);
        }
        
        public int getVertex1() {
            return vertex1;
        }
        
        public int getVertex2() {
            return vertex2;
        }
        
        public int getVertex3() {
            return vertex3;
        }
        
        public float getUnk1() {
            return unk1;
        }
        
        public float getUnk2() {
            return unk2;
        }
        
        public float getUnk3() {
            return unk3;
        }
    }
    
    public static class MNKCVertex {
        private static final int SIZE = 0x10;
        
        private float posX;
        private float posY;
        private float posZ;
        private int color;
        
        public MNKCVertex(Access access) {
            this.posX = access.readFloat();
            this.posY = access.readFloat();
            this.posZ = access.readFloat();
            this.color = access.readInteger();
        }
        
        public void write(Access dest) {
            dest.writeFloat(posX);
            dest.writeFloat(posY);
            dest.writeFloat(posZ);
            dest.writeInteger(color);
        }
        
        public float getPosX() {
            return posX;
        }
        
        public float getPosY() {
            return posY;
        }
        
        public float getPosZ() {
            return posZ;
        }
        
        public int getColor() {
            return color;
        }
    }
    
    public static class MNKCEntry3 {
        private static final int SIZE = 0x20;
        
        private int unk1;
        private int unk2;
        private int unk3;
        private int unk4;
        
        private int unk5;
        private int unk6;
        private int unk7;
        private int unk8;
        
        public MNKCEntry3(Access access) {
            this.unk1 = access.readInteger();
            this.unk2 = access.readInteger();
            this.unk3 = access.readInteger();
            this.unk4 = access.readInteger();
            
            this.unk5 = access.readInteger();
            this.unk6 = access.readInteger();
            this.unk7 = access.readInteger();
            this.unk8 = access.readInteger();
        }
        
        public void write(Access dest) {
            dest.writeInteger(unk1);
            dest.writeInteger(unk2);
            dest.writeInteger(unk3);
            dest.writeInteger(unk4);
            
            dest.writeInteger(unk5);
            dest.writeInteger(unk6);
            dest.writeInteger(unk7);
            dest.writeInteger(unk8);
        }
        
        public int getUnk1() {
            return unk1;
        }
        
        public int getUnk2() {
            return unk2;
        }
        
        public int getUnk3() {
            return unk3;
        }
        
        public int getUnk4() {
            return unk4;
        }
        
        public int getUnk5() {
            return unk5;
        }
        
        public int getUnk6() {
            return unk6;
        }
        
        public int getUnk7() {
            return unk7;
        }
        
        public int getUnk8() {
            return unk8;
        }
    }
    
    public static class TEKCEntry {
        private static final int SIZE = 0x4C;
        
        private int triggerId;
        
        private float posX;
        private float posY;
        private float posZ;
        
        private float[] rotationMatrix = new float[9];
        
        private float width;
        private float height;
        private float depth;
        
        public TEKCEntry(Access source) {
            this.triggerId = source.readInteger();
            source.readInteger(); // always 1
            source.readInteger(); // always 0
            
            this.posX = source.readFloat();
            this.posY = source.readFloat();
            this.posZ = source.readFloat();
            
            // TODO convert to euler angles
            this.rotationMatrix[0] = source.readFloat();
            this.rotationMatrix[1] = source.readFloat();
            this.rotationMatrix[2] = source.readFloat();
            this.rotationMatrix[3] = source.readFloat();
            this.rotationMatrix[4] = source.readFloat();
            this.rotationMatrix[5] = source.readFloat();
            this.rotationMatrix[6] = source.readFloat();
            this.rotationMatrix[7] = source.readFloat();
            this.rotationMatrix[8] = source.readFloat();
            
            this.width = source.readFloat();
            this.height = source.readFloat();
            this.depth = source.readFloat();
            
            source.readInteger(); // always 0
        }
        
        public void write(Access dest) {
            dest.writeInteger(triggerId);
            dest.writeInteger(1); // always 1
            dest.writeInteger(0); // always 0
            dest.writeFloat(posX);
            dest.writeFloat(posY);
            dest.writeFloat(posZ);
            dest.writeFloat(rotationMatrix[0]);
            dest.writeFloat(rotationMatrix[1]);
            dest.writeFloat(rotationMatrix[2]);
            dest.writeFloat(rotationMatrix[3]);
            dest.writeFloat(rotationMatrix[4]);
            dest.writeFloat(rotationMatrix[5]);
            dest.writeFloat(rotationMatrix[6]);
            dest.writeFloat(rotationMatrix[7]);
            dest.writeFloat(rotationMatrix[8]);
            dest.writeFloat(width);
            dest.writeFloat(height);
            dest.writeFloat(depth);
            dest.writeInteger(0); // always 0
        }
        
        public int getTriggerId() {
            return triggerId;
        }
        
        public float getPosX() {
            return posX;
        }
        
        public float getPosY() {
            return posY;
        }
        
        public float getPosZ() {
            return posZ;
        }
        
        public float[] getRotationMatrix() {
            return rotationMatrix;
        }
        
        public float getWidth() {
            return width;
        }
        
        public float getHeight() {
            return height;
        }
        
        public float getDepth() {
            return depth;
        }
    }
}
