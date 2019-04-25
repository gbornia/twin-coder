/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.canvas.CanvasRectangle;
import org.twincoder.ide.canvas.CanvasUtils;

/**
 *
 * @author gabri
 */
public class Rectangle extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 4)) {
                try {
                    CanvasRectangle rectangle = new CanvasRectangle();
                    rectangle.setX1(Math.round(Float.parseFloat(args[0].getValue(context))));
                    rectangle.setY1(Math.round(Float.parseFloat(args[1].getValue(context))));
                    rectangle.setX2(Math.round(Float.parseFloat(args[2].getValue(context))));
                    rectangle.setY2(Math.round(Float.parseFloat(args[3].getValue(context))));
                    
                    // Line color
                    if (args.length >= 5) {
                        rectangle.setColor(CanvasUtils.stringToColor(args[4].getValue(context)));
                        
                        // Fill color
                        if (args.length >= 6) {
                            rectangle.setFill(true);
                            rectangle.setFillColor(CanvasUtils.stringToColor(args[5].getValue(context)));
                        }
                    }
                    context.getOutput().addCanvasObject(rectangle);
                } catch (NumberFormatException e) {
                    context.logError("Rectangle", "Error trying to draw the Rectangle, check arguments", e);
                    return false;
                }
            } else {
                context.logWarning("Rectangle", "Not enough arguments to draw the Rectangle");
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length < 4)
            throw new CommandArgumentException("Line requires at least 4 arguments, e.g. line(10,10,30,10)");
        
    }
    
}
