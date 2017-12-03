package de.phoenixstaffel.decodetools.res;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.extensions.CTPPExtension;
import de.phoenixstaffel.decodetools.res.extensions.GMIPExtension;
import de.phoenixstaffel.decodetools.res.extensions.HSEMExtension;
import de.phoenixstaffel.decodetools.res.extensions.HSMPExtension;
import de.phoenixstaffel.decodetools.res.extensions.KPTFExtension;
import de.phoenixstaffel.decodetools.res.extensions.LDMPExtension;
import de.phoenixstaffel.decodetools.res.extensions.LRTMExtension;
import de.phoenixstaffel.decodetools.res.extensions.LTMPExtension;
import de.phoenixstaffel.decodetools.res.extensions.MFTPExtension;
import de.phoenixstaffel.decodetools.res.extensions.PRGMExtension;
import de.phoenixstaffel.decodetools.res.extensions.RTCLExtension;
import de.phoenixstaffel.decodetools.res.extensions.TDTMExtension;
import de.phoenixstaffel.decodetools.res.extensions.TMEPExtension;
import de.phoenixstaffel.decodetools.res.extensions.TNOJExtension;
import de.phoenixstaffel.decodetools.res.extensions.TREPExtension;
import de.phoenixstaffel.decodetools.res.extensions.VoidExtension;
import de.phoenixstaffel.decodetools.res.extensions.XDIPExtension;
import de.phoenixstaffel.decodetools.res.extensions.XFEPExtension;
import de.phoenixstaffel.decodetools.res.extensions.XTVPExtension;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

public interface HeaderExtension {
    
    public static HeaderExtension craft(Access source) {
        return Extensions.valueOf(source.readInteger()).newInstance(source);
    }
    
    enum Extensions {
        GMIP(0x50494D47, GMIPExtension.class),
        XTVP(0x50565458, XTVPExtension.class),
        XDIP(0x50494458, XDIPExtension.class),
        HSMP(0x504D5348, HSMPExtension.class),
        HSEM(0x4D455348, HSEMExtension.class),
        LRTM(0x4D54524C, LRTMExtension.class),
        PRGM(0x4D475250, PRGMExtension.class),
        TNOJ(0x4A4F4E54, TNOJExtension.class),
        RTCL(0x4C435452, RTCLExtension.class),
        TDTM(0x4D544454, TDTMExtension.class),
        XFEP(0x50454658, XFEPExtension.class),
        TREP(0x50455254, TREPExtension.class),
        MFTP(0x5054464D, MFTPExtension.class),
        TMEP(0x50454D54, TMEPExtension.class),
        CTPP(0x50505443, CTPPExtension.class),
        LTMP(0x504D544C, LTMPExtension.class),
        LDMP(0x504D444C, LDMPExtension.class),
        KPTF(0x4654504B, KPTFExtension.class),
        VOID(0x00000000, VoidExtension.class); // no extension
        
        private final int magicValue;
        private final Class<? extends HeaderExtension> clazz;
        
        private Extensions(int magicValue, Class<? extends HeaderExtension> clazz) {
            this.magicValue = magicValue;
            this.clazz = clazz;
        }
        
        public static Extensions valueOf(int value) {
            for (Extensions extension : values()) {
                if (extension.magicValue == value)
                    return extension;
            }
            
            throw new IllegalArgumentException(Integer.toHexString(value) + " is not a known HeaderExtension");
        }
        
        public HeaderExtension newInstance(Access source) {
            try {
                return clazz.getConstructor(Access.class).newInstance(source);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                    | SecurityException e) {
                Main.LOGGER.log(Level.WARNING, "Failed to instantiate HeaderExtension " + this, e);
                throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException e) {
                throw e;
            }
        }
        
        public int getMagicValue() {
            return magicValue;
        }
        
        public int getPadding() {
            switch (this) {
                case VOID:
                case GMIP:
                    return 0x4;
                default:
                    return 0x10;
            }
        }
    }
    
    class NamePointer {
        private final int offset;
        private final int id;
        
        public NamePointer(int offset, int length) {
            this.offset = offset;
            this.id = length;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getLength() {
            return id;
        }
    }
    
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries);
    
    public Extensions getType();
    
    public int getContentAlignment(KCAPPayload parent);
    
    public int getSize();
    
    public void writeKCAP(Access dest);
}
