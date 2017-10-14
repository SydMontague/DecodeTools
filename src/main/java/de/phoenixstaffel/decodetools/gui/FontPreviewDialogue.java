package de.phoenixstaffel.decodetools.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;

import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.util.List;
import javax.swing.SpinnerNumberModel;

public class FontPreviewDialogue extends JFrame {
    private TNFOPayload tnfo;
    private List<GMIOPayload> gmios;
    
    private final JTextArea inputText = new JTextArea();
    private final JButton updateButton = new JButton("Update");
    private final JSpinner fontSizeSpinner = new JSpinner();
    private final JSpinner widespaceSpinner = new JSpinner();
    private final JLabel lblFontSize = new JLabel("Font Size:");
    private final JLabel lblWidespace = new JLabel("Widespace:");
    private final JKPTFText text = new JKPTFText();
    private final JLabel lblStartY = new JLabel("Start Y");
    private final JLabel lblFontColor = new JLabel("Font Color");
    private final JLabel lblBgColor = new JLabel("BG Color");
    private final JLabel lblStartX = new JLabel("Start X");
    private final JLabel lblScale = new JLabel("Res. Scale");
    private final JLabel lblLineHeight = new JLabel("Line Height");
    private final JSpinner lineHeightSpinner = new JSpinner();
    private final JSpinner scaleSpinner = new JSpinner();
    private final JSpinner startXSpinner = new JSpinner();
    private final JSpinner startYSpinner = new JSpinner();
    private final JTextField fontColorField = new JTextField(); //TODO change into something with color
    private final JTextField bgcolorField = new JTextField(); //TODO change into something with color
    
    public FontPreviewDialogue() {
        setResizable(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setBounds(0, 0, 600, 400);
        
        inputText.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inputText.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        inputText.setTabSize(4);
        inputText.setWrapStyleWord(true);
        inputText.setLineWrap(true);
        inputText.setText("Praised be our one true lord Gabumon.\r\nMay his pelt protect those in need of protection \r\nand his horn hurt those, who mean others harm.");
        
        updateButton.setAction(new FunctionAction("Update", a -> {
            text.setText(inputText.getText());
            text.setFontSize((int) fontSizeSpinner.getValue());
            text.setWidespace((int) widespaceSpinner.getValue());
            text.setLineHeight((int) lineHeightSpinner.getValue());
            text.setResolutionScale((double) scaleSpinner.getValue());
            text.setStartX((int) startXSpinner.getValue());
            text.setStartY((int) startYSpinner.getValue());
            text.setTNFO(tnfo);
            text.setGMIOs(gmios);
            text.update();
            
            pack();
        }));
        
        setFormat();

        lblStartY.setLabelFor(startYSpinner);
        lblStartX.setLabelFor(startXSpinner);
        lblScale.setLabelFor(scaleSpinner);
        lblLineHeight.setLabelFor(lineHeightSpinner);
        lblWidespace.setLabelFor(widespaceSpinner);
        lblFontSize.setLabelFor(fontSizeSpinner);
        
        pack();
    }
    
    public void setTNFO(TNFOPayload tnfo) {
        this.tnfo = tnfo;
    }
    
    public void setGMIOs(List<GMIOPayload> gmios) {
        this.gmios = gmios;
    }
    
    private final void setFormat() {
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(inputText, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(lblWidespace)
                                                .addComponent(lblFontSize)
                                                .addComponent(lblLineHeight, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
                                            .addComponent(lblStartX, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(lblScale, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblStartY, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblFontColor, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblBgColor, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(fontColorField, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(bgcolorField, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(fontSizeSpinner, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                            .addComponent(lineHeightSpinner, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                            .addComponent(scaleSpinner, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                            .addComponent(startXSpinner, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                            .addComponent(startYSpinner, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                            .addComponent(widespaceSpinner))))
                                .addComponent(updateButton))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(text, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(inputText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblFontSize)
                                .addComponent(fontSizeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblWidespace)
                                .addComponent(widespaceSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lineHeightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblLineHeight))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(scaleSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblScale))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(startXSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblStartX))
                            .addGap(6)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(startYSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblStartY))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(fontColorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblFontColor))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(bgcolorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblBgColor))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(updateButton))
                        .addComponent(text, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        getContentPane().setLayout(groupLayout);
        //@formatter:on
        
        scaleSpinner.setModel(new SpinnerNumberModel(new Double(1), 0D, null, new Double(1)));
    }
}
