/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;

/**
 *
 * @author gbornia
 */
public class Lower extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        String result = "";
        if (args.length == 1 && context != null) {
            try {
                String value = args[0].getValue(context);
                if (value != null) {
                    result = value.toLowerCase();
                }
            } catch (NumberFormatException e) {
                context.logError("Lower", "Error with lower, check arguments", e);
            }
        }
        

        return result;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 1)
            throw new CommandArgumentException("The lower function needs 1 argument, e.g lower(string)");

    }
    
}
