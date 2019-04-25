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
public class Not extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        String result = "true";
        if (args.length == 1 && context != null) {
            try {
                String value = args[0].getValue(context);
                if (value != null) {
                    result = (!("true".equals(value)))?"true":"false";
                } else {
                    context.logWarning("Not", "Value was not initialized properly");
                }
            } catch (NumberFormatException e) {
                context.logError("Not", "Error with not, check arguments", e);
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
            throw new CommandArgumentException("The not function needs 1 argument, e.g not(a = b)");

    }
    
}
