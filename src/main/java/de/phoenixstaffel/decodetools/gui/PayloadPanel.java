package de.phoenixstaffel.decodetools.gui;

import javax.swing.JPanel;

public abstract class PayloadPanel extends JPanel {
    private static final long serialVersionUID = -889159315719439977L;

    public abstract void setSelectedFile(Object file);
}
