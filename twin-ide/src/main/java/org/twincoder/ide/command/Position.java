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
public class Position extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        int result = -1;
        if (args.length == 2 && context != null) {
            String string = args[0].getValue(context);
            String substring = args[1].getValue(context);
            if (string != null && substring != null) {
                result = string.indexOf(substring);
            } else {
                context.logWarning("Position", "One of the strings was not initialized properly");
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
            throw new CommandArgumentException("The position function needs 2 arguments substring(string, substring)");

    }
    
}
