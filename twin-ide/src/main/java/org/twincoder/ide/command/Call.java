/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import java.util.Arrays;
import org.twincoder.ide.program.Program;
import org.twincoder.ide.program.ProgramContext;

/**
 *
 * @author gabri
 */
public class Call extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        // Call get value function
        getValue(context);
        
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
     
    }

    @Override
    public String getValue(ProgramContext context) {
        if (context!=null && getArguments()!=null) {
            Function args[] = getArguments();
            if (args != null) {
                Program program = context.getProgram();
                if (program != null) {
                    String functionName = args[0].getValue(context);
                    if (functionName != null) {
                        FunctionBlock function = program.getFunction(functionName.toLowerCase());
                        
                        // Set arguments and exclude the first one (function name)
                        if (args.length > 1) {
                            function.setArguments(Arrays.copyOfRange(args, 1, args.length));
                        }
                        
                        if (function != null) {
                            return function.getValue(context);
                        } else {
                            context.logError("Call", "Unable to find function " + functionName, null);
                        }
                    } else {
                        context.logError("Call", "Invalid function name, check arguments", null);
                    }                      
                        
                }

            }
        } 
        return null;
    }
    
}
