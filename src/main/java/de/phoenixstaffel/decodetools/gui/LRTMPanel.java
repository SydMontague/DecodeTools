package de.phoenixstaffel.decodetools.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.phoenixstaffel.decodetools.gui.util.JHexSpinner;
import de.phoenixstaffel.decodetools.res.payload.LRTMPayload;
import javax.swing.DefaultComboBoxModel;
import de.phoenixstaffel.decodetools.res.payload.LRTMPayload.LRTMShadingType;
import de.phoenixstaffel.decodetools.res.payload.LRTMPayload.LRTMUnkownType;

public class LRTMPanel extends PayloadPanel {
    
    private LRTMPayload selectedLRTM;
    
    private final JComboBox<LRTMShadingType> shadingSelector = new JComboBox<>();
    private final JComboBox<LRTMUnkownType> unk1Selector = new JComboBox<>();
    private final JHexSpinner filterSpinner = new JHexSpinner();
    private final JHexSpinner ambientSpinner = new JHexSpinner();
    private final JHexSpinner specularSpinner = new JHexSpinner();
    private final JHexSpinner emitSpinner = new JHexSpinner();
    private final JHexSpinner unk2Spinner = new JHexSpinner();
    private final JHexSpinner unk3Spinner = new JHexSpinner();
    private final JLabel shadingLabel = new JLabel("Shading:");
    private final JLabel unk1Label = new JLabel("Unk1:");
    private final JLabel filterLabel = new JLabel("Filter:");
    private final JLabel ambientLabel = new JLabel("Ambient:");
    private final JLabel specularLabel = new JLabel("Specular:");
    private final JLabel emitLabel = new JLabel("Emit:");
    private final JLabel unk2Label = new JLabel("Unk2:");
    private final JLabel unk3Label = new JLabel("Unk3:");
    
    public LRTMPanel(Object selected) {
        setSelectedFile(selected);
        
        shadingLabel.setLabelFor(shadingSelector);
        unk1Label.setLabelFor(unk1Selector);
        filterLabel.setLabelFor(filterSpinner);
        unk3Label.setLabelFor(unk3Spinner);
        unk2Label.setLabelFor(unk2Spinner);
        emitLabel.setLabelFor(emitSpinner);
        specularLabel.setLabelFor(specularSpinner);
        ambientLabel.setLabelFor(ambientSpinner);
        
        filterSpinner.addChangeListener(a -> selectedLRTM.setColorFilter(((Long) filterSpinner.getValue()).intValue()));
        ambientSpinner.addChangeListener(a -> selectedLRTM.setColor1(((Long) ambientSpinner.getValue()).intValue()));
        specularSpinner.addChangeListener(a -> selectedLRTM.setColor2(((Long) specularSpinner.getValue()).intValue()));
        emitSpinner.addChangeListener(a -> selectedLRTM.setColor3(((Long) emitSpinner.getValue()).intValue()));
        unk2Spinner.addChangeListener(a -> selectedLRTM.setColor4(((Long) unk2Spinner.getValue()).intValue()));
        unk3Spinner.addChangeListener(a -> selectedLRTM.setColor5(((Long) unk3Spinner.getValue()).intValue()));
        
        shadingSelector.addItemListener(a -> selectedLRTM.setShadingType((LRTMShadingType) a.getItem()));
        unk1Selector.addItemListener(a -> selectedLRTM.setUnknownType((LRTMUnkownType) a.getItem()));
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(ambientLabel)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(ambientSpinner, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(specularLabel))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(unk2Label)
                                    .addGap(18)
                                    .addComponent(unk2Spinner, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                    .addGap(10)
                                    .addComponent(unk3Label)))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(unk3Spinner, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(specularSpinner, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(emitLabel)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(emitSpinner, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(shadingLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(shadingSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(unk1Label)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(unk1Selector, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(filterLabel)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(filterSpinner, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(26, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(shadingLabel)
                        .addComponent(shadingSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(unk1Label)
                        .addComponent(unk1Selector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterLabel)
                        .addComponent(filterSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(ambientLabel)
                        .addComponent(ambientSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(specularLabel)
                        .addComponent(specularSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(emitLabel)
                        .addComponent(emitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(unk2Label)
                        .addComponent(unk3Label)
                        .addComponent(unk2Spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(unk3Spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(212, Short.MAX_VALUE))
        );
        shadingSelector.setModel(new DefaultComboBoxModel<>(LRTMShadingType.values()));
        unk1Selector.setModel(new DefaultComboBoxModel<>(LRTMUnkownType.values()));
        setLayout(groupLayout);
    }
    
    @Override
    public void setSelectedFile(Object file) {
        this.selectedLRTM = null;
        
        if (file instanceof LRTMPayload) {
            this.selectedLRTM = (LRTMPayload) file;
            
            shadingSelector.setSelectedItem(selectedLRTM.getShadingType());
            unk1Selector.setSelectedItem(selectedLRTM.getUnknownType());
            filterSpinner.setValue(Integer.toUnsignedLong(selectedLRTM.getColorFilter()));
            
            ambientSpinner.setValue(Integer.toUnsignedLong(selectedLRTM.getColor1()));
            specularSpinner.setValue(Integer.toUnsignedLong(selectedLRTM.getColor2()));
            emitSpinner.setValue(Integer.toUnsignedLong(selectedLRTM.getColor3()));
            unk2Spinner.setValue(Integer.toUnsignedLong(selectedLRTM.getColor4()));
            unk3Spinner.setValue(Integer.toUnsignedLong(selectedLRTM.getColor5()));
        }
    }
}
