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
public class Min extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        double result = 0;
        if (args.length > 0 && context != null) {
            try {
                result = Double.parseDouble(args[0].getValue(context));
                for (int i=1; i < args.length; i++) {
                    if (args[i] != null) {
                        String value = args[i].getValue(context);
                        result = Math.min(result, Double.parseDouble(value));
                    }                    
                }
            } catch (NumberFormatException e) {
                context.logError("Min", "Error finding min, check if all the arguments are valid numbers", e);
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
