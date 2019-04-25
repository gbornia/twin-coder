/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import java.util.Iterator;

/**
 *
 * @author gbornia
 */
public class While extends CompositeCommand {


    public While() {
        super();
    }

    
    @Override
    public boolean run(ProgramContext context) {
        boolean result = false;
        
        // Get condition
        Function args[] = getArguments();
        if (args.length > 0 && args[0]!= null && context != null) {
            result = true;
            
            // While loop
            while (("true".equals(args[0].getValue(context))) && result) {
                Iterator<Command> i = getCommands().iterator();                     

                int step = 0;
                // Procecss all the commands
                while (i.hasNext() && result) {
                    Command aCommand = i.next();
                    step++;
                    
                    try {
                        result &= aCommand.run(context);
                    } catch (Exception e) {
                        context.logError("While", "Error during while command #" + step, e);
                        result = false;
                        break;
                    }
                }
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
            throw new CommandArgumentException("The while command needs one argument that returns 'true' or 'false'");

        if (args[0] instanceof Set)
            throw new CommandArgumentException("The while command needs one argument that returns 'true' or 'false'");
    }
}
