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
public class Wait extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length == 1)) {
                try {

                    Thread.sleep(Integer.parseInt(args[0].getValue(context)));

                } catch (NumberFormatException e) {
                    context.logError("Wait", "Error trying to get milliseconds to wait (check number)", e);
                    return false;
                } catch (InterruptedException ex) {
                    context.logError("Wait", "Wait was interrupted", ex);
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 1)
            throw new CommandArgumentException("Wait required one argument with the wait in milliseconds, e.g. wait(250)");
        
    }
    
}
