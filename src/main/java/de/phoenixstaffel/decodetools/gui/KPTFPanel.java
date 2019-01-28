package de.phoenixstaffel.decodetools.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.export.fontxml.XMLFont;
import de.phoenixstaffel.decodetools.gui.util.FunctionAction;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload.TNFOEntry;

public class KPTFPanel extends PayloadPanel {
    private static final long serialVersionUID = 4410795681047271875L;
    
    private transient AbstractKCAP kptf;
    private transient TNFOPayload tnfo;
    private transient TNFOEntry entry;
    private transient List<GMIOPayload> gmios;
    
    private final JScrollPane scrollPane = new JScrollPane();
    private final JTextField searchField = new JTextField();
    private final JLabel lblSearch = new JLabel("Search");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnRemove = new JButton("Remove");
    private final JButton btnImportXml = new JButton("Import XML");
    private final JList<Integer> list = new JList<>();
    private DefaultListModel<Integer> model = new DefaultListModel<>();
    
    private final JPanel tnfoEntryPanel = new JPanel();
    private final JPanel tnfoHeaderPanel = new JPanel();
    
    private final JLabel unk1Label = new JLabel("Unk1");
    private final JLabel spaceWidthLabel = new JLabel("Space Width");
    private final JLabel yOffsetLabel = new JLabel("Y Offset");
    private final JLabel unk2Label = new JLabel("Unk2");
    private final JLabel refSizeLabel = new JLabel("Ref. Size");
    private final JLabel unk3Label = new JLabel("Unk3");
    private final JSpinner unk1Field = new JSpinner();
    private final JSpinner spaceWidthField = new JSpinner();
    private final JSpinner yOffsetField = new JSpinner();
    private final JSpinner unk2Field = new JSpinner();
    private final JSpinner refSizeField = new JSpinner();
    private final JSpinner unk3Field = new JSpinner();
    
    private final JLabel xTransLabel = new JLabel("X Translation");
    private final JLabel yTransLabel = new JLabel("Y Translation");
    private final JLabel widthLabel = new JLabel("Width");
    private final JLabel heightLabel = new JLabel("Height");
    private final JLabel textWidthLabel = new JLabel("Text Width");
    private final JSpinner xTransField = new JSpinner();
    private final JSpinner yTransField = new JSpinner();
    private final JSpinner widthField = new JSpinner();
    private final JSpinner heightField = new JSpinner();
    private final JSpinner textWidthField = new JSpinner();
    private final JImage image = new JImage();
    private final JButton btnChange = new JButton("Resize");
    private JResizeDialogue resize;
    private final JButton btnPreview = new JButton("Preview");
    
    private FontPreviewDialogue previewWindow = new FontPreviewDialogue();
    
    public KPTFPanel(Object selected) {
        unk3Label.setLabelFor(unk3Field);
        refSizeLabel.setLabelFor(refSizeField);
        unk2Label.setLabelFor(unk2Field);
        yOffsetLabel.setLabelFor(yOffsetField);
        spaceWidthLabel.setLabelFor(spaceWidthField);
        unk1Label.setLabelFor(unk1Field);
        textWidthLabel.setLabelFor(textWidthField);
        heightLabel.setLabelFor(heightField);
        widthLabel.setLabelFor(widthField);
        yTransLabel.setLabelFor(yTransField);
        xTransLabel.setLabelFor(xTransField);
        lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblSearch.setLabelFor(searchField);
        searchField.setEditable(false);
        searchField.setColumns(10);
        setSelectedFile(selected);
        tnfoEntryPanel.setVisible(false);
        
        resize = new JResizeDialogue(new ArrayList<>());
        resize.addPropertyChangeListener("selected", a -> entry.setGmioId(((Integer) a.getNewValue()).shortValue()));
        
        btnPreview.setAction(new FunctionAction("Preview", a -> {
            previewWindow.setVisible(true);
        }));
        
        btnRemove.setAction(new FunctionAction("Remove", a -> {
            if (list.getSelectedValue() == -1)
                return;
            
            list.getSelectedValuesList().forEach(character ->  {
                tnfo.removeAssignment(character);
                model.removeElement(character);
            });
        }));
        
        btnImportXml.setAction(new FunctionAction("Import XML", a -> {
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Please select the Font XML to import.");
            inputFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            inputFileDialogue.showOpenDialog(null);
            
            File file = inputFileDialogue.getSelectedFile();
            
            if(file == null)
                return;

            try {
                XMLFont font = new XMLFont(file);

                font.getChars().forEach(b -> {
                    tnfo.removeAssignment(b.getChar());
                    tnfo.addAssignment(b.getChar(), b.toTNFOEntry());
                });
            }
            catch (ParserConfigurationException | SAXException | IOException e) {
                Main.LOGGER.severe("Error while loading font XML!" + e.getMessage());
            }
            regenerateListModel();
        }));
        
        btnAdd.setAction(new FunctionAction("Add", a -> {
            String input = JOptionPane.showInputDialog(null, "Insert character to add: ", "Add character to KPTF", JOptionPane.PLAIN_MESSAGE);
            
            if(input == null)
                return;
            
            if (input.isEmpty() || input.length() > 1) {
                JOptionPane.showMessageDialog(null, "You have to enter exactly one character.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int i = input.charAt(0);
            if (i > 0xFFFF) {
                JOptionPane.showMessageDialog(null, "This character is not within UTF-16.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            tnfo.addAssignment(i, new TNFOEntry());
            regenerateListModel();
        }));
        
        btnChange.setAction(new FunctionAction("Resize", a -> resize.setVisible(true)));
        
        resize.imageSelector.addPropertyChangeListener("selection", a -> {
            Rectangle selection = (Rectangle) a.getNewValue();
            BufferedImage i = gmios.get(entry.getGmioId()).getImage();
            double x1 = selection.getMinX() / i.getWidth();
            double y1 = selection.getMinY() / i.getHeight();
            double x2 = selection.getMaxX() / i.getWidth();
            double y2 = selection.getMaxY() / i.getHeight();
            
            entry.setX1(x1);
            entry.setX2(x2);
            entry.setY1(1 - y1);
            entry.setY2(1 - y2);
        });
        list.setModel(model);
        
        list.addListSelectionListener(a -> {
            if (list.getSelectedIndex() == -1)
                return;
            
            if (list.getSelectedValue() == -1)
                entry = tnfo.getDefaultEntry();
            else
                entry = tnfo.getAssignments().get(list.getSelectedValue());
            
            tnfoEntryPanel.setVisible(true);
            xTransField.setValue(entry.getXTranslation());
            yTransField.setValue(entry.getYTranslation());
            widthField.setValue(Byte.toUnsignedInt(entry.getWidth()));
            heightField.setValue(Byte.toUnsignedInt(entry.getHeight()));
            textWidthField.setValue(Byte.toUnsignedInt(entry.getTextWidth()));
            
            BufferedImage i = gmios.get(entry.getGmioId()).getImage();
            int x1 = (int) Math.round(entry.getX1() * i.getWidth());
            int x2 = (int) Math.round(entry.getX2() * i.getWidth());
            int y1 = (int) Math.round(entry.getY1() * i.getHeight());
            int y2 = (int) Math.round(entry.getY2() * i.getHeight());
            
            resize.setSelectedImage(entry.getGmioId());
            resize.imageSelector.setSelection(new Rectangle(x1, i.getHeight() - y1, x2 - x1, y1 - y2));
            
            if (x2 - x1 != 0 && y1 - y2 != 0)
                image.setImage(i.getSubimage(x1, i.getHeight() - y1, x2 - x1, y1 - y2));
            else
                image.setImage(null);
        });
        
        list.setCellRenderer(new GenericListCellRenderer<Integer>(a -> {
            if (a == -1)
                return "DEFAULT";
            
            return ((char) a.intValue()) + String.format(" (0x%04X) ", a) + Character.getName(a);
        }));
        
        unk1Field.addChangeListener(a -> tnfo.setUnknown1((short) unk1Field.getValue()));
        unk2Field.addChangeListener(a -> tnfo.setUnknown2((short) unk2Field.getValue()));
        unk3Field.addChangeListener(a -> tnfo.setUnknown3((short) unk3Field.getValue()));
        spaceWidthField.addChangeListener(a -> tnfo.setSpaceWidth((short) spaceWidthField.getValue()));
        refSizeField.addChangeListener(a -> tnfo.setReferenceSize((short) refSizeField.getValue()));
        yOffsetField.addChangeListener(a -> tnfo.setYOffset((short) yOffsetField.getValue()));
        
        xTransField.addChangeListener(a -> entry.setXTranslation((byte) xTransField.getValue()));
        yTransField.addChangeListener(a -> entry.setYTranslation((byte) yTransField.getValue()));
        heightField.addChangeListener(a -> entry.setHeight(((Integer) heightField.getValue()).byteValue()));
        widthField.addChangeListener(a -> entry.setWidth(((Integer) widthField.getValue()).byteValue()));
        textWidthField.addChangeListener(a -> entry.setTextWidth(((Integer) textWidthField.getValue()).byteValue()));
    }
    
    private void regenerateListModel() {
        model.clear();
        model.addElement(-1); // Default element
        tnfo.getAssignments().forEach((a, b) -> {
            if (b != null)
                model.addElement(a);
        });
    }
    
    @Override
    public void setSelectedFile(Object file) {
        if (file == null)
            return;
        
        if (!(file instanceof AbstractKCAP)) {
            Main.LOGGER.warning("Tried to select non-KCAP File in KPTFPanel.");
            return;
        }
        
        if (((AbstractKCAP) file).getKCAPType() != AbstractKCAP.KCAPType.KPTF) {
            Main.LOGGER.warning("Tried to select non-KPTF KCAP File in KPTFPanel.");
            return;
        }
        
        kptf = (AbstractKCAP) file;
        tnfo = (TNFOPayload) kptf.get(0);
        entry = null;
        tnfoEntryPanel.setVisible(false);
        Object gmio = kptf.get(1);
        
        if (gmio instanceof GMIOPayload)
            gmios = Arrays.asList((GMIOPayload) gmio);
        else if (gmio instanceof AbstractKCAP && ((AbstractKCAP) gmio).getKCAPType() == AbstractKCAP.KCAPType.GMIP) {
            gmios = new ArrayList<>();
            ((AbstractKCAP) gmio).getElementsWithType(Payload.GMIO).forEach(a -> gmios.add((GMIOPayload) a));
        }
        
        List<Image> images = new ArrayList<>();
        gmios.stream().map(GMIOPayload::getImage).forEach(images::add);
        resize.setImages(images);
        
        previewWindow.setTNFO(tnfo);
        previewWindow.setGMIOs(gmios);
        regenerateListModel();
        
        setupLayout();
        
        unk1Field.setValue(tnfo.getUnknown1());
        spaceWidthField.setValue(tnfo.getSpaceWidth());
        yOffsetField.setValue(tnfo.getYOffset());
        unk2Field.setValue(tnfo.getUnknown2());
        refSizeField.setValue(tnfo.getReferenceSize());
        unk3Field.setValue(tnfo.getUnknown3());
    }
    
    private void setupLayout() {
      //@formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                            .addGroup(groupLayout.createSequentialGroup()
                                .addComponent(lblSearch, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(searchField, 0, 0, Short.MAX_VALUE))
                            .addGroup(groupLayout.createSequentialGroup()
                                .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnRemove, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)))
                        .addComponent(btnImportXml)
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(tnfoEntryPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tnfoHeaderPanel, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblSearch))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnImportXml)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(btnAdd)
                                .addComponent(btnRemove)))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(tnfoHeaderPanel, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(tnfoEntryPanel, GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)))
                    .addGap(5))
        );
        GroupLayout gl_tnfoHeaderPanel = new GroupLayout(tnfoHeaderPanel);
        gl_tnfoHeaderPanel.setHorizontalGroup(
            gl_tnfoHeaderPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_tnfoHeaderPanel.createSequentialGroup()
                    .addGroup(gl_tnfoHeaderPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_tnfoHeaderPanel.createSequentialGroup()
                            .addComponent(unk1Field, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(spaceWidthField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_tnfoHeaderPanel.createSequentialGroup()
                            .addComponent(unk1Label, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(spaceWidthLabel)))
                    .addGap(12)
                    .addGroup(gl_tnfoHeaderPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_tnfoHeaderPanel.createSequentialGroup()
                            .addComponent(yOffsetLabel, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(unk2Label, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addGap(10)
                            .addComponent(refSizeLabel, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(unk3Label, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_tnfoHeaderPanel.createSequentialGroup()
                            .addComponent(yOffsetField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(unk2Field, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(refSizeField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(unk3Field, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(btnPreview)))
                    .addGap(10))
        );
        gl_tnfoHeaderPanel.setVerticalGroup(
            gl_tnfoHeaderPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_tnfoHeaderPanel.createSequentialGroup()
                    .addGroup(gl_tnfoHeaderPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(unk1Label)
                        .addComponent(spaceWidthLabel)
                        .addComponent(yOffsetLabel)
                        .addGroup(gl_tnfoHeaderPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(refSizeLabel)
                            .addComponent(unk2Label)
                            .addComponent(unk3Label)))
                    .addGap(12)
                    .addGroup(gl_tnfoHeaderPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(unk1Field, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(spaceWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(yOffsetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(unk2Field, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(refSizeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(unk3Field, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addGroup(Alignment.TRAILING, gl_tnfoHeaderPanel.createSequentialGroup()
                    .addContainerGap(21, Short.MAX_VALUE)
                    .addComponent(btnPreview)
                    .addContainerGap())
        );
        unk1Label.setHorizontalAlignment(SwingConstants.CENTER);
        spaceWidthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        yOffsetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unk2Label.setHorizontalAlignment(SwingConstants.CENTER);
        refSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unk3Label.setHorizontalAlignment(SwingConstants.CENTER);
        unk1Field.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
        spaceWidthField.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
        yOffsetField.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
        unk2Field.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
        refSizeField.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
        unk3Field.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
        tnfoHeaderPanel.setLayout(gl_tnfoHeaderPanel);
        tnfoEntryPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        GroupLayout gl_tnfoEntryPanel = new GroupLayout(tnfoEntryPanel);
        gl_tnfoEntryPanel.setHorizontalGroup(
            gl_tnfoEntryPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_tnfoEntryPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_tnfoEntryPanel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(btnChange, 0, 0, Short.MAX_VALUE)
                        .addComponent(xTransLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(heightLabel)
                        .addComponent(widthLabel)
                        .addComponent(yTransLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(yTransField)
                        .addComponent(widthField)
                        .addComponent(heightField)
                        .addComponent(textWidthLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(textWidthField)
                        .addComponent(xTransField))
                    .addGap(22)
                    .addComponent(image, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                    .addContainerGap())
        );

        gl_tnfoEntryPanel.setVerticalGroup(
            gl_tnfoEntryPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_tnfoEntryPanel.createSequentialGroup()
                    .addGroup(gl_tnfoEntryPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_tnfoEntryPanel.createSequentialGroup()
                            .addComponent(xTransLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(xTransField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(yTransLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(yTransField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(widthLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(widthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(heightLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(heightField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textWidthLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnChange))
                        .addGroup(gl_tnfoEntryPanel.createSequentialGroup()
                            .addGap(13)
                            .addComponent(image, GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        widthField.setModel(new SpinnerNumberModel((short) 0, 0, 255, (short) 1));
        heightField.setModel(new SpinnerNumberModel((short) 0, 0, 255, (short) 1));
        textWidthField.setModel(new SpinnerNumberModel((short) 0, 0, 255, (short) 1));
        yTransField.setModel(new SpinnerNumberModel((byte) 0, null, null, (byte) 1));
        xTransField.setModel(new SpinnerNumberModel((byte) 0, null, null, (byte) 1));
        tnfoEntryPanel.setLayout(gl_tnfoEntryPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(list);
        setLayout(groupLayout);
        //@formatter:on
    }
}

class GenericListCellRenderer<T> extends DefaultListCellRenderer {
    private static final long serialVersionUID = 8858323241227888747L;
    
    private Function<T, String> function;
    
    public GenericListCellRenderer(Function<T, String> function) {
        super();
        this.function = function;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        this.setText(function.apply((T) value));
        
        return this;
    }
}
