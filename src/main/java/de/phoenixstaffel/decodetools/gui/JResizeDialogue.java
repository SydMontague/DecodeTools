package de.phoenixstaffel.decodetools.gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;

public class JResizeDialogue extends JFrame {
    private static final long serialVersionUID = -1409196592444942514L;
    
    private static final Font MONOSPACED_FONT = new Font("Courier New", Font.PLAIN, 14);
    
    private transient List<Image> images;
    private int selected = 0;
    
    final JImageSelector imageSelector;
    private final JLabel lblX = new JLabel("X: 0000");
    private final JLabel lblY = new JLabel("Y: 0000");
    private final JLabel lblW = new JLabel("W: 0000");
    private final JLabel lblH = new JLabel("H: 0000");
    private final JButton zoomOutButton = new JButton("-");
    private final JButton zoomInButton = new JButton("+");
    final JScrollPane scrollPane = new JScrollPane();
    private final JSpinner spinner = new JSpinner();
    private final JLabel lblGmioId = new JLabel("GMIO Id");
    
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
            lblX.setText("X: " + (int) selection.getMinX());
            lblY.setText("Y: " + (int) selection.getMinY());
            lblW.setText("W: " + (int) selection.getWidth());
            lblH.setText("H: " + (int) selection.getHeight());
        });
        
        spinner.addChangeListener(a -> setSelectedImage((int) spinner.getValue()));
        
        zoomInButton.setAction(new AbstractAction("+") {
            private static final long serialVersionUID = 8463305912499686809L;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                imageSelector.setScale(imageSelector.getScale() + 1);
                scrollPane.getVerticalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
                scrollPane.getHorizontalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
            }
        });
        zoomOutButton.setAction(new AbstractAction("-") {
            private static final long serialVersionUID = 8117574193558765559L;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                imageSelector.setScale(imageSelector.getScale() - 1);
                scrollPane.getVerticalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
                scrollPane.getHorizontalScrollBar().setUnitIncrement(imageSelector.getScale() * 2);
            }
        });
        
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblX)
                                .addComponent(lblY)
                                .addComponent(lblW)
                                .addComponent(lblH)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(zoomOutButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(zoomInButton))
                                .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(23)
                            .addComponent(lblGmioId)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblX)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblY)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblW)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblH)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(zoomOutButton)
                        .addComponent(zoomInButton))
                    .addPreferredGap(ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                    .addComponent(lblGmioId)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
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
