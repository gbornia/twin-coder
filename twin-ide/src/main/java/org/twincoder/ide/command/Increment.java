/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.utils.NumberUtils;

/**
 *
 * @author gbornia
 */
public class Increment extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        Function args[] = getArguments();
        if (args != null && args.length == 1) {
            if (args[0] instanceof Variable) {
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
        if (context != null && args.length == 1) {
            // In case there is a variable
            if (args[0] instanceof Variable) {
                String value = args[0].getValue(context);
                try {
                   result = Double.parseDouble(value) + 1;
                } catch (NumberFormatException e) {
                   context.logError("Increment",  "Invalid number [" + value + "]", e);
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
        
        if (args.length != 1)
            throw new CommandArgumentException("The increment only accepts one argument (variable)");
        
        if (args[0]!=null && !(args[0] instanceof Variable))
            throw new CommandArgumentException("The  argument of the increment should be a variable");
    }
    
}
