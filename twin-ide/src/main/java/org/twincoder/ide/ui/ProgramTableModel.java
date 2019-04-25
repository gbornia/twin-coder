/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.ui;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.table.AbstractTableModel;
import org.twincoder.ide.program.Program;
import org.twincoder.ide.program.ProgramController;
import org.twincoder.ide.program.ResourceDisposable;

/**
 *
 * @author gbornia
 */
public class ProgramTableModel extends AbstractTableModel implements Observer, ResourceDisposable {

    private final ProgramController controller;
    private final Timer timer;
    private long lastUpdateFromController = 0;
    private long lastTableUpdateFired = 0;
    private ProgramListControl control;
    
    public ProgramTableModel() {
        controller = ProgramController.getController();
        controller.addObserver(this);
        timer = new Timer();
        timer.schedule(new TimerUpdate(), 1000, 1000);
    }
    
    @Override
    public int getRowCount() {
        return controller.programCount();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int rowIndex) {
        switch (rowIndex) {
            case 0: return "Program";
            case 1: return "Status";
            case 2: return "Started";
            case 3: return "Ended";
            case 4: return "Messages";
            case 5: return "Errors";
        }
        return null;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Program program = controller.getProgramAt(rowIndex);
        if (program != null) {
            switch (columnIndex) {
                case 0: return program.getName();
                case 1: return controller.getProgramStatus(program);
                case 2: return controller.getProgramStarted(program);
                case 3: return controller.getProgramEnded(program);
                case 4: return controller.getProgramMessagesCount(program);
                case 5: return controller.getProgramErrorCount(program);
            }
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if ("Program".equals(arg)) {
            lastUpdateFromController = System.currentTimeMillis();
        }
    }

    @Override
    public void disposeResources() {
        if (timer != null) {
            timer.cancel();
        }
        
        if (controller != null) {
            controller.deleteObserver(this);
        }
    }
    
    /**
     * Updates the table if there are changes from the observed object
     */
    class TimerUpdate extends TimerTask {

        @Override
        public void run() {
            if (lastUpdateFromController > lastTableUpdateFired) {
                lastTableUpdateFired = System.currentTimeMillis();
                fireTableDataChanged();
                if (control != null) {
                    control.tableUpdated();
                }
            }
        }
        
    }
    
    /**
     * Returns the program at a give position
     * @param rowIndex
     * @return 
     */
    public Program getProgram(int rowIndex) {
        return controller.getProgramAt(rowIndex);
    }

    /**
     * Returns the index of a program
     * @param program
     * @return 
     */
    public int getIndex(Program program) {
        return controller.getProgramIndex(program);
    }
    /**
     * @return the control
     */
    public ProgramListControl getControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(ProgramListControl control) {
        this.control = control;
    }
    
}
