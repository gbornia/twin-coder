/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import org.twincoder.ide.program.LogEntry;
import org.twincoder.ide.program.Program;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.program.ResourceDisposable;

/**
 *
 * @author gbornia
 */
public class ProgramListControl extends javax.swing.JPanel implements ResourceDisposable {

    private Program program;
    private final ProgramTableModel model;
    private LogEntryTableModel programLogModel;
    /**
     * Creates new form ProgramControl
     */
    public ProgramListControl() {
        initComponents();
        model = new ProgramTableModel();
        jTablePrograms.setModel(model);
        model.setControl(this);
        
        // Column Name
        jTablePrograms.getColumnModel().getColumn(0).setMinWidth(150);

        // Column Status
        jTablePrograms.getColumnModel().getColumn(1).setMinWidth(100);
        jTablePrograms.getColumnModel().getColumn(1).setCellRenderer(
            new ProgramStatusCellRenderer());     
        
        // Column Start
        jTablePrograms.getColumnModel().getColumn(2).setMinWidth(100);
        jTablePrograms.getColumnModel().getColumn(2).setCellRenderer(
            new ProgramDateCellRenderer());   
        
        // Column End
        jTablePrograms.getColumnModel().getColumn(3).setMinWidth(100);
        jTablePrograms.getColumnModel().getColumn(3).setCellRenderer(
            new ProgramDateCellRenderer());    
        
        // Column Messages
        jTablePrograms.getColumnModel().getColumn(4).setMinWidth(50);
        jTablePrograms.getColumnModel().getColumn(4).setMaxWidth(100); 
        
        // Column Errors
        jTablePrograms.getColumnModel().getColumn(5).setMinWidth(50);
        jTablePrograms.getColumnModel().getColumn(5).setMaxWidth(100);    
        jTablePrograms.getColumnModel().getColumn(5).setCellRenderer(
            new ErrorsCellRenderer());
        
        // Initilize with emtpy information
        setProgramInformation(null);
        
        jTablePrograms.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            processTableProgramsSelection();
        });
        
        jTableLog.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            processTableLogSelection();
        });

    }
    
    public void processTableProgramsSelection() {
        if (jTablePrograms.getSelectedRow() > -1) {
            setProgramInformation(model.getProgram(jTablePrograms.getSelectedRow()));
        }         
    }

    public void processTableLogSelection() {
        String logDetail = "";
        if (jTableLog.getSelectedRow() > -1) {
            LogEntry log = programLogModel.getLogEntry(jTableLog.getSelectedRow());
            if (log != null) {
                logDetail += "<b>Command:</b> " + log.getCommandName() + "<br>";
                logDetail += "<b>Message:</b> " + log.getMessage() + "<br>";
                if (log.getException() != null) {
                    logDetail += "<hr><b>Exception:</b> " + log.getException().toString() + "<br>";
                    StringWriter sw = new StringWriter();
                    log.getException().printStackTrace(new PrintWriter(sw));
                    logDetail += "<hr><b>Stack Trace:</b><br><code> " + sw.toString() + "</code><br>"; 
                }
            }
        }         
        jTextPaneLogDetail.setText(logDetail);
    }

    @Override
    public void disposeResources() {
        if (model != null) {
            model.disposeResources();
        }
        
        if (programLogModel != null) {
            programLogModel.disposeResources();
        }        
    }
    
    class ProgramStatusCellRenderer extends DefaultTableCellRenderer {
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
                        case ProgramContext.PROGRAM_NOT_STARTED:
                            setText("Not started");                            
                            break;
                        case ProgramContext.PROGRAM_RUNNING:
                            setText("Running");                            
                            break;
                        case ProgramContext.PROGRAM_INTERRUPTED:
                            setText("Interrupted");                            
                            break;
                        case ProgramContext.PROGRAM_ENDED:
                            setText("Ended");                            
                            break;                            
                        default:
                            setText("Unknown");                           
                    }
                }
                
          return this;
        } 
    }
    
    class ProgramDateCellRenderer extends DefaultTableCellRenderer {
            private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm:ss");
            
            @Override
            public Component getTableCellRendererComponent(
                JTable aTable, Object aValue, boolean aIsSelected, 
                boolean aHasFocus, int aRow, int aColumn) { 
        
        
                super.getTableCellRendererComponent(
                    aTable, aValue, aIsSelected, aHasFocus, aRow, aColumn
                );
        
                if (aValue != null && aValue instanceof Date) {
                    Date date = (Date) aValue;
                    setText(formatter.format(date));                            
                }
                
          return this;
        } 
    }
    
    class LogTypeCellRenderer extends DefaultTableCellRenderer {
            
            @Override
            public Component getTableCellRendererComponent(
                JTable aTable, Object aValue, boolean isSelected, 
                boolean hasFocus, int aRow, int aColumn) { 
        
        
                super.getTableCellRendererComponent(
                    aTable, aValue, isSelected, hasFocus, aRow, aColumn
                );
        
                if (aValue instanceof Integer) {
                    int type = (Integer) aValue;
                    switch (type) {
                        case LogEntry.INFO:
                            setForeground(isSelected?Color.WHITE:Color.GREEN.darker());
                            setText("INFO");
                            break;
                        case LogEntry.WARNING:
                            setForeground(isSelected?Color.YELLOW:Color.ORANGE.darker());
                            setText("WARNING");
                            break;
                        case LogEntry.ERROR:
                            setText("ERROR");
                            setForeground(isSelected?Color.YELLOW:Color.RED); 
                            break;
                    }
                                               
                }
                
          return this;
        } 
    }
    
    class ErrorsCellRenderer extends DefaultTableCellRenderer {
            
            @Override
            public Component getTableCellRendererComponent(
                JTable aTable, Object aValue, boolean isSelected, 
                boolean hasFocus, int aRow, int aColumn) { 
        
        
                super.getTableCellRendererComponent(
                    aTable, aValue, isSelected, hasFocus, aRow, aColumn
                );
        
                if (aValue instanceof Integer) {
                    int errors = (Integer) aValue;
                    if (errors > 0) {
                        setFont(getFont().deriveFont(Font.BOLD));
                        setForeground(isSelected?Color.YELLOW:Color.RED); 
                    } else {
                        setFont(getFont().deriveFont(Font.PLAIN));
                        setForeground(isSelected?Color.WHITE:Color.BLACK);                                                
                    }       
                }
                
          return this;
        } 
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane = new javax.swing.JSplitPane();
        jScrollPanePrograms = new javax.swing.JScrollPane();
        jTablePrograms = new javax.swing.JTable();
        jPanelProgram = new javax.swing.JPanel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jSplitPaneLogDetail = new javax.swing.JSplitPane();
        jPanelLog = new javax.swing.JPanel();
        jLabelLog = new javax.swing.JLabel();
        jScrollPaneLog = new javax.swing.JScrollPane();
        jTableLog = new javax.swing.JTable();
        jScrollPaneLogDetail = new javax.swing.JScrollPane();
        jTextPaneLogDetail = new javax.swing.JTextPane();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        jSplitPane.setDividerLocation(200);

        jTablePrograms.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPanePrograms.setViewportView(jTablePrograms);

        jSplitPane.setLeftComponent(jScrollPanePrograms);

        jPanelProgram.setLayout(new java.awt.BorderLayout());

        jSplitPaneLogDetail.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelLog.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 5, 5));
        jPanelLog.setLayout(new java.awt.BorderLayout());

        jLabelLog.setText("Program Log:");
        jPanelLog.add(jLabelLog, java.awt.BorderLayout.NORTH);

        jTableLog.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPaneLog.setViewportView(jTableLog);

        jPanelLog.add(jScrollPaneLog, java.awt.BorderLayout.CENTER);

        jSplitPaneLogDetail.setTopComponent(jPanelLog);

        jScrollPaneLogDetail.setMinimumSize(new java.awt.Dimension(23, 85));
        jScrollPaneLogDetail.setPreferredSize(new java.awt.Dimension(4, 85));

        jTextPaneLogDetail.setEditable(false);
        jTextPaneLogDetail.setContentType("text/html"); // NOI18N
        jScrollPaneLogDetail.setViewportView(jTextPaneLogDetail);

        jSplitPaneLogDetail.setBottomComponent(jScrollPaneLogDetail);

        jTabbedPane.addTab("Program Log", jSplitPaneLogDetail);

        jPanelProgram.add(jTabbedPane, java.awt.BorderLayout.CENTER);
        jTabbedPane.getAccessibleContext().setAccessibleName("Program Log");
        jTabbedPane.getAccessibleContext().setAccessibleDescription("");

        jSplitPane.setRightComponent(jPanelProgram);

        add(jSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jSplitPane.setDividerLocation(0.50);
    }//GEN-LAST:event_formComponentResized

    private void setProgramInformation(Program program) {
        this.program = program;

        // Terminate previous model
        if (programLogModel != null) {
           programLogModel.disposeResources();
        }

        // Build a new model
        if (program != null) {
           programLogModel = new LogEntryTableModel(program.getContext());
        } else {
           programLogModel = new LogEntryTableModel(null);
        }           
        jTableLog.setModel(programLogModel);     

        // Set layout for the date
        jTableLog.getColumnModel().getColumn(0).setMinWidth(120);
        jTableLog.getColumnModel().getColumn(0).setMaxWidth(150);
        jTableLog.getColumnModel().getColumn(0).setCellRenderer(
            new ProgramDateCellRenderer());       

        // Set layout for the type
        jTableLog.getColumnModel().getColumn(1).setMinWidth(60);
        jTableLog.getColumnModel().getColumn(1).setMaxWidth(80);
        jTableLog.getColumnModel().getColumn(1).setCellRenderer(
            new LogTypeCellRenderer());              

        // Set layout for the command name
        jTableLog.getColumnModel().getColumn(2).setMinWidth(150);
        jTableLog.getColumnModel().getColumn(2).setMaxWidth(200);

        // Clear the log selection
        processTableLogSelection();
    }
    
    public void tableUpdated() {
        if (program != null) {
            // Reprocess selected elements
            java.awt.EventQueue.invokeLater(() -> {
                int index = model.getIndex(program);
                jTablePrograms.getSelectionModel().setSelectionInterval(index, index);
            });
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelLog;
    private javax.swing.JPanel jPanelLog;
    private javax.swing.JPanel jPanelProgram;
    private javax.swing.JScrollPane jScrollPaneLog;
    private javax.swing.JScrollPane jScrollPaneLogDetail;
    private javax.swing.JScrollPane jScrollPanePrograms;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JSplitPane jSplitPaneLogDetail;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableLog;
    private javax.swing.JTable jTablePrograms;
    private javax.swing.JTextPane jTextPaneLogDetail;
    // End of variables declaration//GEN-END:variables
}
