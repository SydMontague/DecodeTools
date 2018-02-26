package de.phoenixstaffel.decodetools.export.fontxml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload.TNFOEntry;
/**
 * Intermediate format for fonts read from Romsstar Font XML format.
 * 
 * It stores the image resolution, the names of the "pages" (different image files) and the seperate chars.
 * This data can either be polled or //TODO converted into a KPTF.
 */
public class XMLFont {
    //used for calculating TNFOEntry coordinates
    private int imageWidth;
    private int imageHeight;
    private int fontSize;
    
    private Map<Integer, String> pageMap = new HashMap<>();
    private List<Char> charList = new ArrayList<>();
    
    public XMLFont(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        
        Node info = document.getElementsByTagName("info").item(0);
        fontSize = Integer.parseInt(info.getAttributes().getNamedItem("size").getNodeValue());
        
        Node common = document.getElementsByTagName("common").item(0);
        
        imageWidth = Integer.parseInt(common.getAttributes().getNamedItem("scaleW").getNodeValue());
        imageHeight = Integer.parseInt(common.getAttributes().getNamedItem("scaleH").getNodeValue());
        
        Node pages = document.getElementsByTagName("pages").item(0);
        NodeList pageNodes = pages.getChildNodes();
        
        for (int i = 0; i < pageNodes.getLength(); ++i) {
            Node item = pageNodes.item(i);
            
            // filter out non-<page> nodes, e.g. the whitespace between tags that get recognized as text
            if (!item.getNodeName().equals("page"))
                continue;
            
            int id = Integer.parseInt(item.getAttributes().getNamedItem("id").getNodeValue());
            String imageFile = item.getAttributes().getNamedItem("id").getNodeValue();
            pageMap.put(id, imageFile);
        }
        
        Node chars = document.getElementsByTagName("chars").item(0);
        NodeList charNodes = chars.getChildNodes();
        
        for (int i = 0; i < charNodes.getLength(); ++i) {
            Node item = charNodes.item(i);
            
            // filter out non-<char> nodes, e.g. the whitespace between tags that get recognized as text
            if (!item.getNodeName().equals("char"))
                continue;
            
            charList.add(new Char(item));
        }
    }
    
    /**
     * Get the height of the image this font uses.
     * 
     * @return the height of the image
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Get the width of the image this font uses.
     * 
     * @return the width of the image
     */
    public int getImageWidth() {
        return imageWidth;
    }
    
    /**
     * Gets a list of the {@link Char}s within this font.
     *  
     * @return the list of Chars in this font
     */
    public List<Char> getChars() {
        return charList;
    }
    
    /**
     * Gets the font size the font is supposed to use, identified by it's <info> tag.
     * 
     * @return the font size
     */
    public int getFontSize() {
        return fontSize;
    }
    
    /**
     * Represents a single character within the Font.
     */
    public class Char {
        private char id; //the represented character
        private int x; //upper left x coordinate on the image
        private int y; //upper left y coordinate on the image
        private int width; //width of the character and on the sub-image
        private int height; //height of the character and on the sub-image
        private int xTranslation; 
        private int yTranslation;
        private int xAdvance; //how much to advance on the X axis when printing the character
        private int page; //the index of the image to use for this character
        
        public Char(Node node) {
            NamedNodeMap attr = node.getAttributes();
            
            id = (char) Integer.parseInt(attr.getNamedItem("id").getNodeValue());
            x = Integer.parseInt(attr.getNamedItem("x").getNodeValue());
            y = Integer.parseInt(attr.getNamedItem("y").getNodeValue());
            width = Integer.parseInt(attr.getNamedItem("width").getNodeValue());
            height = Integer.parseInt(attr.getNamedItem("height").getNodeValue());
            xTranslation = Integer.parseInt(attr.getNamedItem("xoffset").getNodeValue());
            yTranslation = Integer.parseInt(attr.getNamedItem("yoffset").getNodeValue());
            xAdvance = Integer.parseInt(attr.getNamedItem("xadvance").getNodeValue());
            page = Integer.parseInt(attr.getNamedItem("page").getNodeValue());
        }
        
        public TNFOEntry toTNFOEntry() {
            //TODO check if the calculations are correct
            TNFOEntry entry = new TNFOEntry();
            
            entry.setGmioId((short) page);
            
            //convert coordinates into doubles, the Y axis is mirrored for TNFO
            entry.setX1((double) x / getImageWidth());
            entry.setX2((double) (x + width) / getImageWidth());
            entry.setY1(1 - (double) y / getImageHeight());
            entry.setY2(1 - (double) (y + height) / getImageHeight());
            
            entry.setWidth((byte) width);
            entry.setHeight((byte) height);
            entry.setTextWidth((byte) xAdvance);
            entry.setXTranslation((byte) xTranslation);
            entry.setYTranslation((byte) (getFontSize() - yTranslation)); 
            
            return entry;
        }
        
        public char getChar() {
            return id;
        }
        
        public void setChar(char id) {
            this.id = id;
        }
        
        public int getX() {
            return x;
        }
        
        public void setX(int x) {
            this.x = x;
        }
        
        public int getY() {
            return y;
        }
        
        public void setY(int y) {
            this.y = y;
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public int getXTranslation() {
            return xTranslation;
        }
        
        public void setXTranslation(int xTranslation) {
            this.xTranslation = xTranslation;
        }
        
        public int getYTranslation() {
            return yTranslation;
        }
        
        public void setYTranslation(int yTranslation) {
            this.yTranslation = yTranslation;
        }
        
        public int getXAdvance() {
            return xAdvance;
        }
        
        public void setxAdvance(int xAdvance) {
            this.xAdvance = xAdvance;
        }
        
        public int getPage() {
            return page;
        }
        
        public void setPage(int page) {
            this.page = page;
        }
        
    }
}
