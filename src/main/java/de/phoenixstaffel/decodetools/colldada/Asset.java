package de.phoenixstaffel.decodetools.colldada;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Asset {
    @XmlElement
    private Contributor contributor = new Contributor();
    @XmlElement(required = true)
    private LocalDateTime created = LocalDateTime.now();
    @XmlElement(required = true)
    private LocalDateTime modified = LocalDateTime.now();
    @XmlElement
    private String keywords;
    @XmlElement
    private String revision;
    @XmlElement
    private String title;
    @XmlElement
    private String subjects;
    @XmlElement
    private Unit unit = new Unit("centimeter", 1);
    @XmlElement(name = "up_axis")
    private Axis upAxis = Axis.X_UP;
    

    enum Axis {
        X_UP,
        Y_UP,
        Z_UP;
    }

    static class Unit {
        @XmlAttribute(required = true)
        private String name = "";
        @XmlAttribute(required = true)
        private double meter = 1.0;
        
        private Unit() { }
        
        public Unit(String name, double meter) {
            this.name = name;
            this.meter = meter;
        }
    }
    
    static class Contributor {
        @XmlElement
        private String author;
        @XmlElement(name = "authoring_tool")
        private String tool;
        @XmlElement
        private String comments;
        @XmlElement
        private String copyright;
        @XmlElement(name = "source_data")
        private String source;
    }
}