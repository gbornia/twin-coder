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
public class Power extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        double result = 0;
        if (args.length > 1 && context != null) {
            if (args[0] != null && args[1] != null) {
                String valueA = args[0].getValue(context);
                String valueB = args[1].getValue(context);
                try {
                   double a = Double.parseDouble(valueA);
                   double b = Double.parseDouble(valueB);
                   result = Math.pow(a, b);
                } catch (NumberFormatException e) {
                   context.logError("Power", "Error calculating power of numbers " + valueA + " and " + valueB, e);
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
        
        if (args.length != 2)
            throw new CommandArgumentException("The power function requires two arguments (e.g. power(3,2)");

    }
    
}
