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
public class Substring extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        String result = "";
        if (args.length == 3 && context != null) {
            try {
                String value = args[0].getValue(context);
                int start = Math.round(Float.parseFloat(args[1].getValue(context)));
                int end = Math.round(Float.parseFloat(args[2].getValue(context)));
                if (value != null && start >=0 && end <= value.length()) {
                    result = value.substring(start, end);
                } else {
                    context.logWarning("Substring", "Wrong string, index or length; check arguments");
                }
            } catch (NumberFormatException e) {
                context.logError("Substring", "Error with substring, check arguments", e);
            }
        }
        

        return result;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 3)
            throw new CommandArgumentException("The substring function needs 3 arguments substring(string, start, end)");

    }
    
}
