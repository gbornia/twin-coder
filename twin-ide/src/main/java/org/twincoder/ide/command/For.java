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
public class For extends CompositeCommand {


    public For() {
        super();
    }

    
    @Override
    public boolean run(ProgramContext context) {
        boolean result = false;
        
        // Get condition
        Function args[] = getArguments();
        if (args.length == 3 && args[0]!= null && args[1]!= null && args[2]!= null && context != null) {
            result = true;
            
            // Attribution
            if (args[0] instanceof Set) {
                Set attribution = (Set) args[0];
                if (!attribution.run(context)) {
                    context.logError("For", "Error executing Set, For cannot continue", null);
                    return false;
                }
            } else {
                context.logError("For", "First argument should be a Set command (e.g. i=0)", null);
                return false;
            }
            
            // Comparison
            Comparison comparison = null;
             if (args[1] instanceof Comparison) {
                comparison = (Comparison) args[1];
            } else {
                context.logError("For", "Second argument should be a Comparison command (e.g. i<10)", null);
                return false;
            }           
             
            // Increment
            Increment increment = null;
             if (args[2] instanceof Increment) {
                increment = (Increment) args[2];
            } else {
                context.logError("For", "Third argument should be an Increment command (e.g. i++)", null);
                return false;
            } 
            // While loop
            while (("true".equals(comparison.getValue(context))) && result) {
                Iterator<Command> i = getCommands().iterator();                     

                int step = 0;
                // Procecss all the commands
                while (i.hasNext() && result) {
                    Command aCommand = i.next();
                    step++;
                    
                    try {
                        result &= aCommand.run(context);
                    } catch (Exception e) {
                        context.logError("For", "Error during while command #" + step, e);
                        return false;                      
                    }
                }
                
                // Run increment
                if (!increment.run(context)) {
                    context.logError("For", "Problem running increment, for loop cannot continue", null);
                    return false;
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
        
        if (args.length != 3)
            throw new CommandArgumentException("The for command needs three arguments (attribution, comparison, increment)");

        if (!(args[0] instanceof Set))
            throw new CommandArgumentException("The first argument should be an attribution, example: for(i=0, i<10, i++)");

        if (!(args[1] instanceof Comparison))
            throw new CommandArgumentException("The second argument should be a comparison, example: for(i=0, i<10, i++)");

        if (!(args[2] instanceof Increment))
            throw new CommandArgumentException("The third argument should be an increment, example: for(i=0, i<10, i++)");
    }
}
