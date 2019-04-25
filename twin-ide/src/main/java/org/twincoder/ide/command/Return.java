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
public class Return extends Command {

    @Override
    public boolean run(ProgramContext context) {
        Function args[] = getArguments();
        
        if (args.length == 1 && context != null) {
            String value = args[0].getValue(context);
            if (value != null) {
                context.setVariableValue("return", value);
            }
        }
      
        return true;
    }


    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 1)
            throw new CommandArgumentException("The return function needs 1 argument, e.g return(\"OK\")");

    }
    
}
