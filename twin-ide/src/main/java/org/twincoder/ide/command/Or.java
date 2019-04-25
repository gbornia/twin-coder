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
public class Or extends Comparison implements Function {

    public Or() {
        super();
    }
    
    @Override
    public boolean run(ProgramContext context) {
        return true;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        boolean result = false;
        if (args.length > 0 && context != null) {
            try {
                for (int i=0; i < args.length; i++) {
                    if (args[i] != null) {                     
                        result = result || "true".equals(args[i].getValue(context));
                    }                    
                }
            } catch (Exception e) {
                context.logError("Or", "Error performing or, check if all the arguments are valid comparisons",e);
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
