package net.digimonworld.decodetools.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileFilter;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.PixelFormat;
import net.digimonworld.decodetools.gui.util.FunctionAction;
import net.digimonworld.decodetools.gui.util.JImage;
import net.digimonworld.decodetools.res.payload.GMIOPayload;
import net.digimonworld.decodetools.res.payload.GMIOPayload.TextureFiltering;
import net.digimonworld.decodetools.res.payload.GMIOPayload.TextureWrap;
import net.digimonworld.decodetools.res.payload.GMIOPayload.UnknownEnum;

public class GMIOPanel extends PayloadPanel {
    private static final long serialVersionUID = -4042970327489697448L;
    
    private transient Optional<GMIOPayload> selectedGMIO = Optional.empty();
    
    private final JButton exportButton = new JButton("Export");
    private final JButton importButton = new JButton("Import");
    private final JImage image = new JImage();
    private final JPanel panel = new JPanel();
    private final JSeparator separator = new JSeparator();
    private final JLabel resolution = new JLabel("0x0");
    private final JLabel lblUvWidth = new JLabel("UV Width");
    private final JLabel lblUvHeight = new JLabel("UV Height");
    private final JSpinner uvWidthSpinner = new JSpinner();
    private final JSpinner uvHeightSpinner = new JSpinner();

    private final JComboBox<PixelFormat> formatBox = new JComboBox<>();
    private final JComboBox<TextureWrap> wrapSBox = new JComboBox<>();
    private final JComboBox<TextureWrap> wrapTBox = new JComboBox<>();
    private final JComboBox<UnknownEnum> unknownBox = new JComboBox<>();
    private final JComboBox<TextureFiltering> minFilterBox = new JComboBox<>();
    private final JComboBox<TextureFiltering> magFilterBox = new JComboBox<>();
    private final JTextField fileNameField = new JTextField();
    private final JLabel lblFormat = new JLabel("Format");
    private final JLabel lblWrapS = new JLabel("Wrap S");
    private final JLabel lblWrapT = new JLabel("Wrap T");
    private final JLabel lblUnknown = new JLabel("Unknown");
    private final JLabel lblMinFilter = new JLabel("Min Filter");
    private final JLabel lblMagFilter = new JLabel("Mag Filter");
    private final JLabel lblFileName = new JLabel("File Name");
    private final JButton btnUpdateName = new JButton("Update Name");
    
    public GMIOPanel(Object selected) {
        setSelectedFile(selected);
        
        exportButton.setAction(new ExportAction());
        importButton.setAction(new ImportAction());
        
        image.setMinimumSize(new Dimension(100, 100));
        image.setBackground(Color.LIGHT_GRAY);
        
        formatBox.setModel(new DefaultComboBoxModel<>(PixelFormat.values()));
        wrapSBox.setModel(new DefaultComboBoxModel<>(TextureWrap.values()));
        wrapTBox.setModel(new DefaultComboBoxModel<>(TextureWrap.values()));
        unknownBox.setModel(new DefaultComboBoxModel<>(UnknownEnum.values()));
        minFilterBox.setModel(new DefaultComboBoxModel<>(TextureFiltering.values()));
        magFilterBox.setModel(new DefaultComboBoxModel<>(TextureFiltering.values()));
        
        btnUpdateName.setAction(new FunctionAction("Update Name", a -> selectedGMIO.ifPresent(b -> b.setName(fileNameField.getText()))));
        
        formatBox.addItemListener(a -> {
            if (a.getStateChange() != ItemEvent.SELECTED)
                return;
            
            selectedGMIO.ifPresent(b -> b.setFormat((PixelFormat) a.getItem()));
        });
        wrapSBox.addItemListener(a -> {
            if (a.getStateChange() != ItemEvent.SELECTED)
                return;
            
            selectedGMIO.ifPresent(b -> b.setWrapS((TextureWrap) a.getItem()));
        });
        wrapTBox.addItemListener(a -> {
            if (a.getStateChange() != ItemEvent.SELECTED)
                return;
            
            selectedGMIO.ifPresent(b -> b.setWrapT((TextureWrap) a.getItem()));
        });
        unknownBox.addItemListener(a -> {
            if (a.getStateChange() != ItemEvent.SELECTED)
                return;
            
            selectedGMIO.ifPresent(b -> b.setUnknown((UnknownEnum) a.getItem()));
        });
        minFilterBox.addItemListener(a -> {
            if (a.getStateChange() != ItemEvent.SELECTED)
                return;
            
            selectedGMIO.ifPresent(b -> b.setMinFilter((TextureFiltering) a.getItem()));
        });
        magFilterBox.addItemListener(a -> {
            if (a.getStateChange() != ItemEvent.SELECTED)
                return;
            
            selectedGMIO.ifPresent(b -> b.setMagFilter((TextureFiltering) a.getItem()));
        });
        
        uvWidthSpinner.addChangeListener(a -> selectedGMIO.ifPresent(b -> b.setUVWidthAbsolute((int) uvWidthSpinner.getValue())));
        uvHeightSpinner.addChangeListener(a -> selectedGMIO.ifPresent(b -> b.setUVHeightAbsolute((int) uvHeightSpinner.getValue())));
        
        //@formatter:off
        panel.setBorder(new MatteBorder(0, 1, 0, 0, new Color(0, 0, 0)));
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(exportButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(importButton))
                        .addComponent(image, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                    .addGap(54)
                    .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(221)
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
        

        fileNameField.setColumns(10);
        
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
                            .addComponent(formatBox, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
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
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblWrapS)
                    .addContainerGap(124, Short.MAX_VALUE))
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblWrapT)
                    .addContainerGap(124, Short.MAX_VALUE))
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblUnknown)
                    .addContainerGap(115, Short.MAX_VALUE))
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblMinFilter)
                    .addContainerGap(116, Short.MAX_VALUE))
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblMagFilter)
                    .addContainerGap(112, Short.MAX_VALUE))
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(wrapSBox, 0, 149, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(magFilterBox, 0, 149, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(minFilterBox, 0, 149, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(unknownBox, 0, 149, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(wrapTBox, 0, 149, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblFileName)
                    .addContainerGap(113, Short.MAX_VALUE))
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(fileNameField, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
                    .addContainerGap(70, Short.MAX_VALUE)
                    .addComponent(btnUpdateName)
                    .addContainerGap())
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblFormat)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(formatBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(resolution))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUvWidth)
                        .addComponent(lblUvHeight))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(uvWidthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uvHeightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblWrapS)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(wrapSBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblWrapT)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(wrapTBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblUnknown)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(unknownBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblMinFilter)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(minFilterBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblMagFilter)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(magFilterBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(8)
                    .addComponent(lblFileName)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(fileNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnUpdateName)
                    .addContainerGap(46, Short.MAX_VALUE))
        );
        
        panel.setLayout(gl_panel);
        setLayout(groupLayout);
        //@formatter:on
    }
    
    @Override
    public void setSelectedFile(Object file) {
        this.selectedGMIO = file instanceof GMIOPayload ? Optional.ofNullable((GMIOPayload) file) : Optional.empty();
        
        fileNameField.setText(selectedGMIO.map(GMIOPayload::getName).orElse(null));
        image.setImage(selectedGMIO.map(GMIOPayload::getImage).orElse(null));
        formatBox.setSelectedItem(selectedGMIO.map(GMIOPayload::getFormat).orElse(null));
        wrapSBox.setSelectedItem(selectedGMIO.map(GMIOPayload::getWrapS).orElse(null));
        wrapTBox.setSelectedItem(selectedGMIO.map(GMIOPayload::getWrapT).orElse(null));
        unknownBox.setSelectedItem(selectedGMIO.map(GMIOPayload::getUnknown).orElse(null));
        minFilterBox.setSelectedItem(selectedGMIO.map(GMIOPayload::getMinFilter).orElse(null));
        magFilterBox.setSelectedItem(selectedGMIO.map(GMIOPayload::getMagFilter).orElse(null));
        selectedGMIO.ifPresent(a -> {
            uvHeightSpinner.setModel(new SpinnerNumberModel(a.getUVHeightAbsolute(), 0, null, 1));
            uvWidthSpinner.setModel(new SpinnerNumberModel(a.getUVWidthAbsolute(), 0, null, 1));
        });
        resolution.setText(selectedGMIO.map(a -> a.getWidth() + "x" + a.getHeight()).orElse(null));
    }
    
    public Optional<GMIOPayload> getSelectedFile() {
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
            BufferedImage lImage = getSelectedFile().map(GMIOPayload::getImage).orElseGet(() -> null);
            
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
            Optional<GMIOPayload> selected = getSelectedFile();
            
            if (!selected.isPresent())
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showOpenDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try {
                BufferedImage localImage = ImageIO.read(fileDialogue.getSelectedFile());
                if (selected.get().setImage(localImage)) {
                    setSelectedFile(getSelectedFile());
                    uvHeightSpinner.setValue(selected.get().getUVHeightAbsolute());
                    uvWidthSpinner.setValue(selected.get().getUVWidthAbsolute());
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
