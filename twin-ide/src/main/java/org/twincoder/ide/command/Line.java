/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.canvas.CanvasLine;
import org.twincoder.ide.canvas.CanvasUtils;

/**
 *
 * @author gabri
 */
public class Line extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 4)) {
                try {
                    CanvasLine line = new CanvasLine();
                    line.setX1(Math.round(Float.parseFloat(args[0].getValue(context))));
                    line.setY1(Math.round(Float.parseFloat(args[1].getValue(context))));
                    line.setX2(Math.round(Float.parseFloat(args[2].getValue(context))));
                    line.setY2(Math.round(Float.parseFloat(args[3].getValue(context))));
                    
                    if (args.length >= 5) {
                        line.setColor(CanvasUtils.stringToColor(args[4].getValue(context)));
                        if (args.length >= 6) {
                            line.setStroke(Float.parseFloat(args[5].getValue(context)));
                            if (args.length >= 7) {
                                line.setType(args[6].getValue(context));
                            }
                        }
                    }
                    context.getOutput().addCanvasObject(line);
                } catch (NumberFormatException e) {
                    context.logError("Line", "Error trying to draw line, check arguments", e);
                    return false;
                }
            } else {
                context.logWarning("Line", "Not enough arguments, ignoring Line command");
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
