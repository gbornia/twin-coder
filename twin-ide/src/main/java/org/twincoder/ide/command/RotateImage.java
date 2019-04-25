/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.canvas.CanvasRotateImage;
import org.twincoder.ide.program.ProgramFileController;

/**
 *
 * @author gabri
 */
public class RotateImage extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 3)) {
                try {
                    String imagePath = args[0].getValue(context);
                    java.awt.Image img = ProgramFileController.getInstance().getImage(imagePath, context);

                    if (img != null) {
                        CanvasRotateImage image = new CanvasRotateImage();
                    
                        image.setImage(img);
                        image.setAngle(Math.round(Float.parseFloat(args[1].getValue(context))));                        
                        image.setX1(Math.round(Float.parseFloat(args[2].getValue(context))));
                        image.setY1(Math.round(Float.parseFloat(args[3].getValue(context))));
    
                        if (args.length >= 5) {
                            image.setX2(Math.round(Float.parseFloat(args[4].getValue(context))));
                            image.setY2(Math.round(Float.parseFloat(args[5].getValue(context))));
                        }
                        
                        context.getOutput().addCanvasObject(image);
                    } else {
                        if (ProgramFileController.getInstance().reportFileError("Image", imagePath)) {
                            context.logError("RotateImage", "Unable to open image", null);
                        }
                    }
                    
                } catch (NumberFormatException e) {
                    context.logError("RotateImage", "Error trying to draw Image, check arguments", e);
                    return false;
                }
            } else {
                context.logWarning("RotateImage", "Not enough arguments to draw the image");
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
