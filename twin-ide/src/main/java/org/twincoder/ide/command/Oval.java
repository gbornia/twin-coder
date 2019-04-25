/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.canvas.CanvasOval;
import org.twincoder.ide.canvas.CanvasUtils;

/**
 *
 * @author gabri
 */
public class Oval extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 4)) {
                try {
                    CanvasOval oval = new CanvasOval();
                    oval.setX1(Math.round(Float.parseFloat(args[0].getValue(context))));
                    oval.setY1(Math.round(Float.parseFloat(args[1].getValue(context))));
                    oval.setX2(Math.round(Float.parseFloat(args[2].getValue(context))));
                    oval.setY2(Math.round(Float.parseFloat(args[3].getValue(context))));
                    
                    // Circle color
                    if (args.length >= 5) {
                        oval.setColor(CanvasUtils.stringToColor(args[4].getValue(context)));
                        
                        // Fill color
                        if (args.length >= 6) {
                            oval.setFill(true);
                            oval.setFillColor(CanvasUtils.stringToColor(args[5].getValue(context)));
                        }
                    }
                    context.getOutput().addCanvasObject(oval);
                } catch (NumberFormatException e) {
                    context.logError("Oval", "Error trying to draw Oval, check arguments", e);
                    return false;
                }
            } else {
                context.logWarning("Oval", "Not enough arguments to draw the oval");
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length < 3)
            throw new CommandArgumentException("Oval requires at least 4 arguments, e.g. circle(10,10,30,20)");
        
    }
    
}
