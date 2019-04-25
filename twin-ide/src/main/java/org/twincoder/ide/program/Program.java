/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.program;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.twincoder.ide.command.Command;
import org.twincoder.ide.command.CommandArgumentException;
import org.twincoder.ide.command.CompositeCommand;
import org.twincoder.ide.command.FunctionBlock;

/**
 *
 * @author Gabriel
 */
public class Program extends CompositeCommand {

    private ProgramContext context = null;
    private ProgramRun runningProgram;
    private final String name;
    private boolean compilerError = false;
    private static final SimpleDateFormat df = new SimpleDateFormat("hh:MM:ss.S");
    private HashMap<String, FunctionBlock> functions = new HashMap();
    
    public Program(String name) {
        super();
        this.name = name;
        this.context = new ProgramContext(this);
    }
    
    public String getName() {
        return name;
    }
    
    /**
     *
     * @param output
     */
    protected void run(ProgramOutputInterface output) {
        
        // Initialize context and output
        context.setOutput(output);
        output.initialize(context);
        output.setFocus();
        
        // Run thread
        runningProgram = new ProgramRun(this);
        runningProgram.start();
    }

    @Override
    public boolean run(ProgramContext context) {
        
        if (context == null) {
            return false;
        }
        
        boolean result = true;
        try {
            // Acquire semaphore
            context.acquire();
            
            // Set focus
            context.getOutput().setFocus();
            
            // Run all programs
            Iterator<Command> i = getCommands().iterator();
            while (i.hasNext() && result) {
                Command aCommand = i.next();

                try {
                    long start = System.currentTimeMillis();
                    result &= aCommand.run(context);
                    println("Command " + aCommand.getClass().getName() + " completed in " + (System.currentTimeMillis() - start) + "ms");
                } catch (Exception e) {
                    if (e instanceof NullPointerException) {
                        context.logError("Program", "Error running (" + aCommand.getClass().getSimpleName() + "), check variables", e);
                    } else {
                        context.logError("Program", "Error running (" + aCommand.getClass().getSimpleName() + ")", e);
                    }
                    return false;
                }
            }
            
            // Release semaphore
            context.release();
        } catch(InterruptedException e) {
            context.logError("Program", "Program has been interrupted)", e);
            Logger.getLogger(Program.class.getName()).log(Level.SEVERE, "Error running program", e);
            result = false;
        }
        
        return result;
    }
    
    class ProgramRun extends Thread {
        Program program;
        public ProgramRun(Program program) {
            this.program = program;
        }
        
        @Override
        public void run() {
            if (getContext() != null) {
                // Marks it as running
                context.markProgramStarted();
                
                // Run the program
                program.run(getContext());
                
                // Marks it as stopped
                context.markProgramEnded();
            }
        }
    }
   
    @Override
    public void validateArguments() throws CommandArgumentException {
    }

    /**
     * @return the context
     */
    public ProgramContext getContext() {
        return context;
    }

    /**
     * @return the running
     */
    protected boolean isRunning() {
        return (context.getProgramStatus() == ProgramContext.PROGRAM_RUNNING);
    }

    
    protected void stopProgram() {
        if (isRunning() && runningProgram != null) {
            runningProgram.interrupt();
            runningProgram = null;
            context.markProgramEnded(ProgramContext.PROGRAM_INTERRUPTED);
        }
    }

    /**
     * @return the compilerError
     */
    public boolean isCompilerError() {
        return compilerError;
    }

    /**
     * @param compilerError the compilerError to set
     */
    public void setCompilerError(boolean compilerError) {
        this.compilerError = compilerError;
    }
    
    /**
     * Register a function for the program
     * @param functionName
     * @param function 
     */
    public void registerFunction(String functionName, FunctionBlock function) {
        functions.put(functionName, function);
    }
    
    /**
     * Returns registered function
     * @param functionName
     * @return 
     */
    public FunctionBlock getFunction(String functionName) {
        return functions.get(functionName);
    }
    
    public static void println(String message) {
        System.out.println(df.format(new Date()) + message);
        
    }
}
