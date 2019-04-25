/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import java.awt.Font;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.canvas.CanvasText;
import org.twincoder.ide.canvas.CanvasUtils;
import java.awt.Font;
/**
 *
 * @author gabri
 */
public class Text extends Command {

    @Override
    public boolean run(ProgramContext context) {
        if (context!=null && context.getOutput()!= null && getArguments()!=null) {
            Function args[] = getArguments();
            if ((args != null) && (args.length >= 3)) {
                try {
                    CanvasText text = new CanvasText();
                    text.setX1(Math.round(Float.parseFloat(args[0].getValue(context))));
                    text.setY1(Math.round(Float.parseFloat(args[1].getValue(context))));
                    text.setText((args[2].getValue(context)));
                    
                    // Font defaults
                    String fontName = "Arial";
                    int fontSize = 12;
                    int fontStyle = Font.PLAIN;
                    
                    // Text color
                    if (args.length > 3) {
                        text.setColor(CanvasUtils.stringToColor(args[3].getValue(context)));
                        
                        // Font name
                        if (args.length > 4) {
                            fontName = args[4].getValue(context);
                            
                            // Font size
                            if (args.length > 5) {
                                fontSize = Math.round(Float.parseFloat(args[5].getValue(context)));
                                
                                // Font style
                                if (args.length > 6) {
                                    String typeString = args[6].getValue(context).trim().toLowerCase();
                                    fontStyle = "bold".equals(typeString)?Font.BOLD:"italic".equals(typeString)?Font.ITALIC:Font.PLAIN;
                                }
                            }
                        }
                    }
                    text.setFont(new Font(fontName, fontStyle, fontSize));
                    context.getOutput().addCanvasObject(text);
                } catch (NumberFormatException e) {
                    context.logError("Text", "Problem creating text, check if all arguments are valid", e);
                    return false;
                }
            } else {
                context.logError("Text", "Invalid arguments for the text", null);
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
            throw new CommandArgumentException("Text requires at least 3 arguments, e.g. text(10,10,\"Hello World\")");
        
    }
    
}
