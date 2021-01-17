package de.phoenixstaffel.decodetools.gui.util;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class JImage extends JComponent {
    private static final long serialVersionUID = 4941666770159805904L;
    
    private transient Image image;
    
    public JImage(Image image) {
        setImage(image);
    }
    
    public JImage() {
        // nothing to initialise
    }
    
    public void setImage(Image image) {
        this.image = image;
        if (image != null)
            this.setSize(image.getWidth(null), image.getHeight(null));

        this.firePropertyChange("image", null, null);
        repaint();
    }
    
    public Image getImage() {
        return image;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
