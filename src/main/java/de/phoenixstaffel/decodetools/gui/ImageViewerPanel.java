package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.res.payload.GMIOFile;

public class ImageViewerPanel extends EditorPanel {
    private static final long serialVersionUID = 4301317831427884206L;
    
    private final JScrollPane scrollPane = new JScrollPane();
    final JList<GMIOFile> list = new JList<>();
    final JImage image = new JImage();
    private final JButton btnExport = new JButton("Export");
    private final JButton btnImport = new JButton("Import");
    
    public ImageViewerPanel(EditorModel model) {
        super(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(a -> {
            GMIOFile selected = list.getSelectedValue();
            image.setImage(selected != null ? selected.getImage() : null);
        });
        
        btnExport.setAction(new ExportAction());
        btnImport.setAction(new ImportAction());
        
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(2)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(btnExport)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnImport))
                        .addComponent(image, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnExport)
                        .addComponent(btnImport))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(image, GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE))
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        //@formatter:on
        
        scrollPane.setViewportView(list);
        setLayout(groupLayout);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (list.getModel() != getModel().getImageListModel())
            list.setModel(getModel().getImageListModel());
    }
    
    class ExportAction extends AbstractAction {
        private static final long serialVersionUID = 7773708520423706131L;
        
        public ExportAction() {
            super("Export");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            GMIOFile selected = list.getSelectedValue();
            
            if (selected == null)
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
                
                ImageIO.write(selected.getImage(), "PNG", file);
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
            GMIOFile selected = list.getSelectedValue();
            
            if (selected == null)
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showOpenDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try {
                BufferedImage localImage = ImageIO.read(fileDialogue.getSelectedFile());
                selected.setImage(localImage);
                image.setImage(selected.getImage());
            }
            catch (IOException ex) {
                Main.LOGGER.log(Level.WARNING, "Could not read image file, not an image?", ex);
            }
        }
    }
}
