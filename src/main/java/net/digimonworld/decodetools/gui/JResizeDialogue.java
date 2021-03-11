package net.digimonworld.decodetools.gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.digimonworld.decodetools.gui.util.FunctionAction;
import net.digimonworld.decodetools.gui.util.JImageSelector;

import javax.swing.SpinnerNumberModel;

public class JResizeDialogue extends JFrame {
    private static final long serialVersionUID = -1409196592444942514L;
    
    private static final Font MONOSPACED_FONT = new Font("Courier New", Font.PLAIN, 14);
    
    private transient List<Image> images;
    private int selected = 0;
    
    final JImageSelector imageSelector;
    private final JLabel lblX = new JLabel("X: ");
    private final JLabel lblY = new JLabel("Y: ");
    private final JLabel lblW = new JLabel("W: ");
    private final JLabel lblH = new JLabel("H: ");
    private final JButton zoomOutButton = new JButton("-");
    private final JButton zoomInButton = new JButton("+");
    final JScrollPane scrollPane = new JScrollPane();
    private final JSpinner spinner = new JSpinner();
    private final JLabel lblGmioId = new JLabel("GMIO Id");
    private final JSpinner xSpinner = new JSpinner();
    private final JSpinner ySpinner = new JSpinner();
    private final JSpinner widthSpinner = new JSpinner();
    private final JSpinner heightSpinner = new JSpinner();
    
    public JResizeDialogue(List<Image> images) {
        setBounds(0, 0, 500, 300);
        setImages(images);
        
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        imageSelector = new JImageSelector();
        lblX.setFont(MONOSPACED_FONT);
        lblY.setFont(MONOSPACED_FONT);
        lblW.setFont(MONOSPACED_FONT);
        lblH.setFont(MONOSPACED_FONT);
        lblGmioId.setFont(MONOSPACED_FONT);
        
        imageSelector.addPropertyChangeListener("selection", a -> {
            Rectangle selection = (Rectangle) a.getNewValue();
            xSpinner.setValue(selection.getMinX());
            ySpinner.setValue(selection.getMinY());
            widthSpinner.setValue(selection.getWidth());
            heightSpinner.setValue(selection.getHeight());
        });
        
        xSpinner.addChangeListener(a -> {
            imageSelector.getSelection().x = ((Number) xSpinner.getValue()).intValue();
            imageSelector.fireSelectionUpdate();
            imageSelector.repaint();
        });
        ySpinner.addChangeListener(a -> {
            imageSelector.getSelection().y = ((Number) ySpinner.getValue()).intValue();
            imageSelector.fireSelectionUpdate();
            imageSelector.repaint();
        });
        widthSpinner.addChangeListener(a -> {
            imageSelector.getSelection().width = ((Number) widthSpinner.getValue()).intValue();
            imageSelector.fireSelectionUpdate();
            imageSelector.repaint();
        });
        heightSpinner.addChangeListener(a -> {
            imageSelector.getSelection().height = ((Number) heightSpinner.getValue()).intValue();
            imageSelector.fireSelectionUpdate();
            imageSelector.repaint();
        });
        
        spinner.addChangeListener(a -> setSelectedImage((int) spinner.getValue()));
        
        zoomInButton.setAction(new FunctionAction("+", a -> {
            imageSelector.setScale(imageSelector.getScale() + 1);
            scrollPane.getVerticalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
        }));
        
        zoomOutButton.setAction(new FunctionAction("-", a -> {
            imageSelector.setScale(imageSelector.getScale() - 1);
            scrollPane.getVerticalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
        }));
        
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblY)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(ySpinner, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblW)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(widthSpinner, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblH)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(heightSpinner, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
                                .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblX)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(xSpinner))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(zoomOutButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(zoomInButton))))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(23)
                            .addComponent(lblGmioId)))
                    .addGap(7)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblX)
                        .addComponent(xSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblY)
                        .addComponent(ySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblW)
                        .addComponent(widthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblH)
                        .addComponent(heightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(zoomOutButton)
                        .addComponent(zoomInButton))
                    .addPreferredGap(ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                    .addComponent(lblGmioId)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        );
        scrollPane.setViewportView(imageSelector);
        GroupLayout gl_imageSelector = new GroupLayout(imageSelector);
        gl_imageSelector.setHorizontalGroup(
            gl_imageSelector.createParallelGroup(Alignment.LEADING)
                .addComponent(imageSelector.getImage(), GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
        );
        gl_imageSelector.setVerticalGroup(
            gl_imageSelector.createParallelGroup(Alignment.LEADING)
                .addComponent(imageSelector.getImage(), GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
        );
        imageSelector.setLayout(gl_imageSelector);
        getContentPane().setLayout(groupLayout);
        //@formatter:on
    }
    
    public void setSelectedImage(int gmioId) {
        this.imageSelector.getImage().setImage(images.get(gmioId));
        int old = selected;
        this.selected = gmioId;
        this.spinner.setValue(gmioId);
        firePropertyChange("selected", old, selected);
    }
    
    public int getSelectedImage() {
        return selected;
    }
    
    public void setImages(List<Image> images) {
        this.images = images;
        spinner.setModel(new SpinnerNumberModel(0, 0, Math.max(images.size() - 1, 0), 1));
    }
}
