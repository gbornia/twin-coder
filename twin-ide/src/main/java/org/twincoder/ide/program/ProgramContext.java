/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.program;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.Semaphore;

/**
 *
 * @author gabri
 */
public class ProgramContext extends Observable implements ThreadControl {
    public static final int PROGRAM_NOT_STARTED = 0;
    public static final int PROGRAM_RUNNING = 1;
    public static final int PROGRAM_ENDED = 2;
    public static final int PROGRAM_INTERRUPTED = 3;
    
    public static final int MAX_LOG_ENTRIES = 1000;
    
    private final HashMap<String,String> variables;
    private final Semaphore semaphore;
    private final Properties properties;
    private ProgramOutputInterface output;
    private String lastKeyPressed;
    private Program program;
    
    private ArrayList<LogEntry> log;
    private int[] logSummary = new int[] {0,0,0,0};
    
    private int programStatus = PROGRAM_NOT_STARTED;
    private long programStarted = 0;
    private long programEnded = 0;
    private Stack scope;
    
    public ProgramContext(Program program) {
        this.variables = new HashMap<>();
        this.semaphore = new Semaphore(1);
        this.properties = new Properties();
        this.log = new ArrayList<>();
        this.scope = new Stack();
        this.program = program;
    }

    public Program getProgram() {
        return program;
    }
    
    /**
     * Add a new context scope for a give object
     * @param o 
     */
    public void addContextScope(Object o) {
        if (o != null) {
            scope.push(getContextScopeCode(o));
        }
    }
     
    /**
     * Releases the context scope
     */
    public void releaseContextScope(Object o) {
        if (!scope.empty()) {
            if (scope.peek().equals(getContextScopeCode(o))) {
                scope.pop();
            } else {
                logError("Context", "Unable to release context scope", null);
            }
        }
    }
    
    /**
     * Return the current content scope
     * @return 
     */
    public String getCurrentContextScope() {
        if (!scope.empty()) {
            return scope.peek().toString();
        } else {
            return getContextScopeCode(program);
        }
    }
    
    /**
     * Privately generate a context scope code for an object
     * @param o
     * @return 
     */
    private String getContextScopeCode(Object o) {
        if (o != null) {
            return o.hashCode()+"";
        } else {
            return program.hashCode() + "";
        }
    }
    
    /**
     * @return the output
     */
    public ProgramOutputInterface getOutput() {
        return output;
    }
    
    public boolean containsVariable(String variable) {
        return variables.containsKey(variableKey(variable));
    }
    
    public void addVariable(String variable) {
        variables.put(variableKey(variable), null);
        setChanged();
        notifyObservers("Variable");
    }
    
    public void setVariableValue(String variable, String value) {
        variables.put(variableKey(variable), value);
        setChanged();
        notifyObservers("Variable");
    }
    
    private String variableKey(String variableName) {
        return getCurrentContextScope() + ":" + variableName;
    }
    
          
    public String getVariableValue(String variable) {
        return variables.get(variableKey(variable));
    }
    
    public HashMap<String,String> getVariables() {
        return variables;
    }
    
    public void markProgramStarted() {
        programStatus = PROGRAM_RUNNING;
        programStarted = System.currentTimeMillis();
        setChanged();
        notifyObservers("Program");
    }
    
    public void markProgramEnded() {
        markProgramEnded(PROGRAM_ENDED);
    }
        
    public void markProgramEnded(int endedStatus) {
        programStatus = endedStatus;
        programEnded = System.currentTimeMillis();
        
        // Terminate output
        if (output != null) {
            output.disposeResources();
            output = null;
        }
        
        // Notify observers
        setChanged();
        notifyObservers("Program");
    }

    /**
     *
     * @throws InterruptedException
     */
    @Override
    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }
    
    /**
     * 
     */
    @Override
    public void release() {
        semaphore.release();
    }
    
    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
    
    public int getIntProperty(String propertyName) {
        try {
            return Integer.parseInt(properties.getProperty(propertyName,"0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void setProperty(String propertyName, String value) {
        properties.put(propertyName, value);
    }

    /**
     * @param output the output to set
     */
    public void setOutput(ProgramOutputInterface output) {
        this.output = output;
    }
    
    
    public void setKeyPressed(String keyPressed) {
        this.lastKeyPressed = keyPressed;
    }
    
    public String getKeyPressed() {
        return lastKeyPressed;
    }

    /**
     * @return the programStatus
     */
    public int getProgramStatus() {
        return programStatus;
    }

    /**
     * @return the programStarted
     */
    public long getProgramStarted() {
        return programStarted;
    }

    /**
     * @return the programEnded
     */
    public long getProgramEnded() {
        return programEnded;
    }

    /**
     * @return the log
     */
    public ArrayList<LogEntry> getLog() {
        return log;
    }
    
    public void logInfo(String commandName, String message) {
        log(LogEntry.INFO, commandName, message, null);
    }
    
    public void logWarning(String commandName, String message) {
        log(LogEntry.WARNING, commandName, message, null);
    }

    public void logError(String commandName, String message, Exception e) {
        log(LogEntry.ERROR, commandName, message, e);
    }
    
    public void log(int type, String commandName, String message, Exception e) {
        // Maintain log size under control
        while (log.size() > MAX_LOG_ENTRIES) {
            log.remove(0);
        }
        
        // Add new entry
        LogEntry entry = new LogEntry(new Date(), type, commandName, message, e);
        log.add(entry);
        
        // Update summary
        if (type >= 0 && type < logSummary.length) {
            logSummary[type]++;
        }
        
        // Notify observers
        setChanged();
        notifyObservers("Log");
    }
    
    public int[] getLogSummary() {
        return logSummary;
    }
}
