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
public class And extends Comparison implements Function {

    public And() {
        super();
    }
    
    @Override
    public boolean run(ProgramContext context) {
        return true;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        boolean result = true;
        if (context != null && args.length > 0) {
            try {
                for (int i=0; i < args.length; i++) {
                    if (args[i] != null) {                     
                        result = result && "true".equals(args[i].getValue(context));
                    }                    
                }
            } catch (NumberFormatException e) {
                context.logError("And", "Problem with comparison", e);
            }
        }
        
        return result + "";
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
    }
    
}
