/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.twincoder.ide.utils.NumberUtils;

/**
 *
 * @author gbornia
 */
public class Divide extends Command implements Function {

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
        
        double result = 0;
        if (args.length > 1) {
            // In case there is a variable
            if (args[0] instanceof Variable && args[1] != this) {
                try {
                    result = Double.parseDouble(args[0].getValue(context));
                    result = result / Double.parseDouble(args[1].getValue(context));
                } catch (NumberFormatException e) {
                   context.logError("Divide", "Error trying to divide number", e);
                }               
            }
            
            if (args.length > 2) {
                try {
                    result = Double.parseDouble(args[1].getValue(context));
                    for (int i=2; i < args.length; i++) {
                        if (args[i] != null) {
                            String value = args[i].getValue(context);
                            result = result / Double.parseDouble(value);
                        }
                    }
                } catch (NumberFormatException e) {
                   context.logError("Divide", "Error trying to divide number", e);
                }
            }
        }
        
        return NumberUtils.numberToString(result);
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length < 2)
            throw new CommandArgumentException("Not enough arguments, division needs at least two");
        
        if (args[0]!=null && !(args[0] instanceof Variable))
            throw new CommandArgumentException("The first argument of the division should be a variable");
    }
    
}
