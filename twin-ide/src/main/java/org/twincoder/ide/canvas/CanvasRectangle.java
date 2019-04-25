/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.canvas;

import java.awt.Graphics;

/**
 *
 * @author gbornia
 */
public class CanvasRectangle extends CanvasObject {

    @Override
    protected void draw(Graphics g) {
        
        if (isFill()) {
            g.setColor(getFillColor());
            g.fillRect(getX1(), getY1(), getX2(), getY2());
        }
        
        // Draw the border
        g.setColor(getColor());
        g.drawRect(getX1(), getY1(), getX2(), getY2());
    }

}
