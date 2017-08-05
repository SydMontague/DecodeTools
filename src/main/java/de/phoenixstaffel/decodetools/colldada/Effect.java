package de.phoenixstaffel.decodetools.colldada;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

//TODO implement if needed
public class Effect {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    
    @XmlElement(name = "profile_COMMON")
    private CommonProfile profile;
    
    
    
    static class CommonProfile {
        
        private List<NewParam> newparam = new ArrayList<>();
    }
    
    static class NewParam {
        @XmlAttribute
        private String sid;
        
        
    }
}
