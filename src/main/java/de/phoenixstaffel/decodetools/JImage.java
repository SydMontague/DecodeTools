package de.phoenixstaffel.decodetools;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class JImage extends JComponent {
    private Image image;

    public JImage(Image image) {
        this.image = image;
    }

    public JImage() {
    }

    public void setImage(Image image) {
        this.image = image;
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
