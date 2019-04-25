/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.canvas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

/**
 *
 * @author gbornia
 */
public class CanvasRotateImage extends CanvasObject {

    private Image image;
    
    private int angle;
    private boolean rotateAtCenter = true;
    private int rotationX;
    private int rotationY;
    
    @Override
    protected void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (getImage() != null) {
            
            //Make a backup so that we can reset our graphics object after using it.
            AffineTransform backup = g2d.getTransform();
            
            if (isRotateAtCenter()) {
                rotationX = getX1() + getImage().getWidth(null)/2;
                rotationY = getY1() + getImage().getHeight(null)/2;
            }
            //rotationX is the x coordinate for rotation, rotationY is the y coordinate for rotation, and angle
            //is the angle to rotate the image. If you want to rotate around the center of an image,
            //use the image's center x and y coordinates for rx and ry.
            AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians(angle), rotationX, rotationY);
            
            //Set our Graphics2D object to the transform
            g2d.setTransform(a);
            
            //Draw our image like normal
            if (getX2() > 0 && getY2() > 0) {
                // Draw image based on size (stretch)
                g.drawImage(getImage(), getX1(), getY1(), getX2(), getY2(), null);
            } else {
                // Draw image to it's natural size
                g.drawImage(getImage(), getX1(), getY1(), null);
            }
            
            //Reset our graphics object so we can draw with it again.
            g2d.setTransform(backup);            
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

    /**
     * @return the angle
     */
    public int getAngle() {
        return angle;
    }

    /**
     * @param angle the angle to set
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }

    /**
     * @return the rotateAtCenter
     */
    public boolean isRotateAtCenter() {
        return rotateAtCenter;
    }

    /**
     * @param rotateAtCenter the rotateAtCenter to set
     */
    public void setRotateAtCenter(boolean rotateAtCenter) {
        this.rotateAtCenter = rotateAtCenter;
    }

    /**
     * @return the rotationX
     */
    public int getRotationX() {
        return rotationX;
    }

    /**
     * @param rotationX the rotationX to set
     */
    public void setRotationX(int rotationX) {
        this.rotationX = rotationX;
        setRotateAtCenter(false);
    }

    /**
     * @return the rotationY
     */
    public int getRotationY() {
        return rotationY;
    }

    /**
     * @param rotationY the rotationY to set
     */
    public void setRotationY(int rotationY) {
        this.rotationY = rotationY;
        setRotateAtCenter(false);
    }


}
