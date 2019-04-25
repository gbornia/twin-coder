/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import java.util.HashMap;
import java.util.HashSet;
import org.twincoder.ide.program.ProgramContext;
import java.util.Iterator;
import org.twincoder.ide.utils.JSONUtils;

/**
 *
 * @author gbornia
 */
public class FunctionBlock extends CompositeCommand implements Function {
    private final String functionName;
    private String[] argNames;
    
    public FunctionBlock(String name, String[] argNames) {
        super();
        this.functionName = name;
        this.argNames = argNames;
    }

    public String[] getArgNames() {
        return argNames;
    }
    
    @Override
    public boolean run(ProgramContext context) {

        // Run the function
        String value = getValue(context);
   
        return true;
    }
    
    @Override
    public String getValue(ProgramContext context) {
        String returnResult = null;
        
        // Get condition
        if (context != null) {
            
            // Create args based on the parent content
            Function args[] = getArguments();
            String argNames[] = getArgNames();
            String argValues[] = null;
            
            // Get variable values for the parent context
            if (args!= null && argNames != null && args.length > 0 && args.length == argNames.length) {             
                argValues = new String[argNames.length];
                for (int i=0; i < argNames.length; i++) {
                    argValues[i] = args[i].getValue(context);
                }
            }            
            // Set the context scope
            context.addContextScope(this);
            
            // Set arguments for the function under the next context
            if (argNames != null && argValues != null && argNames.length == argValues.length) {
                for (int i=0; i < argNames.length; i++) {
                    context.setVariableValue(argNames[i], argValues[i]);
                }
            }
                
           
            // Navigate through all the commands loop
            Iterator<Command> i = getCommands().iterator();                     

            int step = 0;
            boolean result = true;
            // Procecss all the commands
            while (i.hasNext() && result) {
                Command aCommand = i.next();
                step++;

                try {
                    result &= aCommand.run(context);
                } catch (Exception e) {
                    context.logError("Function " + functionName, "Error during while command #" + step, e);
                    break;
                }
            }
            
            // Get the return for the context scope of the function
            returnResult = context.getVariableValue("return");
            
            // Remove context scope
            context.releaseContextScope(this);
        }
        
        return returnResult;
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
