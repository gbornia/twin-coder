/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.canvas.CanvasArc;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.canvas.CanvasOval;
import org.twincoder.ide.canvas.CanvasUtils;

/**
 *
 * @author gabri
 */
public class Arc extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 3)) {
                try {
                    CanvasArc arc = new CanvasArc();
                    Float radius = Float.parseFloat(args[2].getValue(context));
                    arc.setX1(Math.round(Float.parseFloat(args[0].getValue(context))-radius));
                    arc.setY1(Math.round(Float.parseFloat(args[1].getValue(context))-radius));
                    arc.setX2(Math.round(radius*2));
                    arc.setY2(Math.round(radius*2));
                    arc.setStartAngle(Float.parseFloat(args[3].getValue(context)));
                    arc.setEndAngle(Float.parseFloat(args[4].getValue(context)));
                    
                    // Circle color
                    if (args.length >= 6) {
                        arc.setColor(CanvasUtils.stringToColor(args[5].getValue(context)));
                        
                        // Fill color
                        if (args.length >= 7) {
                            arc.setFill(true);
                            arc.setFillColor(CanvasUtils.stringToColor(args[6].getValue(context)));
                        }
                    }
                    context.getOutput().addCanvasObject(arc);
                } catch (NumberFormatException e) {
                    context.logError("Arc", "Problem creating arc, check if all arguments are valid", e);
                    return false;
                }
            } else {
                context.logError("Arc", "Invalid arguments for the circle", null);
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length < 5)
            throw new CommandArgumentException("Arc requires at least 5 arguments, e.g. arc(10,10,30,0,45)");
        
    }
    
}
