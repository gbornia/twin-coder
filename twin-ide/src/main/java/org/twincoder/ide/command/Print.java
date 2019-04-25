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
public class Print extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if (args != null) {
                for (Function arg : args) {
                    context.getOutput().print(arg != null ? arg.getValue(context) : "");
                }               
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
    }
    
}
