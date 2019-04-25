/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gbornia
 */
public class Equal extends Comparison implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return true;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        boolean result = false;
        if (args.length > 1 && context != null) {
            int count = args.length-1;
            for (int i=1; i < args.length; i++) {
                try {
                   if (args[i-1].getValue(context).equals(args[i].getValue(context))) {
                       count--;
                   }
                   result = (count==0);
                } catch (Exception e) {
                   context.logError("Equal", "Error with variable content " + args[0], e);
                } 
            }
        }
        
        return result?"true":"false";
    }
    
}
