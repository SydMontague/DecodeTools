package de.phoenixstaffel.decodetools.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import de.phoenixstaffel.decodetools.res.HeaderExtension.Extensions;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;

public abstract class PayloadPanel extends JPanel {
    private static final long serialVersionUID = -889159315719439977L;
    
    public static final PayloadPanel NULL_PANEL = new PayloadPanel() {
        private static final long serialVersionUID = -1637343165773635969L;
        
        @Override
        public void setSelectedFile(Object file) {
            // no implementation
        }
    };
    
    public abstract void setSelectedFile(Object file);
    
    public static Map<Enum<?>, PayloadPanel> generatePayloadPanels() {
        Map<Enum<?>, PayloadPanel> tempPanels = new HashMap<>();
        
        tempPanels.put(Payload.GMIO, new GMIOPanel(null));
        tempPanels.put(Extensions.KPTF, new KPTFPanel(null));
        tempPanels.put(Payload.HSEM, new HSEMPanel(null));
        
        return tempPanels;
    }
}
