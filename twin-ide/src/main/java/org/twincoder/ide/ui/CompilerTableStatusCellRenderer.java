/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.ui;

import org.twincoder.ide.compiler.CompilerMessage;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author gabri
 */
public class CompilerTableStatusCellRenderer extends DefaultTableCellRenderer {
   
    private static Icon ICON_ERROR = null;
    private static Icon ICON_WARNING = null;
    private static Icon ICON_INFO = null;

    public CompilerTableStatusCellRenderer() {
        super();
        
        if (ICON_ERROR == null)
            ICON_ERROR =  new javax.swing.ImageIcon(getClass().getResource("/images/error.png"));
        
        if (ICON_WARNING == null)
            ICON_WARNING = new javax.swing.ImageIcon(getClass().getResource("/images/warning.png"));
        
        if (ICON_INFO == null)
            ICON_INFO = new javax.swing.ImageIcon(getClass().getResource("/images/info.png"));
    }
    @Override
    public Component getTableCellRendererComponent(
    JTable aTable, Object aValue, boolean aIsSelected, 
    boolean aHasFocus, int aRow, int aColumn) { 
        
        
        super.getTableCellRendererComponent(
            aTable, aValue, aIsSelected, aHasFocus, aRow, aColumn
        );
        
        if (aValue != null && aValue instanceof Number) {
            Number status = (Number) aValue;
            switch (status.intValue()) {
                case CompilerMessage.ERROR:
                    setText("Error");
                    setIcon(ICON_ERROR);
                    break;
                case CompilerMessage.WARNING:
                    setText("Warning");
                    setIcon(ICON_WARNING);
                    break;
                default:
                    setText("Information");
                    setIcon(ICON_INFO);
            }
        }
                
        return this;
    }   
}
