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
public class CanvasPolygon extends CanvasObject {

    int xPoints[];
    int yPoints[];
    
    public void setPolygonPoints(int points[]) {
        if (points != null && points.length >= 6) {
            int size = points.length / 2;
            xPoints = new int[size];
            yPoints = new int[size];
            for (int i=0; i < size; i++) {
                xPoints[i] = points[i*2];
                yPoints[i] = points[i*2+1];
            }
        }
    }
    
    @Override
    protected void draw(Graphics g) {
        
        if (xPoints!= null && yPoints!= null && xPoints.length >= 3 && yPoints.length >= 3 && xPoints.length == yPoints.length) {
            if (isFill()) {
                g.setColor(getFillColor());
                g.fillPolygon(xPoints, yPoints, xPoints.length); 
            }

            // Draw the border
            g.setColor(getColor());
            g.drawPolygon(xPoints, yPoints, xPoints.length); 
        }
    }

}
