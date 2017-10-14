package de.phoenixstaffel.decodetools.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload.TNFOEntry;

public class JKPTFText extends JComponent {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 240;
    
    private transient BufferedImage textBoxImage;
    
    private String text = "";
    private int fontSize = 0;
    private int widespace = 0;
    private int lineHeight = 15;
    
    private int startX = 0;
    private int startY = 0;
    private double resolutionScale = 1;

    private TNFOPayload tnfo;
    private List<GMIOPayload> gmios;
    
    private boolean showTextbox = true;
    
    //space width in px
    //FIXME linear scaling
    //translation does what is says (translate the rendering, not the logic) //TODO verify X translation is working
    //width and height are size of the rendering canvas (2D billboard)
    
    //text width -> how much it advances on the baseline
    
    //widespace -> extra space between characters, unscaled by font size!
    
    public JKPTFText() {
        super();
        setPreferredSize(new Dimension((int) (WIDTH * resolutionScale), (int) (HEIGHT * resolutionScale)));
        try {
            textBoxImage = ImageIO.read(getClass().getClassLoader().getResource("textbox.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int) (WIDTH * resolutionScale), (int) (HEIGHT * resolutionScale));
        g.setColor(Color.WHITE);
        
        if(tnfo == null)
            return;
        
        Graphics2D gg = (Graphics2D) g;

        if(showTextbox) {
            AffineTransform t2 = new AffineTransform();
            t2.scale(resolutionScale, resolutionScale);
            t2.translate(61, 172);
            gg.drawImage(textBoxImage, t2, null);
        }
        
        //TODO font color
        //TODO background color
        
        double x = startX * resolutionScale;
        int y = (int) ((lineHeight + startY) * resolutionScale);
        
        double scale = resolutionScale * fontSize / tnfo.getReferenceSize();

        if(scale == 0)
            return;
        
        for(char c : text.toCharArray()) {
            switch(c) {
                case '\r':
                    break;
                case '\n':
                    x = startX * resolutionScale;
                    y += lineHeight * resolutionScale;
                    break;
                case ' ':
                    x += tnfo.getSpaceWidth() * scale;
                    x += widespace * resolutionScale;
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

                        double localX = x + entry.getXTranslation() * scale;
                        double localY = y - entry.getYTranslation() * scale - scale;

                        if(localY < 0)
                            continue;
                        
                        double textureScaleX = (double) entry.getWidth() / (x2 - x1);
                        double textureScaleY = (double) entry.getHeight() / (y1 - y2);

                        AffineTransform t = new AffineTransform();
                        t.translate(localX, localY);
                        t.scale(scale, scale);
                        t.scale(textureScaleX, textureScaleY);
                        
                        AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);

                        BufferedImage ii = op.filter(subImage, null);
                        gg.drawImage(ii, null, null);
                    }
                    
                    x += entry.getTextWidth() * scale;
                    x += widespace * resolutionScale;
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

    public void setResolutionScale(double value) {
        this.resolutionScale = value;
    }

    public void setStartX(int value) {
        this.startX = value;
    }

    public void setStartY(int value) {
        this.startY = value;
    }
    
    public void setTNFO(TNFOPayload tnfo) {
        this.tnfo = tnfo;
    }
    
    public void setGMIOs(List<GMIOPayload> gmios) {
        this.gmios = gmios;
    }

    public void update() {
        repaint();
        setPreferredSize(new Dimension((int) (WIDTH * resolutionScale), (int) (HEIGHT * resolutionScale)));
    }
}
