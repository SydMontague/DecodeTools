package de.phoenixstaffel.decodetools.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JComponent;

import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload.TNFOEntry;

public class JKPTFText extends JComponent {
    private static final int WIDTH = 400;
    
    private String text = "";
    private int fontSize = 0;
    private int widespace = 0;
    
    private int lineHeight = 15; //FIXME add line height to GUI

    private TNFOPayload tnfo;
    private List<GMIOPayload> gmios;
    
    //space width in px
    //FIXME linear scaling
    //translation does what is says (translate the rendering, not the logic) //TODO verify X translation is working
    //width and height are size of the rendering canvas (2D billboard)
    
    //text width -> how much it advances on the baseline
    
    //widespace -> extra space between characters, unscaled by font size!
    
    public void setText(String text) {
        this.text = text;
    }
    
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 500, 500);
        g.setColor(Color.WHITE);
        
        int x = 0;
        int y = lineHeight;

        for(char c : text.toCharArray()) {
            switch(c) {
                case '\r':
                    break;
                case '\n':
                    x = 0;
                    y += lineHeight;
                    break;
                case ' ':
                    x += tnfo.getSpaceWidth();
                    x += widespace;
                    break;
                default:
                    TNFOEntry entry = tnfo.getEntry(c);
                    
                    BufferedImage i = gmios.get(entry.getGmioId()).getImage();
                    int x1 = (int) Math.round(entry.getX1() * i.getWidth());
                    int x2 = (int) Math.round(entry.getX2() * i.getWidth());
                    int y1 = (int) Math.round(entry.getY1() * i.getHeight());
                    int y2 = (int) Math.round(entry.getY2() * i.getHeight());

                    if(x1 != x2 && y1 != y2) {
                        BufferedImage subImage = i.getSubimage(x1, i.getHeight() - y1, x2 - x1, y1 - y2);
                        g.drawImage(subImage, x + entry.getXTranslation(), y - entry.getYTranslation(), null);
                    }
                    
                    
                    
                    x += entry.getTextWidth();
                    x += widespace;
                
            }
            
            
            

            
            
            
        }
        
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setWidespace(int widespace) {
        this.widespace = widespace;
    }
    
    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }
    
    public void setTNFO(TNFOPayload tnfo) {
        this.tnfo = tnfo;
    }
    
    public void setGMIOs(List<GMIOPayload> gmios) {
        this.gmios = gmios;
    }


    public void update() {
        repaint();
    }
    
    
}
