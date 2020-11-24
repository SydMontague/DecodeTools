package de.phoenixstaffel.decodetools.gui.util;

import java.text.ParseException;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatterFactory;

public class JHexSpinner extends JSpinner {
    private static final long serialVersionUID = 1450837930694974722L;

    public JHexSpinner() {
        super(new SpinnerNumberModel(Long.valueOf(0), Long.valueOf(0), Long.valueOf(4294967295L), Long.valueOf(1)));
        ((JSpinner.NumberEditor) getEditor()).getTextField().setFormatterFactory(new HexFormatterFactory());
    }
    
    private static class HexFormatterFactory extends DefaultFormatterFactory {
        private static final long serialVersionUID = 8880598100311748936L;

        @Override
        public AbstractFormatter getDefaultFormatter() {
            return new HexFormatter();
        }
    }
    
    private static class HexFormatter extends AbstractFormatter {
        private static final long serialVersionUID = -5767647981763616282L;

        @Override
        public Object stringToValue(String text) throws ParseException {
            return Long.parseLong(text, 16);
        }
        
        @Override
        public String valueToString(Object value) throws ParseException {
            return value instanceof Number ? String.format("%08X", ((Number) value).longValue()) : "00000000";
        }
        
    }
}
