/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.program.InputListener;
import org.twincoder.ide.program.Program;

/**
 *
 * @author gabri
 */
public class Read extends Command implements InputListener {

    private String inputString = "";
    
    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            
            try {
                // Release the thread control
                context.release();
                
                // Calls the output for starting to read
                context.getOutput().startInput(this, context);
                
                // Regain the thread control
                context.acquire();

                // Process the results
                Function args[] = getArguments();
                if (args != null && args.length > 0 && args[0] instanceof Variable) {
                    context.setVariableValue(((Variable)args[0]).getName(), inputString);
                }
                return true;
            } catch (InterruptedException ex) {
                context.logError("Read", "Error trying to read (thread control)", ex);
            }
        }
        return false;
    }

    @Override
    public void inputReady(String inputString) {
        this.inputString = inputString;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 1 || !(args[0] instanceof Variable))
            throw new CommandArgumentException("The read command needs only one variable as argument");

    }
}
