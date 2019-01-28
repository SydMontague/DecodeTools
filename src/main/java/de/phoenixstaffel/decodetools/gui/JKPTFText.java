package de.phoenixstaffel.decodetools.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload.TNFOEntry;

public class JKPTFText extends JComponent {
    private static final long serialVersionUID = 2059603808794522704L;
    
    private static final int TEXT_WIDTH = 400;
    private static final int TEXT_HEIGHT = 240;
    
    private transient BufferedImage textBoxImage;
    
    private String text = "";
    private int fontSize = 0;
    private int widespace = 0;
    private int lineHeight = 15;
    
    private int startX = 0;
    private int startY = 0;
    private double resolutionScale = 1;

    private transient TNFOPayload tnfo;
    private transient List<GMIOPayload> gmios;
    
    private boolean showTextbox = true;
    
    public JKPTFText() {
        super();
        setPreferredSize(new Dimension((int) (TEXT_WIDTH * resolutionScale), (int) (TEXT_HEIGHT * resolutionScale)));
        try {
            textBoxImage = ImageIO.read(getClass().getClassLoader().getResource("textbox.png"));
        }
        catch (IOException e) {
            Main.LOGGER.log(Level.WARNING, "Error while loading Textbox image.", e);
        }
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        //TODO cleanup
        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, (int) (TEXT_WIDTH * resolutionScale), (int) (TEXT_HEIGHT * resolutionScale));
        g.setColor(getForeground());
        
        if(tnfo == null)
            return;
        
        Graphics2D gg = (Graphics2D) g;

        if(showTextbox) {
            AffineTransform t2 = new AffineTransform();
            t2.scale(resolutionScale, resolutionScale);
            t2.translate(61, 172);
            gg.drawImage(textBoxImage, t2, null);
        }
        
        double x = startX * resolutionScale;
        int y = lineHeight + startY;
        
        double scale = resolutionScale * fontSize / tnfo.getReferenceSize();

        if(scale == 0)
            return;
        
        for(char c : text.toCharArray()) {
            switch(c) {
                case '\r':
                    break;
                case '\n':
                    x = startX * resolutionScale;
                    y += lineHeight;
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
                        Graphics2D b = subImage.createGraphics();
                        b.setComposite(AlphaComposite.SrcAtop);
                        b.setColor(getForeground());
                        b.fillRect(0, 0, subImage.getWidth(), subImage.getHeight());
                        b.dispose();

                        double localX = x + entry.getXTranslation() * scale;
                        double localY = (y - entry.getYTranslation() + tnfo.getYOffset()) * scale - scale;

                        if(localY < 0)
                            continue;
                        
                        double textureScaleX = (double) Byte.toUnsignedInt(entry.getWidth()) / (x2 - x1);
                        double textureScaleY = (double) Byte.toUnsignedInt(entry.getHeight()) / (y1 - y2);

                        AffineTransform t = new AffineTransform();
                        t.translate(localX, localY);
                        t.scale(scale, scale);
                        t.scale(textureScaleX, textureScaleY);
                        
                        AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);

                        BufferedImage ii = op.filter(subImage, null);
                        gg.drawImage(ii, null, null);
                    }
                    
                    x += Byte.toUnsignedInt(entry.getTextWidth()) * scale;
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
        setPreferredSize(new Dimension((int) (TEXT_WIDTH * resolutionScale), (int) (TEXT_HEIGHT * resolutionScale)));
    }

    public void setDisplayTextbox(boolean selected) {
        this.showTextbox = selected;
    }
}
