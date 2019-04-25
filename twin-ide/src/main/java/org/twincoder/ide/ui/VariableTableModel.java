/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.ui;

import org.twincoder.ide.program.Program;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.program.ResourceDisposable;

/**
 *
 * @author Gabriel
 */
public class VariableTableModel extends AbstractTableModel implements Observer, ResourceDisposable {

    private Program program = null;
    private ProgramContext context = null;
    private HashMap<String,String> variables;
    private String[] keys;
    /**
     *
     * @param program
     */
    public VariableTableModel(Program program) {
        this.program = program;
        if (program != null && program.getContext() != null) {
            context = program.getContext();
            variables = context.getVariables();
            keys = variables.keySet().toArray(new String[]{});
            context.addObserver(this);
        } else {
            variables = null;
            keys = null;
        }
    }
    
    @Override
    public int getRowCount() {
        if ((variables != null) && (keys != null)){
            return keys.length;
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Variable";
            case 1: return "Value";
        }
        return null;
    }
    
    @Override
    public Object getValueAt(int row, int column) {

        if ((variables != null) && (keys != null) && (row >= 0) && (row < keys.length)) {            
            switch (column) {
                case 0: return keys[row];
                case 1: return variables.get(keys[row]);
            }                
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        // Update keys
        if (variables != null) {
            keys = variables.keySet().toArray(new String[]{});
            fireTableDataChanged();
        }    
    }

    @Override
    public void disposeResources() {
        if (context != null) {
            context.deleteObserver(this);
        }
    }

    
}
