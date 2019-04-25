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
public class Circle extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 3)) {
                try {
                    CanvasOval oval = new CanvasOval();
                    Float radius = Float.parseFloat(args[2].getValue(context));
                    oval.setX1(Math.round(Float.parseFloat(args[0].getValue(context))-radius));
                    oval.setY1(Math.round(Float.parseFloat(args[1].getValue(context))-radius));
                    oval.setX2(Math.round(radius*2));
                    oval.setY2(Math.round(radius*2));
                    
                    // Circle color
                    if (args.length >= 4) {
                        oval.setColor(CanvasUtils.stringToColor(args[3].getValue(context)));
                        
                        // Fill color
                        if (args.length >= 5) {
                            oval.setFill(true);
                            oval.setFillColor(CanvasUtils.stringToColor(args[4].getValue(context)));
                        }
                    }
                    context.getOutput().addCanvasObject(oval);
                } catch (NumberFormatException e) {
                    context.logError("Circle", "Problem creating circle, check if all arguments are valid", e);
                    return false;
                }
            } else {
                context.logError("Circle", "Invalid arguments for the circle", null);
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
            throw new CommandArgumentException("Circle requires at least 3 arguments, e.g. circle(10,10,30)");
        
    }
    
}
