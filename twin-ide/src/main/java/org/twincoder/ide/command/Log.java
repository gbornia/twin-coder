/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;

/**
 *
 * @author gabri
 */
public class Log extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            String logStr = "";
            if (args != null) {
                for (int i=0; i < args.length; i++) {
                    logStr = logStr + (args[i]!=null?args[i].getValue(context):"");
                }
            }
            context.logInfo("Log", logStr);
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
    }
    
}
