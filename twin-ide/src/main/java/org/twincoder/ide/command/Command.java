package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;

/**
 *
 * @author Gabriel
 */
public abstract class Command {

    private Function[] args = null;
    
    /**
     * Executes the command
     * @param context
     * @return true if successful, false otherwise.
     */
    public abstract boolean run(ProgramContext context);

    /**
     * @return the args
     */
    public Function[] getArguments() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArguments(Function[] args) {
        this.args = args;
    }
    
    /**
     * Method to validate arguments
     * @throws CommandArgumentException 
     */
    public abstract void validateArguments() throws CommandArgumentException;
    
}
