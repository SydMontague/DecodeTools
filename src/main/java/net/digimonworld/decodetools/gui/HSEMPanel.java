package net.digimonworld.decodetools.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.res.payload.HSEMPayload;
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntry;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import java.awt.Font;

// TODO quick 'n' dirty, make proper
public class HSEMPanel extends PayloadPanel {

    private static final long serialVersionUID = -4369075808768544826L;

    private transient HSEMPayload selected;
    private final JList<HSEMEntry> list = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JLabel lblNewLabel = new JLabel("ID:");
    private final JLabel idLabel = new JLabel("<idLabel>");
    private final JLabel lblUnk = new JLabel("Unk2:");
    private final JLabel unk2label = new JLabel("<unk2Label>");
    private final JLabel lblUnk_2 = new JLabel("Unk3:");
    private final JLabel unk3label = new JLabel("<unk3Label>");
    private final JLabel lblUnk_2_1 = new JLabel("Unk4:");
    private final JLabel unk4label = new JLabel("<unk4Label>");
    private final JTextPane textPane = new JTextPane();
    private final JLabel unk2_2label = new JLabel("<unk2_2>");

    public HSEMPanel(Object obj) {
        setSelectedFile(obj);

        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblNewLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(idLabel))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblUnk)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(unk2label))
                                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                    .addPreferredGap(ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                                    .addComponent(unk2_2label)
                                    .addPreferredGap(ComponentPlacement.RELATED))))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblUnk_2, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(unk3label, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblUnk_2_1, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(unk4label, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))
                        .addComponent(textPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(68, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblNewLabel)
                                .addComponent(idLabel))
                            .addGap(4)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblUnk)
                                .addComponent(unk2label)
                                .addComponent(unk2_2label))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblUnk_2)
                                .addComponent(unk3label))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblUnk_2_1)
                                .addComponent(unk4label))
                            .addGap(28)
                            .addComponent(textPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        textPane.setFont(new Font("Inconsolata", Font.PLAIN, 12));
        setLayout(groupLayout);
        //@formatter:on
    }

    @Override
    public void setSelectedFile(Object file) {
        if (file == null)
            return;

        if (!(file instanceof HSEMPayload)) {
            Main.LOGGER.warning("Tried to select non-HSEM File in HSEMPanel.");
            return;
        }

        selected = (HSEMPayload) file;
        scrollPane.setViewportView(list);
        list.setListData(selected.getEntries().toArray(new HSEMEntry[0]));

        idLabel.setText(Integer.toString(selected.getId()));
        unk2label.setText(Integer.toString(selected.getUnknown2_1()));
        unk2_2label.setText(Integer.toString(selected.getUnknown2_2()));
        unk3label.setText(Integer.toString(selected.getUnknown3()));
        unk4label.setText(Integer.toString(selected.getUnknown4()));

        float[] arr = selected.getHeaderData();
        String s = String.format("%10.4f %10.4f\n%10.4f %10.4f\n%10.4f %10.4f\n%10.4f %10.4f\n%10.4f %10.4f\n", arr[0],
                                 arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9]);
        textPane.setText(s);
    }

    public HSEMPayload getSelectedFile() {
        return selected;
    }
}
