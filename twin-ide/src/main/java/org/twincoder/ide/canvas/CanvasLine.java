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
public class CanvasLine extends CanvasObject {

    @Override
    protected void draw(Graphics g) {
        g.drawLine(getX1(), getY1(), getX2(), getY2());
    }

}
