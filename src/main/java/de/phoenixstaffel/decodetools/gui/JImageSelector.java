package de.phoenixstaffel.decodetools.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;

public class JImageSelector extends JPanel {
    private static final long serialVersionUID = -8906353552698868618L;
    private static final int SELECTION_AREA_SIZE = 3;
    
    private final JImage image = new JImage();
    
    private Rectangle selection = new Rectangle(-10, -10, 0, 0);
    private int scale = 1;
    
    public JImageSelector() {
        this(null);
    }
    
    public JImageSelector(Image sourceImage) {
        image.addPropertyChangeListener("image", a -> updateSize());
        image.setImage(sourceImage);
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                  .addComponent(image, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(image, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE));
        setLayout(groupLayout);
        
        image.addMouseMotionListener(new SelectorMouseListener());
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
    
    public Rectangle getSelection() {
        return selection;
    }
    
    public void setSelection(Rectangle rect) {
        int x1 = (int) rect.getMinX();
        int x2 = (int) rect.getMaxX();
        int y1 = (int) rect.getMinY();
        int y2 = (int) rect.getMaxY();
        
        x1 = x1 < 0 ? 0 : x1;
        x1 = x1 > image.getWidth() ? image.getWidth() : x1;
        x2 = x2 < 0 ? 0 : x2;
        x2 = x2 > image.getWidth() ? image.getWidth() : x2;
        y1 = y1 < 0 ? 0 : y1;
        y1 = y1 > image.getHeight() ? image.getHeight() : y1;
        y2 = y2 < 0 ? 0 : y2;
        y2 = y2 > image.getHeight() ? image.getHeight() : y2;
        
        this.selection = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        
        firePropertyChange("selection", null, this.selection);
    }
    
    public JImage getImage() {
        return image;
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(scale, scale);
        super.paint(g);
        Area a = new Area(image.getBounds());
        a.subtract(new Area(selection));
        
        Rectangle sel = new Rectangle(selection);
        sel.grow(1, 1);
        Area a2 = new Area(sel);
        a2.subtract(new Area(selection));
        
        g2d.setColor(new Color(0, 0, 0, 64));
        g2d.fill(a);
        g2d.setColor(Color.BLACK);
        g2d.fill(a2);
    }
    
    public int getScale() {
        return scale;
    }
    
    public void setScale(int scale) {
        this.scale = scale < 1 ? 1 : scale;
        updateSize();
        repaint();
    }
    
    void updateSize() {
        int width = image.getImage() == null ? 64 : image.getImage().getWidth(null);
        int height = image.getImage() == null ? 64 : image.getImage().getHeight(null);
        
        setPreferredSize(new Dimension(width * scale, height * scale));
        revalidate();
    }
    
    class SelectorMouseListener implements MouseMotionListener {
        private Point start;
        private Direction dir;
        private Rectangle localSelection;
        
        @Override
        public void mouseMoved(MouseEvent e) {
            start = e.getPoint();
            start = new Point(start.x / getScale(), start.y / getScale());
            
            double distanceS = getSelection().getMaxY() - start.getY();
            double distanceN = getSelection().getMinY() - start.getY();
            double distanceE = getSelection().getMaxX() - start.getX();
            double distanceW = getSelection().getMinX() - start.getX();
            
            int vert = 0;
            int hor = 0;
            
            if (Math.abs(distanceN) <= SELECTION_AREA_SIZE || Math.abs(distanceS) <= SELECTION_AREA_SIZE)
                vert = Math.abs(distanceS) < Math.abs(distanceN) ? -1 : 1;
            
            if (Math.abs(distanceE) <= SELECTION_AREA_SIZE || Math.abs(distanceW) <= SELECTION_AREA_SIZE)
                hor = Math.abs(distanceW) < Math.abs(distanceE) ? -1 : 1;
            
            Rectangle d = new Rectangle(getSelection());
            d.grow(SELECTION_AREA_SIZE, SELECTION_AREA_SIZE);
            
            dir = Direction.valueOf(vert, hor);
            
            if (!d.contains(start))
                dir = Direction.NEUTRAL;
            
            if (dir == Direction.NEUTRAL && getSelection().contains(start.x, start.y))
                dir = Direction.INSIDE;
            
            setCursor(new Cursor(dir.getCursorType()));
            localSelection = new Rectangle(getSelection());
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            Point end = e.getPoint();
            end = new Point(end.x / getScale(), end.y / getScale());
            
            if (dir == Direction.INSIDE) {
                Rectangle r = new Rectangle(localSelection);
                r.translate(end.x - start.x, end.y - start.y);
                setSelection(r);
            }
            else if (dir == Direction.NEUTRAL) {
                int x = start.x > end.x ? end.x : start.x;
                int y = start.y > end.y ? end.y : start.y;
                setSelection(new Rectangle(new Point(x, y), new Dimension(Math.abs(start.x - end.x), Math.abs(start.y - end.y))));
            }
            else {
                handleResize(end);
                // TODO fix/improve behaviour when going into "negative" width
            }
            repaint();
        }
        
        private void handleResize(Point end) {
            int x = dir.getX();
            int y = dir.getY();
            
            int x1 = (int) getSelection().getMinX();
            int x2 = (int) getSelection().getMaxX();
            int y1 = (int) getSelection().getMinY();
            int y2 = (int) getSelection().getMaxY();
            
            if (x == -1)
                x1 = end.x;
            else if (x == 1)
                x2 = end.x;
            
            if (y == -1)
                y2 = end.y;
            else if (y == 1)
                y1 = end.y;
            
            int finalX1 = x1 < x2 ? x1 : x2;
            int finalX2 = x1 < x2 ? x2 : x1;
            int finalY1 = y1 < y2 ? y1 : y2;
            int finalY2 = y1 < y2 ? y2 : y1;
            
            setSelection(new Rectangle(finalX1, finalY1, finalX2 - finalX1, finalY2 - finalY1));
        }
    }
    
    enum Direction {
        NORTH(1, 0, Cursor.N_RESIZE_CURSOR),
        NORTH_EAST(1, -1, Cursor.NW_RESIZE_CURSOR),
        EAST(0, -1, Cursor.E_RESIZE_CURSOR),
        SOUTH_EAST(-1, -1, Cursor.SW_RESIZE_CURSOR),
        SOUTH(-1, 0, Cursor.S_RESIZE_CURSOR),
        SOUTH_WEST(-1, 1, Cursor.SE_RESIZE_CURSOR),
        WEST(0, 1, Cursor.W_RESIZE_CURSOR),
        NORTH_WEST(1, 1, Cursor.NE_RESIZE_CURSOR),
        NEUTRAL(0, 0, Cursor.CROSSHAIR_CURSOR),
        INSIDE(Integer.MAX_VALUE, Integer.MAX_VALUE, Cursor.MOVE_CURSOR);
        
        private int vert;
        private int hor;
        private int cursorType;
        
        private Direction(int vert, int hor, int cursorType) {
            this.vert = vert;
            this.hor = hor;
            this.cursorType = cursorType;
        }
        
        public int getX() {
            return hor;
        }
        
        public int getY() {
            return vert;
        }
        
        public static Direction valueOf(int vertical, int horizontal) {
            if (Math.abs(vertical) > 1 || Math.abs(horizontal) > 1)
                throw new IllegalArgumentException("Values provided must be either -1, 0 or 1");
            
            for (Direction dir : values())
                if (dir.hor == horizontal && dir.vert == vertical)
                    return dir;
                
            throw new IllegalStateException("Couldn't find a matching direction. SHOULD NOT BE POSSIBLE!!!");
        }
        
        public int getCursorType() {
            return cursorType;
        }
    }

    public void fireSelectionUpdate() {
        firePropertyChange("selection", null, this.selection);
    }
}
