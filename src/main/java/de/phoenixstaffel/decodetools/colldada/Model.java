package de.phoenixstaffel.decodetools.colldada;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.stream.FactoryConfigurationError;

//Specs: https://www.khronos.org/files/collada_spec_1_4.pdf
@XmlRootElement(name = "COLLADA")
public class Model {
    @XmlAttribute
    private String xmlns = "http://www.collada.org/2005/11/COLLADASchema";
    @XmlAttribute
    private String version = "1.4.1";

    @XmlElement
    private Asset asset = new Asset();
    
    @XmlElementWrapper(name = "library_images")
    @XmlElement(name="image")
    private List<Image> images = new ArrayList<>(); 
    
    @XmlElementWrapper(name = "library_effects")
    @XmlElement(name="effect")
    private List<Effect> effects = new ArrayList<>(); 

    public static void main(String[] args) throws JAXBException, FactoryConfigurationError, FileNotFoundException {
        Model m = new Model();
        
        JAXBContext context = JAXBContext.newInstance(Model.class);
        Marshaller marshaller = context.createMarshaller();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
        Model mm = (Model) unmarshaller.unmarshal(new FileInputStream(new File("agumon.dae")));
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        marshaller.marshal(mm, System.out);
    }
}


class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    @Override
    public LocalDateTime unmarshal(String input) throws Exception {
        if(input.endsWith("Z"))
            input = input.substring(0, input.length()-1);
        return LocalDateTime.parse(input);
    }
    
    @Override
    public String marshal(LocalDateTime input) throws Exception {
        return input.toString();
    }
}