package de.phoenixstaffel.decodetools.res;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload;
import de.phoenixstaffel.decodetools.res.payload.CTPPPayload;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.GenericPayload;
import de.phoenixstaffel.decodetools.res.payload.HSEMPayload;
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
import de.phoenixstaffel.decodetools.res.payload.VoidPayload;
import de.phoenixstaffel.decodetools.res.payload.XDIOPayload;
import de.phoenixstaffel.decodetools.res.payload.XTVOPayload;

/**
 * The abstract superclass for all different entry types inside a Re:Digitize/Decode Resource file.
 */
public abstract class ResPayload {
    private AbstractKCAP parent = null;
    
    protected ResPayload(AbstractKCAP parent) {
        this.parent = parent;
    }
    
    /**
     * Gets whether the payload has a parent KCAP it is embedded in or not.
     * 
     * @return true when the entry has a parent KCAP, false otherwise
     */
    public boolean hasParent() {
        return parent != null;
    }
    
    /**
     * Gets the parent KCAP of this entry or null if {@link #hasParent()} is false.
     * 
     * @return the parent KCAPPayload or null if there is none
     */
    @Deprecated
    public AbstractKCAP getParent() {
        return parent;
    }
    
    /**
     * Sets the parent KCAP of this entry, only few entries care really for this (e.g. TNOJ)
     * 
     * @param parent the new parent of the entry
     */
    public void setParent(AbstractKCAP parent) {
        this.parent = parent;
    }
    
    /**
     * Gets the entry's size when written in a Resource file, excluding ResData but including all potential children.
     * 
     * @return the entry's size
     */
    public abstract int getSize();
    
    /**
     * Gets the type of the Payload.
     * 
     * @return the type of the Payload
     */
    public abstract Payload getType();
    
    /**
     * Get the size of the uppermost parent element, which includes all the child elements. This function should only be
     * called after the root element has been fully initialized.
     * 
     * @return the size of the root node
     */
    public int getSizeOfRoot() {
        return parent != null ? parent.getSizeOfRoot() : getSize();
    }
    
    /**
     * Writes the resource entry to given then {@link Access} and eventual data to the given {@link IResData}
     * starting from the current position of each.
     * 
     * @param dest the {@link Access} to write into
     * @param dataStream the {@link ResData} to write into
     */
    public abstract void writeKCAP(Access dest, ResData dataStream);
    
    /**
     * Creates a new ResPayload by reading the next structure from the passed {@link Access}.
     * 
     * @param source the {@link Access} to read from
     * @param dataStart a pointer to the start of the data section of the file this entry exists in
     * @param parent the parent KCAP of this entry or null if there is none
     * @param size the size of the entry as defined by the parent KCAP or -1 if it is the root entry
     * @param name the name of the entry as defined by the parent KCAP or null if there is none
     * @return the newly created ResPayload
     */
    public static ResPayload craft(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        if(size == 0)
            return new VoidPayload(parent);
        
        return Payload.valueOf(parent, source.readLongOffset(0)).newInstance(source, dataStart, parent, size, name);
    }
    
    /**
     * Creates a new ResPayload by reading the next structure from the passed {@link Access}.
     * 
     * @param source the {@link Access} to read from
     * @return the newly created ResPayload
     */
    public static ResPayload craft(Access source) {
        int dataStart = Payload.valueOf(null, source.readLong(0)).getDataStart(source);
        return ResPayload.craft(source, dataStart, (AbstractKCAP) null, -1, null);
    }
    
    /**
     * Writes the entry's resource data to a {@link DummyResData} instance for size calculation purposes.
     * Since it's not an actual write this allows to only perform what is necessary for the calculations, 
     * thus improving the performance.
     * 
     * @param data the {@link DummyResData} to write into
     */
    public void fillDummyResData(DummyResData data) {
    }
    
    /**
     * Gets a list of all entries with a certain {@link Payload} in this entry, including itself.
     * 
     * @param type the Payload type to look for
     * @return a list of ResPayloads with that type
     */
    public List<ResPayload> getElementsWithType(Payload type) {
        List<ResPayload> list = new ArrayList<>();
        
        if (getType() == type)
            list.add(this);
        
        return list;
    }
    
    public void repack(File file) {
        file.delete();
        if (!file.exists())
            try {
                Optional.ofNullable(file.getParentFile()).ifPresent(File::mkdirs);
                file.createNewFile();
            }
            catch (IOException e1) {
                Main.LOGGER.log(Level.WARNING, "Exception while writing new .res file.", e1);
            }
        
        try (Access dest = new FileAccess(file); ResData data = new ResData()) {
            writeKCAP(dest, data);
            
            if(data.getSize() != 0) {
                dest.setPosition(Utils.align(getSizeOfRoot(), 0x80));
                dest.writeByteArray(data.getStream().toByteArray());
            }
        }
        catch (IOException e) {
            Main.LOGGER.log(Level.WARNING, "Exception while writing new .res file.", e);
        }
    }

    @Override
    public String toString() {
        return getType().name();
    }
    
    /**
     * A functional interface for {@link Payload}, to allow to passing Lambdas and method references.
     */
    @FunctionalInterface
    private static interface ResPayloadInitializer {
        /**
         * See {@link ResPayload#craft(Access, int, AbstractKCAP, int, String)}
         */
        public ResPayload initialize(Access source, int dataStart, AbstractKCAP parent, int size, String name);
    }
    
    /**
     * An enumeration of all valid Payload types for resource files.
     */
    public enum Payload {
        GENERIC(0, GenericPayload::new),
        GMIO(0x4F494D47, GMIOPayload::new, a -> 0x40 + a.readInteger(0x3C)),
        KCAP(0x5041434B, AbstractKCAP::craftKCAP, a -> a.readInteger(0x08)),
        XTVO(0x4F565458, XTVOPayload::new, a -> 0x74 + 0xC * a.readShort(0x30)),
        XDIO(0x4F494458, XDIOPayload::new, a -> 0x20),
        VCTM(0x4D544356, VCTMPayload::new),
        QSTM(0x4D545351, QSTMPayload::new),
        BTX(0x20585442, BTXPayload::new),
        PADH(0x48444150, PADHPayload::new),
        HSEM(0, HSEMPayload::new),
        LRTM(0, LRTMPayload::new),
        CTPP(0, CTPPPayload::new),
        LTMP(0, LTMPPayload::new),
        TNFO(0x4F464E54, TNFOPayload::new),
        LDMP(0, LDMPPayload::new),
        MFTP(0, MFTPPayload::new),
        PRGM(0, PRGMPayload::new),
        RTCL(0, RTCLPayload::new),
        TMEP(0, TMEPPayload::new),
        TREP(0, TREPPayload::new),
        TNOJ(0, TNOJPayload::new);
        
        private final int magicValue;
        private Function<Access, Integer> sizeMethod;
        private ResPayloadInitializer initializer;
        
        /**
         * @param magicValue the magic value that identify the Payload or 0 there is none
         * @param initializer a {@link ResPayloadInitializer} to instantiate the payload, e.g. a constructor reference 
         */
        private Payload(int magicValue, ResPayloadInitializer initializer) {
            this(magicValue, initializer, a -> 0);
        }
        
        /**
         * @param magicValue the magic value that identify the Payload or 0 there is none
         * @param initializer a {@link ResPayloadInitializer} to instantiate the payload, e.g. a constructor reference 
         * @param sizeMethod a Function that reads from a given {@link Access} the size of the given resource entry. <br /> 
         *                   This is only relevant for payloads that can be root <b>and</b> contain resource data.
         */
        private Payload(int magicValue, ResPayloadInitializer initializer, Function<Access, Integer> sizeMethod) {
            this.magicValue = magicValue;
            this.initializer = initializer;
            this.sizeMethod = sizeMethod;
        }
        
        /**
         * Gets the magic 4-byte value that identifies this Payload.
         * 
         * @return the magic value of this Payload
         */
        public int getMagicValue() {
            return magicValue;
        }
        
        /**
         * Returns the Payload that are identified by the first 8 bytes of it's structure as well as it's parent KCAP.
         * 
         * First the passed bytes 0-3 get checked if they match a magic value as well as byte 4-7 for the BTX magic value.
         * If no magic value matches, the parent's KCAP Extension gets checked whether it implies a certain Payload type.
         * 
         * In case there are still no matches the payload will be considered GENERIC.
         * 
         * @param parent the parent {@link AbstractKCAP}
         * @param value the first 8-bytes of the Payload's structure
         * @return the type of the Payload identified by the parent and/or the first 8-bytes of data
         */
        public static Payload valueOf(AbstractKCAP parent, long value) {
            //split the long value to compare them to the 4-byte magic value
            int left = (int) (value >>> 32);
            int right = (int) (value & 0xFFFFFFFF);
            
            //check if the first 4 bytes match a magic value
            for (Payload extension : values()) {
                if (extension.magicValue == 0)
                    continue;
                
                if (extension.magicValue == right)
                    return extension;
            }
            
            //BTX with meta entries have their magic value at 0x4 instead of 0x0
            if (left == BTX.getMagicValue())
                return BTX;
            
            //some payloads depend on the extension of the parent KCAP
            if (parent == null || parent.getKCAPType() == null)
                return GENERIC;
            
            switch (parent.getKCAPType()) {
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
        
        /**
         * See {@link ResPayload#craft(Access, int, AbstractKCAP, int, String)}
         */
        public ResPayload newInstance(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
            return initializer.initialize(source, dataStart, parent, size, name);
        }
        
        /**
         * Gets the address where the res data starts for this entry, if it were root
         * 
         * @param source the Access to read from, current at the start of a structure with this type
         * @return the address of where the res data starts, aligned to 0x80
         */
        public int getDataStart(Access source) {
            return Utils.align(sizeMethod.apply(source), 0x80);
        }
    }
}
