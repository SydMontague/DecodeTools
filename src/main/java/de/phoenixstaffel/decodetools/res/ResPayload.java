package de.phoenixstaffel.decodetools.res;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.payload.BTXFile;
import de.phoenixstaffel.decodetools.res.payload.CTPPPayload;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.GenericPayload;
import de.phoenixstaffel.decodetools.res.payload.HSEMPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;
import de.phoenixstaffel.decodetools.res.payload.LDMPPayload;
import de.phoenixstaffel.decodetools.res.payload.LRTMPayload;
import de.phoenixstaffel.decodetools.res.payload.LTMPPayload;
import de.phoenixstaffel.decodetools.res.payload.MFTPPayload;
import de.phoenixstaffel.decodetools.res.payload.PADHPayload;
import de.phoenixstaffel.decodetools.res.payload.PRGMPayload;
import de.phoenixstaffel.decodetools.res.payload.QSTMPayload;
import de.phoenixstaffel.decodetools.res.payload.RTCLPayload;
import de.phoenixstaffel.decodetools.res.payload.TMEPPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNOJPayload;
import de.phoenixstaffel.decodetools.res.payload.TREPPayload;
import de.phoenixstaffel.decodetools.res.payload.VCTMPayload;
import de.phoenixstaffel.decodetools.res.payload.XDIOPayload;
import de.phoenixstaffel.decodetools.res.payload.XTVOPayload;

public abstract class ResPayload {
    private static final int DEFAULT_ALIGNMENT = 1;
    
    private KCAPPayload parent = null;
    
    public ResPayload(KCAPPayload parent) {
        this.parent = parent;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public KCAPPayload getParent() {
        return parent;
    }
    
    public abstract int getSize();
    
    public MutableTreeNode getTreeNode() {
        return new DefaultMutableTreeNode(this);
    }
    
    public abstract Payload getType();
    
    /**
     * Get the size of the uppermost parent element, which includes all the child elements. This function should only be
     * called after the root element has been fully initialised.
     * 
     * @return the size of the root node
     */
    public int getSizeOfRoot() {
        return parent != null ? parent.getSizeOfRoot() : getSize();
    }
    
    /**
     * Gets the alignment of that data structure, so that padding gets added if necessary. For example, if the value is
     * 0x10 then each address is dividable by 0x10.
     * 
     * @return the alignment
     */
    public int getAlignment() {
        return DEFAULT_ALIGNMENT;
    }
    
    public abstract void writeKCAP(Access dest, IResData dataStream);
    
    public static ResPayload craft(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        return Payload.valueOf(parent, source.readLong(source.getPosition())).newInstance(source, dataStart, parent, size, name);
    }
    
    @Override
    public String toString() {
        return getType().name();
    }
    
    public enum Payload {
        GENERIC(0, GenericPayload.class),
        GMIO(0x4F494D47, GMIOPayload.class, a -> 0x40 + a.readInteger(0x3C)),
        KCAP(0x5041434B, KCAPPayload.class, a -> a.readInteger(0x08)),
        XTVO(0x4F565458, XTVOPayload.class),
        XDIO(0x4F494458, XDIOPayload.class),
        VCTM(0x4D544356, VCTMPayload.class),
        QSTM(0x4D545351, QSTMPayload.class),
        BTX(0x20585442, BTXFile.class),
        PADH(0x48444150, PADHPayload.class),
        HSEM(0, HSEMPayload.class),
        LRTM(0, LRTMPayload.class),
        CTPP(0, CTPPPayload.class),
        LTMP(0, LTMPPayload.class),
        TNFO(0x4F464E54, TNFOPayload.class),
        LDMP(0, LDMPPayload.class),
        MFTP(0, MFTPPayload.class),
        PRGM(0, PRGMPayload.class),
        RTCL(0, RTCLPayload.class),
        TMEP(0, TMEPPayload.class),
        TREP(0, TREPPayload.class),
        TNOJ(0, TNOJPayload.class),;
        
        private final int magicValue;
        private final Class<? extends ResPayload> clazz;
        private Function<Access, Integer> method;
        
        private Payload(int magicValue, Class<? extends ResPayload> clazz) {
            this(magicValue, clazz, a -> 0);
        }
        
        private Payload(int magicValue, Class<? extends ResPayload> clazz, Function<Access, Integer> method) {
            this.magicValue = magicValue;
            this.clazz = clazz;
            this.method = method;
        }
        
        public int getMagicValue() {
            return magicValue;
        }
        
        public static Payload valueOf(KCAPPayload parent, long value) {
            int left = (int) (value >>> 32);
            int right = (int) (value & 0xFFFFFFFF);
            
            for (Payload extension : values()) {
                if (extension.magicValue == 0)
                    continue;
                
                if (extension.magicValue == right)
                    return extension;
            }
            
            if (left == BTX.getMagicValue())
                return BTX;
            
            if (parent == null || parent.getExtension() == null)
                return GENERIC;
            
            switch (parent.getExtension().getType()) {
                case HSEM:
                    return HSEM;
                case LRTM:
                    return LRTM;
                case CTPP:
                    return CTPP;
                case LTMP:
                    return LTMP;
                case LDMP:
                    return LDMP;
                case MFTP:
                    return MFTP;
                case PRGM:
                    return PRGM;
                case RTCL:
                    return RTCL;
                case TMEP:
                    return TMEP;
                case TREP:
                    return TREP;
                case TNOJ:
                    return TNOJ;
                default:
                    return GENERIC;
            }
        }
        
        public ResPayload newInstance(Access source, int dataStart, KCAPPayload parent, int size, String name) {
            try {
                return clazz.getConstructor(Access.class, int.class, KCAPPayload.class, int.class, String.class).newInstance(source, dataStart, parent, size, name);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                Main.LOGGER.log(Level.WARNING, "Failed to instantiate HeaderExtension " + this, e);
                throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException e) {
                Main.LOGGER.log(Level.WARNING, this.name());
                throw e;
            }
        }
        
        public int getDataStart(Access source) {
            return Utils.align(method.apply(source), 0x80);
        }
    }
    
    public void fillDummyResData(DummyResData data) {
    }
    
    public List<ResPayload> getElementsWithType(Payload type) {
        List<ResPayload> list = new ArrayList<>();
        
        if (getType() == type)
            list.add(this);
        
        return list;
    }
}
