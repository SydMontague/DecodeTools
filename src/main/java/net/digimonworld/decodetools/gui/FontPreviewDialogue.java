package net.digimonworld.decodetools.gui;

import javax.swing.GroupLayout;
import javax.swing.InputVerifier;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.digimonworld.decodetools.gui.util.FunctionAction;
import net.digimonworld.decodetools.res.payload.GMIOPayload;
import net.digimonworld.decodetools.res.payload.TNFOPayload;

import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.SpinnerNumberModel;
import javax.swing.JMenuBar;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

public class FontPreviewDialogue extends JFrame {
    private static final long serialVersionUID = 8687652956429883024L;

    private static final String HEX_REGEX = "^#?[0-9a-fA-F]{6}$";
    
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
    private final JTextField fontColorField = new JTextField();
    private final JTextField bgColorField = new JTextField();
    private final JMenuBar menu = new JMenuBar();
    
    private final JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Show Textbox");
    private final JMenuItem mntmNewMenuItem = new JMenuItem("Textbox");
    private final JMenu mnSettings = new JMenu("Settings");
    private final JMenu mnPresets = new JMenu("Presets");
    
    public FontPreviewDialogue() {
        setResizable(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setBounds(0, 0, 600, 400);
        setTitle("Font Preview");
        
        inputText.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inputText.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        inputText.setTabSize(4);
        inputText.setWrapStyleWord(true);
        inputText.setLineWrap(true);
        inputText.setText("Praised be our one true lord Gabumon.\r\nMay his pelt protect those in need of protection \r\nand his horn hurt those, who mean others harm.");
        
        setJMenuBar(menu);
        
        menu.add(mnSettings);
        mnSettings.add(chckbxmntmNewCheckItem);
        
        menu.add(mnPresets);
        mnPresets.add(mntmNewMenuItem);
        
        mntmNewMenuItem.setAction(new FunctionAction("Textbox", a -> {
            fontSizeSpinner.setValue(10);
            widespaceSpinner.setValue(0);
            lineHeightSpinner.setValue(24);
            fontSizeSpinner.setValue(10);
            startXSpinner.setValue(72);
            startYSpinner.setValue(323);
            bgColorField.setText("#000000");
            fontColorField.setText("#FFFFFF");
        }));
        
        fontColorField.setText("#FFFFFF");
        bgColorField.setText("#000000");
        
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
            text.setDisplayTextbox(chckbxmntmNewCheckItem.isSelected());
            
            Color bg = bgColorField.getText().matches(HEX_REGEX) ? Color.decode(bgColorField.getText()) : Color.BLACK;
            Color font = fontColorField.getText().matches(HEX_REGEX) ? Color.decode(fontColorField.getText()) : Color.WHITE;
            
            text.setForeground(font);
            text.setBackground(bg);
            text.update();
            
            pack();
        }));
        
        InputVerifier hexVerifier = new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return ((JTextField) input).getText().matches(HEX_REGEX);
            }
        };
        
        fontColorField.setInputVerifier(hexVerifier);
        bgColorField.setInputVerifier(hexVerifier);
        
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
                                        .addComponent(bgColorField, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(bgColorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblBgColor))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(updateButton))
                        .addComponent(text, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        getContentPane().setLayout(groupLayout);
        //@formatter:on
        
        scaleSpinner.setModel(new SpinnerNumberModel(1D, 0D, null, 1D));
        fontSizeSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
        widespaceSpinner.setModel(new SpinnerNumberModel(0, null, null, 1));
        lineHeightSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
        startXSpinner.setModel(new SpinnerNumberModel(0, null, null, 1));
        startYSpinner.setModel(new SpinnerNumberModel(0, null, null, 1));
    }
}
