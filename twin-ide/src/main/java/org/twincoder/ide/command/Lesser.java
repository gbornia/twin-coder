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
public class Lesser extends Comparison implements Function {

    @Override
    public boolean run(ProgramContext context) {        
        return true;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        boolean result = false;
        if ((args.length==2) && (args[0]!=null) && (args[1]!=null) && context != null) {
            try {
               result = (Double.parseDouble(args[0].getValue(context)) < Double.parseDouble(args[1].getValue(context))); 
            } catch (NumberFormatException e) {
               context.logError("Lesser", "Error with comparison between " + args[0] + " and " + args[1], e);
            }
        }
        

        return result?"true":"false";
    }
    
}
