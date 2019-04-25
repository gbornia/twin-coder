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
import org.twincoder.ide.program.LogEntry;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.program.ResourceDisposable;

/**
 *
 * @author gbornia
 */
public class LogEntryTableModel  extends AbstractTableModel implements Observer, ResourceDisposable {

    private final ProgramContext context;
    private final Timer timer;
    private long lastUpdateFromContext = 0;
    private long lastTableUpdateFired = 0;
   
    public LogEntryTableModel(ProgramContext context) {
        this.context = context;
        if (context != null) {
            context.addObserver(this);
        }
        timer = new Timer();
        timer.schedule(new TimerUpdate(), 1000, 1000);
    }
    
    @Override
    public int getRowCount() {
        if (context != null && context.getLog() != null) {
            return context.getLog().size();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0: return "Date";
            case 1: return "Type";
            case 2: return "Command";
            case 3: return "Message";
        }
        return null;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogEntry log = getLogEntry(rowIndex);
        if (log != null) {
            switch(columnIndex) {
                case 0: return log.getDate();
                case 1: return log.getType();
                case 2: return log.getCommandName();
                case 3: return log.getMessage();
            }
        }
        return null;
    }

    public LogEntry getLogEntry(int rowIndex) {
        if (rowIndex >-1 && context != null && context.getLog() != null) {
            return context.getLog().get(rowIndex);
        }
        return null;
        
    }
    @Override
    public void update(Observable o, Object arg) {
        if ("Log".equals(arg)) {
            lastUpdateFromContext = System.currentTimeMillis();
        }
    }

    @Override
    public void disposeResources() {
        if (timer != null) {
            timer.cancel();
        }
        
        if (context != null) {
            context.deleteObserver(this);
        }
    }
    
    class TimerUpdate extends TimerTask {

        @Override
        public void run() {
            if (lastUpdateFromContext > lastTableUpdateFired) {
                fireTableDataChanged();
                lastTableUpdateFired = System.currentTimeMillis();
            }
        }
        
    }
    
}
