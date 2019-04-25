/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.utils.CompilerUtils;

/**
 *
 * @author gbornia
 */
public class IsNumber extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        String result = "false";
        if (args.length == 1 && context != null) {
            try {
                String value = args[0].getValue(context);
                if (value != null) {
                    result = CompilerUtils.isNumber(value)?"true":"false";
                }
            } catch (NumberFormatException e) {
                context.logError("IsNumber","Error with isNumber, check arguments", e);
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
            throw new CommandArgumentException("The isNumber function needs 1 argument, e.g not(a = b)");

    }
    
}
