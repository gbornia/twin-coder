/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gbornia
 */
public class Round extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        Function args[] = getArguments();
        if (args != null && args.length > 1) {
            if (args[0] instanceof Variable && args[1] != this) {
                context.setVariableValue(((Variable)args[0]).getName(), getValue(context));
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        long result = 0;
        if (args.length > 0 && context != null) {
            // In case there is a variable
            String value = args[0].getValue(context);
            try {
               result = Math.round(Double.parseDouble(value));
            } catch (NumberFormatException e) {
               context.logError("Round", "Error trying to round value " + value, e);
            }               
        }
        

        return result + "";
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length == 0 || args.length > 1)
            throw new CommandArgumentException("The round command needs only one argument");

    }
    
}
