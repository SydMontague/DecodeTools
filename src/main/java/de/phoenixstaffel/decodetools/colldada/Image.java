package de.phoenixstaffel.decodetools.colldada;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Image {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String format;
    @XmlAttribute
    private Integer height;
    @XmlAttribute
    private Integer width;
    @XmlAttribute
    private Integer depth;
    
    @XmlElement
    private Asset asset;
    
    @XmlElement(name = "init_from", required = true)
    private String source = "";
}
