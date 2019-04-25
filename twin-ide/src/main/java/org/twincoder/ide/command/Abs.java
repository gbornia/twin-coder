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
public class Abs extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        double result = 0;
        if (context != null && args.length > 0) {
            // In case there is a variable
            if (args[0] != null) {
                String value = args[0].getValue(context);
                try {
                   result = Math.abs(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                   context.logError("Abs", "Invalid number [" + value + "]", e);
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
            throw new CommandArgumentException("The round command needs only one argument");

    }
    
}
