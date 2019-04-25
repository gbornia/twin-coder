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
public class CanvasArc extends CanvasObject {

    private float startAngle;
    private float endAngle;
    
    @Override
    protected void draw(Graphics g) {
        // x2 and y2 represents width and height
        
        if (isFill()) {
            g.setColor(getFillColor());
            g.fillArc(getX1(), getY1(), getX2(), getY2(), (int)getStartAngle(), (int)getEndAngle());
        }
        
        // Draw the border
        g.setColor(getColor());
        g.drawArc(getX1(), getY1(), getX2(), getY2(), (int)getStartAngle(), (int)getEndAngle());

}

    /**
     * @return the startAngle
     */
    public float getStartAngle() {
        return startAngle;
    }

    /**
     * @param startAngle the startAngle to set
     */
    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * @return the endAngle
     */
    public float getEndAngle() {
        return endAngle;
    }

    /**
     * @param endAngle the endAngle to set
     */
    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }

}