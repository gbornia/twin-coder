/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import java.util.ArrayList;
import org.twincoder.ide.canvas.CanvasPolygon;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.canvas.CanvasUtils;
import org.twincoder.ide.utils.CompilerUtils;

/**
 *
 * @author gabri
 */
public class Polygon extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 6)) {
                try {
                    ArrayList<String> pointArray = new ArrayList<>();
                    String color = null;
                    String fillColor = null;
                    for (int i=0; i < args.length; i++) {
                        String value = args[i].getValue(context);
                        if (CompilerUtils.isNumber(value)) {
                            pointArray.add(value);
                        } else {
                            if (color == null) {
                                color = args[i].getValue(context);
                            } else {
                                if (fillColor == null) {
                                    fillColor = args[i].getValue(context);
                                 }
                            }
                        }
                    }
                    
                    if (pointArray!=null && pointArray.size() >= 6) {
                        int points[] = new int[pointArray.size()];
                        for (int i=0; i < pointArray.size(); i++) {
                            points[i] = Math.round(Float.parseFloat(pointArray.get(i)));
                        }
                        
                        CanvasPolygon polygon = new CanvasPolygon();
                        polygon.setPolygonPoints(points);
                        if (color != null) {
                            polygon.setColor(CanvasUtils.stringToColor(color));
                        }
                        if (fillColor != null) {
                            polygon.setFill(true);
                            polygon.setFillColor(CanvasUtils.stringToColor(fillColor));
                        }
                        
                        context.getOutput().addCanvasObject(polygon);
                    } else {
                        context.logWarning("Polygon", "Not enough values to draw the Polygon, check arguments");
                    }                   
                } catch (NumberFormatException e) {
                    context.logError("Polygon", "Error trying to draw Polygon, check arguments", e);
                    return false;
                }
            } else {
                context.logWarning("Polygon", "Not enough arguments to draw a Polygon");
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length < 6)
            throw new CommandArgumentException("Polygon requires at least 6 arguments, e.g. line(10,10,30,10,40,50)");
        
    }
    
}
