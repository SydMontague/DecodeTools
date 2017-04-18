package de.phoenixstaffel.decodetools.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileFilter;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.res.payload.GMIOFile;

public class GMIOPanel extends PayloadPanel {
    private static final long serialVersionUID = -4042970327489697448L;
    
    private GMIOFile selectedGMIO = null;
    
    private final JButton exportButton = new JButton("Export");
    private final JButton importButton = new JButton("Import");
    private final JImage image = new JImage();
    
    public GMIOPanel(Object selected) {
        setSelectedFile(selected);
        
        exportButton.setAction(new ExportAction());
        importButton.setAction(new ImportAction());
        
        image.setMinimumSize(new Dimension(100, 100));
        image.setBackground(Color.LIGHT_GRAY);
        
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(image, GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(exportButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(importButton)))
                    .addContainerGap())
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
        );
        setLayout(groupLayout);
        //@formatter:on
    }
    
    @Override
    public void setSelectedFile(Object file) {
        this.selectedGMIO = null;
        if (file instanceof GMIOFile)
            this.selectedGMIO = (GMIOFile) file;
        
        image.setImage(selectedGMIO == null ? null : selectedGMIO.getImage());
    }
    
    public GMIOFile getSelectedFile() {
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
                
                ImageIO.write(getSelectedFile().getImage(), "PNG", file);
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
                getSelectedFile().setImage(localImage);
                getImage().setImage(getSelectedFile().getImage());
            }
            catch (IOException ex) {
                Main.LOGGER.log(Level.WARNING, "Could not read image file, not an image?", ex);
            }
        }
    }
}
