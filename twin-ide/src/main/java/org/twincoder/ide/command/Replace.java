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
public class Replace extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        String result = "";
        if (args.length == 3 && context != null) {
            String string = args[0].getValue(context);
            String searchString = args[1].getValue(context);
            String replacementString = args[2].getValue(context);
            if (string != null && searchString != null && replacementString != null) {
                result = string.replaceAll(searchString, replacementString);
            } else {
                context.logWarning("Replace", "Some of the replace parameters were not initialized properly");
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
            throw new CommandArgumentException("The substring function needs 3 arguments substring(string, searchString, replacementString)");

    }
    
}
