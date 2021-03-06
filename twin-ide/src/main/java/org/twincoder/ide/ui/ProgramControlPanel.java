/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.ui;

import org.twincoder.ide.program.Program;
import org.twincoder.ide.program.ResourceDisposable;

/**
 *
 * @author gbornia
 */
public class ProgramControlPanel extends javax.swing.JPanel implements ResourceDisposable {

    private final Program program;
    private VariableTableModel variableModel;
    /**
     * Creates new form VariableDisplay
     * @param program
     */
    public ProgramControlPanel(Program program) {
        initComponents();
        this.program = program;  
        this.variableModel = new VariableTableModel(program);
        jTableControl.setModel(variableModel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPaneControl = new javax.swing.JScrollPane();
        jTableControl = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(250, 404));
        setLayout(new java.awt.BorderLayout());

        jTableControl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPaneControl.setViewportView(jTableControl);

        add(jScrollPaneControl, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPaneControl;
    private javax.swing.JTable jTableControl;
    // End of variables declaration//GEN-END:variables

    @Override
    public void disposeResources() {
        if (variableModel != null) {
            variableModel.disposeResources();
        }
    }
}
