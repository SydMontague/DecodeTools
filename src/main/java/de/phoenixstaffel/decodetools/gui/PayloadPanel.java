package de.phoenixstaffel.decodetools.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.KCAPPayload.Payload;

public abstract class PayloadPanel extends JPanel {
    private static final long serialVersionUID = -889159315719439977L;
    
    public static final PayloadPanel NULL_PANEL = new PayloadPanel() {
        private static final long serialVersionUID = -1637343165773635969L;

        @Override
        public void setSelectedFile(Object file) {
            //no implementation
        }
    };

    public abstract void setSelectedFile(Object file);

    public static Map<Payload, PayloadPanel> generatePayloadPanels() {
        Map<KCAPPayload.Payload, PayloadPanel> tempPanels = new HashMap<>();
        
        tempPanels.put(Payload.GMIO, new GMIOPanel(null));
        
        return tempPanels;
    }
}
