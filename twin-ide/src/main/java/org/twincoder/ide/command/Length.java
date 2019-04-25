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
public class Length extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        int result = 0;
        if (args.length == 1 && context != null) {
            try {
                String value = args[0].getValue(context);
                if (value != null) {
                    result = value.length();
                } else {
                    context.logWarning("Length", "String was not initialized");
                }                
            } catch (NumberFormatException e) {
                context.logError("Length", "Error with length, check arguments", e);
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
            throw new CommandArgumentException("The length function needs 1 argument, e.g length(string)");

    }
    
}
