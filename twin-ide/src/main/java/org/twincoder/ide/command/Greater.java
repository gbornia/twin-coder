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
public class Greater extends Comparison implements Function {

    @Override
    public boolean run(ProgramContext context) {        
        return true;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        boolean result = false;
        if ((args.length==2) && (args[0]!=null) && (args[1]!=null) && context != null) {
            String valueA = args[0].getValue(context);
            String valueB = args[1].getValue(context);
            try {
               result = (Double.parseDouble(valueA) > Double.parseDouble(valueB)); 
            } catch (NumberFormatException e) {
               context.logError("Greater", "Error with comparison between [" + valueA + "] and [" + valueB + "]", e);
            }
        }
        

        return result?"true":"false";
    }
    
    
}
