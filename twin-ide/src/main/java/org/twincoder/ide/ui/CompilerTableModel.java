/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.ui;

import org.twincoder.ide.compiler.Compiler;
import org.twincoder.ide.compiler.CompilerMessage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Gabriel
 */
public class CompilerTableModel extends AbstractTableModel implements Observer {

    private Compiler compiler;
    
    public CompilerTableModel(Compiler compiler) {
        this.compiler = compiler;
        if (compiler != null) {
            compiler.addObserver(this);
        }
    }
    
    @Override
    public int getRowCount() {
        if (getCompiler() != null && getCompiler().getMessages() != null) {
            return getCompiler().getMessages().size();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }
    
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Type";
            case 1: return "Line";
            case 2: return "Module";
            case 3: return "Message";
        }
        return null;
    }

    public CompilerMessage getCompilerMessage(int row) {
        return getCompiler().getMessages().get(row);
    }
    
    @Override
    public Object getValueAt(int row, int column) {

        if (getCompiler() != null && getCompiler().getMessages() != null) {
            CompilerMessage message = getCompilerMessage(row);
            if (message != null) {
                switch (column) {
                    case 0: return message.getType();
                    case 1: return message.getLine() + 1;
                    case 2: return message.getCommand();
                    case 3: return message.getDescription();
                }                
            }
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        fireTableDataChanged();
    }

    /**
     * @return the compiler
     */
    public Compiler getCompiler() {
        return compiler;
    }
    
}
