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
public class Sqrt extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        double result = 0;
        if (args.length > 0 && context != null) {
            if (args[0] != null) {
                String value = args[0].getValue(context);
                try {
                   result = Math.sqrt(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                   context.logError("Sqrt", "Error calculating squared root of " + value, e);
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
        
        if (args.length == 0 || args.length > 1)
            throw new CommandArgumentException("The sqrt command needs only one argument");

    }
    
}
