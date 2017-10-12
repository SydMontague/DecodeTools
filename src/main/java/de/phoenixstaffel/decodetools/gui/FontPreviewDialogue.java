package de.phoenixstaffel.decodetools.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;

import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.util.List;

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
            text.setTNFO(tnfo);
            text.setGMIOs(gmios);
            text.update();
        }));
        
        setFormat();
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
                        .addComponent(text, GroupLayout.PREFERRED_SIZE, 558, GroupLayout.PREFERRED_SIZE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(inputText, GroupLayout.PREFERRED_SIZE, 415, GroupLayout.PREFERRED_SIZE)
                            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addGap(10)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(lblWidespace)
                                        .addComponent(lblFontSize))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(fontSizeSpinner, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                                        .addComponent(widespaceSpinner, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(updateButton)))))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(18)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblFontSize)
                                .addComponent(fontSizeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(7)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblWidespace)
                                .addComponent(widespaceSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(updateButton))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(inputText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)))
                    .addGap(12)
                    .addComponent(text, GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                    .addContainerGap())
        );
        getContentPane().setLayout(groupLayout);
        //@formatter:on
    }
}
