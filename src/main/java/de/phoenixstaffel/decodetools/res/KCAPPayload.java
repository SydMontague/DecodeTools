package de.phoenixstaffel.decodetools.res;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.payload.BTXFile;
import de.phoenixstaffel.decodetools.res.payload.CTPPPayload;
import de.phoenixstaffel.decodetools.res.payload.GMIOFile;
import de.phoenixstaffel.decodetools.res.payload.GenericPayload;
import de.phoenixstaffel.decodetools.res.payload.HSEMPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;
import de.phoenixstaffel.decodetools.res.payload.LDMPPayload;
import de.phoenixstaffel.decodetools.res.payload.LRTMPayload;
import de.phoenixstaffel.decodetools.res.payload.LTMPPayload;
import de.phoenixstaffel.decodetools.res.payload.MFTPPayload;
import de.phoenixstaffel.decodetools.res.payload.PADHPayload;
import de.phoenixstaffel.decodetools.res.payload.PRGMPayload;
import de.phoenixstaffel.decodetools.res.payload.QSTMFile;
import de.phoenixstaffel.decodetools.res.payload.RTCLPayload;
import de.phoenixstaffel.decodetools.res.payload.TMEPPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNOJPayload;
import de.phoenixstaffel.decodetools.res.payload.TREPPayload;
import de.phoenixstaffel.decodetools.res.payload.VCTMFile;
import de.phoenixstaffel.decodetools.res.payload.XDIOFile;
import de.phoenixstaffel.decodetools.res.payload.XTVOFile;

public abstract class KCAPPayload {
    private static final int DEFAULT_ALIGNMENT = 1;
    
    private KCAPFile parent = null;
    
    public KCAPPayload(KCAPFile parent) {
        this.parent = parent;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public KCAPFile getParent() {
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
    
    public static KCAPPayload craft(Access source, int dataStart, KCAPFile parent, int size) {
        return Payload.valueOf(parent, source.readInteger(source.getPosition())).newInstance(source,
                                                                                             dataStart,
                                                                                             parent,
                                                                                             size);
    }
    
    @Override
    public String toString() {
        return getType().name();
    }
    
    public enum Payload {
        GENERIC(0, GenericPayload.class),
        GMIO(0x4F494D47, GMIOFile.class, a -> 0x40 + a.readInteger(0x3C)),
        KCAP(0x5041434B, KCAPFile.class, a -> a.readInteger(0x08)),
        XTVO(0x4F565458, XTVOFile.class),
        XDIO(0x4F494458, XDIOFile.class),
        VCTM(0x4D544356, VCTMFile.class),
        QSTM(0x4D545351, QSTMFile.class),
        BTX(0x20585442, BTXFile.class),
        PADH(0x48444150, PADHPayload.class),
        HSEM(0, HSEMPayload.class),
        LRTM(0, LRTMPayload.class),
        CTPP(0, CTPPPayload.class),
        LTMP(0, LTMPPayload.class),
        TNFO(0, TNFOPayload.class),
        LDMP(0, LDMPPayload.class),
        MFTP(0, MFTPPayload.class),
        PRGM(0, PRGMPayload.class),
        RTCL(0, RTCLPayload.class),
        TMEP(0, TMEPPayload.class),
        TREP(0, TREPPayload.class),
        TNOJ(0, TNOJPayload.class),;
        
        private static final Logger log = Logger.getLogger("DataMiner");
        
        private final int magicValue;
        private final Class<? extends KCAPPayload> clazz;
        private Function<Access, Integer> method;
        
        private Payload(int magicValue, Class<? extends KCAPPayload> clazz) {
            this(magicValue, clazz, a -> 0);
        }
        
        private Payload(int magicValue, Class<? extends KCAPPayload> clazz, Function<Access, Integer> method) {
            this.magicValue = magicValue;
            this.clazz = clazz;
            this.method = method;
        }
        
        public int getMagicValue() {
            return magicValue;
        }
        
        public static Payload valueOf(KCAPFile parent, int value) {
            for (Payload extension : values()) {
                if (extension.magicValue == 0)
                    continue;
                
                if (extension.magicValue == value)
                    return extension;
            }
            
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
                case KPTF:
                    return TNFO;
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
        
        public KCAPPayload newInstance(Access source, int dataStart, KCAPFile parent, int size) {
            try {
                return clazz.getConstructor(Access.class, int.class, KCAPFile.class, int.class)
                            .newInstance(source, dataStart, parent, size);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                    | SecurityException e) {
                log.log(Level.WARNING, "Failed to instantiate HeaderExtension " + this, e);
                throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException e) {
                log.log(Level.WARNING, this.name());
                throw e;
            }
        }
        
        public int getDataStart(Access source) {
            return Utils.getPadded(method.apply(source), 0x80);
        }
    }
    
    public void fillDummyResData(DummyResData data) {
    }
}
