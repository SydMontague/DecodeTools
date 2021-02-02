package de.phoenixstaffel.decodetools.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileFilter;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.PixelFormat;
import de.phoenixstaffel.decodetools.gui.util.JImage;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import javax.swing.JSpinner;

public class GMIOPanel extends PayloadPanel {
    private static final long serialVersionUID = -4042970327489697448L;
    
    private GMIOPayload selectedGMIO = null;
    
    private final JButton exportButton = new JButton("Export");
    private final JButton importButton = new JButton("Import");
    private final JImage image = new JImage();
    private final JPanel panel = new JPanel();
    private final JSeparator separator = new JSeparator();
    private final JComboBox<PixelFormat> comboBox = new JComboBox<>();
    private final JLabel lblFormat = new JLabel("Format");
    private final JLabel resolution = new JLabel("0x0");
    private final JLabel lblUvWidth = new JLabel("UV Width");
    private final JLabel lblUvHeight = new JLabel("UV Height");
    private final JSpinner uvWidthSpinner = new JSpinner();
    private final JSpinner uvHeightSpinner = new JSpinner();
    
    public GMIOPanel(Object selected) {
        setSelectedFile(selected);
        
        exportButton.setAction(new ExportAction());
        importButton.setAction(new ImportAction());
        
        image.setMinimumSize(new Dimension(100, 100));
        image.setBackground(Color.LIGHT_GRAY);
        comboBox.setModel(new DefaultComboBoxModel<>(PixelFormat.values()));
        comboBox.addItemListener(a -> {
           if(a.getStateChange() != ItemEvent.SELECTED || selectedGMIO == null)
               return;
           
           selectedGMIO.setFormat((PixelFormat) a.getItem());
        });
        
        uvWidthSpinner.addChangeListener(a -> selectedGMIO.setUVWidthAbsolute((int) uvWidthSpinner.getValue()));
        uvHeightSpinner.addChangeListener(a -> selectedGMIO.setUVHeightAbsolute((int) uvHeightSpinner.getValue()));
        
        //@formatter:off
        panel.setBorder(new MatteBorder(0, 1, 0, 0, new Color(0, 0, 0)));
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                            .addComponent(exportButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(importButton))
                        .addComponent(image, GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(11)
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE))
        );
        
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(exportButton)
                        .addComponent(importButton))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(image, GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                    .addContainerGap())
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(separator, GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
        );
        
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addGap(10)
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(lblFormat)
                            .addContainerGap(125, Short.MAX_VALUE))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                            .addGap(10)
                            .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel.createSequentialGroup()
                                    .addComponent(resolution, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                    .addGap(4))
                                .addGroup(gl_panel.createSequentialGroup()
                                    .addComponent(lblUvHeight)
                                    .addContainerGap())
                                .addGroup(gl_panel.createSequentialGroup()
                                    .addComponent(uvHeightSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())))))
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addComponent(uvWidthSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblUvWidth))
                    .addContainerGap(99, Short.MAX_VALUE))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblFormat)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(resolution))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUvWidth)
                        .addComponent(lblUvHeight))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(uvWidthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uvHeightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(353, Short.MAX_VALUE))
        );
        
        panel.setLayout(gl_panel);
        setLayout(groupLayout);
        //@formatter:on
    }
    
    @Override
    public void setSelectedFile(Object file) {
        this.selectedGMIO = null;
        if (file instanceof GMIOPayload)
            this.selectedGMIO = (GMIOPayload) file;
        
        image.setImage(selectedGMIO == null ? null : selectedGMIO.getImage());
        comboBox.setSelectedItem(selectedGMIO == null ? null : selectedGMIO.getFormat());
        if(this.selectedGMIO != null) {
            uvHeightSpinner.setModel(new SpinnerNumberModel(selectedGMIO.getUVHeightAbsolute(), 0, null, 1));
            uvWidthSpinner.setModel(new SpinnerNumberModel(selectedGMIO.getUVWidthAbsolute(), 0, null, 1));
        }
        resolution.setText(selectedGMIO == null ? null : selectedGMIO.getWidth() + "x" + selectedGMIO.getHeight());
    }
    
    public GMIOPayload getSelectedFile() {
        return selectedGMIO;
    }
    
    protected JImage getImage() {
        return image;
    }
    
    class ExportAction extends AbstractAction {
        private static final long serialVersionUID = 7773708520423706131L;
        
        public ExportAction() {
            super("Export");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (getSelectedFile() == null)
                return;
            
            BufferedImage lImage = getSelectedFile().getImage();
            
            if(lImage == null)
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Output");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.setFileFilter(new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isDirectory();
                }
                
                @Override
                public String getDescription() {
                    return "PNG Image File (.png)";
                }
            });
            fileDialogue.showSaveDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try {
                File file;
                if (fileDialogue.getSelectedFile().getName().endsWith(".png"))
                    file = fileDialogue.getSelectedFile();
                else
                    file = new File(fileDialogue.getSelectedFile().getPath() + ".png");
                
                ImageIO.write(lImage, "PNG", file);
            }
            catch (IOException ex) {
                Main.LOGGER.log(Level.WARNING, "Could not read image file, not an image?", ex);
            }
        }
    }
    
    class ImportAction extends AbstractAction {
        private static final long serialVersionUID = 7376798961551919059L;
        
        public ImportAction() {
            super("Import");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (getSelectedFile() == null)
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showOpenDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try {
                BufferedImage localImage = ImageIO.read(fileDialogue.getSelectedFile());
                if (getSelectedFile().setImage(localImage)) {
                    setSelectedFile(getSelectedFile());
                    uvHeightSpinner.setValue(getSelectedFile().getUVHeightAbsolute());
                    uvWidthSpinner.setValue(getSelectedFile().getUVWidthAbsolute());
                }
                else
                    JOptionPane.showMessageDialog(null, "Couldn't set image. Please check the log.");
            }
            catch (IOException ex) {
                Main.LOGGER.log(Level.WARNING, "Could not read image file, not an image?", ex);
            }
        }
    }
}
