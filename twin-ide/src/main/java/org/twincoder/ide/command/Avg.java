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
public class Avg extends Command implements Function {

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
        if (args.length > 0 && context != null) {
            try {
                for (int i=0; i < args.length; i++) {
                    if (args[i] != null) {
                        String value = args[i].getValue(context);
                        result = result + Double.parseDouble(value);
                    }                    
                }
                result = result / args.length;
            } catch (NumberFormatException e) {
                context.logError("Avg", "Error finding avg, check if all the arguments are valid numbers", e);
            }
        }
        
        return NumberUtils.numberToString(result);
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
    }
    
}
