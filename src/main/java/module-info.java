module net.digimonworld.decode.decode_tools {
    exports net.digimonworld.decodetools.res.kcap;
    exports net.digimonworld.decodetools.res.payload.hsem;
    exports net.digimonworld.decodetools.export.fontxml;
    exports net.digimonworld.decodetools.res;
    exports net.digimonworld.decodetools;
    exports net.digimonworld.decodetools.randomizer;
    exports net.digimonworld.decodetools.gui;
    exports net.digimonworld.decodetools.keepdata;
    exports net.digimonworld.decodetools.core;
    exports net.digimonworld.decodetools.arcv;
    exports net.digimonworld.decodetools.res.payload;
    exports net.digimonworld.decodetools.gui.util;
    exports net.digimonworld.decodetools.keepdata.enums;
    exports net.digimonworld.decodetools.res.payload.xdio;
    exports net.digimonworld.decodetools.res.payload.xtvo;
    exports net.digimonworld.decodetools.res.payload.qstm;
    exports net.digimonworld.decodetools.map;
    
    requires transitive java.desktop;
    requires transitive java.logging;
    requires java.xml;
    requires org.lwjgl;
    requires org.lwjgl.assimp;
}
