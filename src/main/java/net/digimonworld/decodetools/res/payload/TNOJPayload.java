package net.digimonworld.decodetools.res.payload;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.NameablePayload;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;
import net.digimonworld.decodetools.res.kcap.TNOJKCAP;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP.KCAPType;

public class TNOJPayload extends NameablePayload {
    
    // private int nameId
    private int parentId; // TODO replace with reference of actual TNOJ, calculate this stuff when necessary
    private int unknown1;
    private int unknown2;
    
    // combined rotation+translation of this node and all parent node until root
    private float[] matrix = new float[16]; // rotation+translation matrix?
    
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float unknown3;
    
    private float unknown4;
    private float unknown5;
    private float unknown6;
    private float unknown7;
    
    private float scaleX; // ? scales the vertices of this joint + children
    private float scaleY; // ?
    private float scaleZ; // ?
    // padding
    
    private float localScaleX; // ? scales only the joint
    private float localScaleY; // ?
    private float localScaleZ; // ?
    // padding
    
    public TNOJPayload(AbstractKCAP parent, int parentBone, String name, int unknown1, int unknown2, float[] parentMatrix, 
                       float[] offsetVector, float[] unknownVector, float[] scaleVector, float[] localScaleVector) {
        super(parent, name);
        
        this.parentId = parentBone;
        this.unknown1 = unknown1;
        this.unknown2 = unknown2;
        
        this.matrix = Arrays.copyOf(parentMatrix, 16);
        
        this.xOffset = offsetVector[0];
        this.yOffset = offsetVector[1];
        this.zOffset = offsetVector[2];
        this.unknown3 = offsetVector[3];
        
        this.unknown4 = unknownVector[0];
        this.unknown5 = unknownVector[1];
        this.unknown6 = unknownVector[2];
        this.unknown7 = unknownVector[3];
        
        this.scaleX = scaleVector[0];
        this.scaleY = scaleVector[1];
        this.scaleZ = scaleVector[2];

        this.localScaleX = localScaleVector[0];
        this.localScaleY = localScaleVector[1];
        this.localScaleZ = localScaleVector[2];
        
        matrix[3] -= xOffset;
        matrix[7] -= yOffset;
        matrix[11] -= zOffset;
        
        // TODO proper modification of the matrix, taking into account scale and rotation
    }
    
    public TNOJPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent, name);
        
        source.readInteger(); // nameId
        parentId = source.readInteger();
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        
        for (int i = 0; i < 16; i++)
            matrix[i] = source.readFloat();
        
        xOffset = source.readFloat();
        yOffset = source.readFloat();
        zOffset = source.readFloat();
        unknown3 = source.readFloat();
        
        unknown4 = source.readFloat();
        unknown5 = source.readFloat();
        unknown6 = source.readFloat();
        unknown7 = source.readFloat();
        
        scaleX = source.readFloat();
        scaleY = source.readFloat();
        scaleZ = source.readFloat();
        source.readFloat(); // padding
        
        localScaleX = source.readFloat();
        localScaleY = source.readFloat();
        localScaleZ = source.readFloat();
        source.readFloat(); // padding
        
        source.readInteger(); // padding
        source.readInteger(); // padding
        source.readInteger(); // padding
        source.readInteger(); // padding
    }
    
    @Override
    public int getSize() {
        return 0xA0;
    }
    
    @Override
    public Payload getType() {
        return Payload.TNOJ;
    }
    
    @Override
    public TNOJKCAP getParent() {
        return (TNOJKCAP) super.getParent();
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        int nameId = -1;
        if(getParent() != null && getParent().getKCAPType() == KCAPType.TNOJ) {
            Iterator<? extends NameablePayload> itr = getParent().getEntries().stream().filter(a -> a instanceof NameablePayload)
                                                                 .map(a -> (NameablePayload) a).filter(NameablePayload::hasName)
                                                                 .sorted(Comparator.comparing(NameablePayload::getName)).iterator();
            
            while (itr.hasNext()) {
                nameId++;
                NameablePayload payload = itr.next();
                if(payload.getName().equalsIgnoreCase(getName()))
                    break;
            }
        }
        
        
        dest.writeInteger(nameId);
        dest.writeInteger(parentId);
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        
        for (float f : matrix)
            dest.writeFloat(f);
        
        dest.writeFloat(xOffset);
        dest.writeFloat(yOffset);
        dest.writeFloat(zOffset);
        dest.writeFloat(unknown3);
        
        dest.writeFloat(unknown4);
        dest.writeFloat(unknown5);
        dest.writeFloat(unknown6);
        dest.writeFloat(unknown7);
        
        dest.writeFloat(scaleX);
        dest.writeFloat(scaleY);
        dest.writeFloat(scaleZ);
        dest.writeFloat(0);
        
        dest.writeFloat(localScaleX);
        dest.writeFloat(localScaleY);
        dest.writeFloat(localScaleZ);
        dest.writeFloat(0);
        
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
    }

    public float[] getOffsetMatrix() {
        return matrix;
    }
    
    public float getXOffset() {
        return xOffset;
    }
    
    public float getYOffset() {
        return yOffset;
    }
    
    public float getZOffset() {
        return zOffset;
    }
    
    public float getLocalScaleX() {
        return localScaleX;
    }
    
    public float getLocalScaleY() {
        return localScaleY;
    }
    
    public float getLocalScaleZ() {
        return localScaleZ;
    }
    
    public float getScaleX() {
        return scaleX;
    }
    
    public float getScaleY() {
        return scaleY;
    }
    
    public float getScaleZ() {
        return scaleZ;
    }
    
    public int getParentId() {
        return parentId;
    }
    
    public float getUnknown3() {
        return unknown3;
    }
    
    public float getRotationX() {
        return unknown4;
    }
    
    public float getRotationY() {
        return unknown5;
    }
    
    public float getRotationZ() {
        return unknown6;
    }
    
    public float getRotationW() {
        return unknown7;
    }
    
    public double[] getAngles() {
        double[] angles = new double[3];
        
        double x = getRotationX();
        double y = getRotationY();
        double z = getRotationZ();
        double w = getRotationW();
        
        // code adapted from Wikipedia https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles#Source_code_2
        double sinrCosp = 2 * (w * x + y * z);
        double cosrCosp = 1 - 2 * (x * x + y * y);
        angles[0] = Math.toDegrees(Math.atan2(sinrCosp, cosrCosp));

        double sinp = 2 * (w * y - z * x);
        if (Math.abs(sinp) >= 1)
            angles[1] = Math.toDegrees(Math.copySign(Math.PI / 2, sinp)); // use 90 degrees if out of range
        else
            angles[1] = Math.toDegrees(Math.asin(sinp));
        
        double sinyCosp = 2 * (w * z + x * y);
        double cosyCosp = 1 - 2 * (y * y + z * z);
        angles[2] = Math.toDegrees(Math.atan2(sinyCosp, cosyCosp));
        
        return angles;
    }
}
