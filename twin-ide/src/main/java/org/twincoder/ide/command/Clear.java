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
public class Clear extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null) {
            context.getOutput().clear();
            return true;
        }
        return false;
    }
    
        @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args != null)
            throw new CommandArgumentException("Clear should not have any argument");
       
    }
    
}
