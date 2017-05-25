package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public class FunctionAction extends AbstractAction {
    private static final long serialVersionUID = 6115002314325923497L;

    private Consumer<ActionEvent> function;
    
    public FunctionAction(Consumer<ActionEvent> function) {
        super();
        this.function = function;
    }
    
    public FunctionAction(String name, Consumer<ActionEvent> function) {
        super(name);
        this.function = function;
    }
    
    public FunctionAction(String name, Icon icon, Consumer<ActionEvent> function) {
        super(name, icon);
        this.function = function;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        function.accept(e);
    }
    
}
