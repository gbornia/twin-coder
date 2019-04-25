/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.canvas;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author gbornia
 */
public class CanvasImage extends CanvasObject {

    private Image image;
    
    @Override
    protected void draw(Graphics g) {
        if (getImage() != null) {
            if (getX2() > 0 && getY2() > 0) {
                // Draw image based on size (stretch)
                g.drawImage(getImage(), getX1(), getY1(), getX2(), getY2(), null);
            } else {
                // Draw image to it's natural size
                g.drawImage(getImage(), getX1(), getY1(), null);
            }
        }
    }


    /**
     * @return the image
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
    }


}
