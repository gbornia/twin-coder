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
public class CanvasOval extends CanvasObject {

    @Override
    protected void draw(Graphics g) {
        // x2 and y2 represents width and height
        
        if (isFill()) {
            g.setColor(getFillColor());
            g.fillOval(getX1(), getY1(), getX2(), getY2());
        }
        
        // Draw the border
        g.setColor(getColor());
        g.drawOval(getX1(), getY1(), getX2(), getY2());
    }

}
