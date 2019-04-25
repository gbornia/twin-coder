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
public class Println extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if (args != null) {
                for (int i=0; i < args.length-1; i++) {
                    context.getOutput().print(args[i]!=null?args[i].getValue(context):"");
                }
                context.getOutput().println(args[args.length-1]!=null?args[args.length-1].getValue(context):"");
            } else {
                context.getOutput().println("");
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
    }
    
}
