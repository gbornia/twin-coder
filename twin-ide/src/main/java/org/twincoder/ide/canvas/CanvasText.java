/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.canvas;

import java.awt.Font;
import java.awt.Graphics;

/**
 *
 * @author gbornia
 */
public class CanvasText extends CanvasObject {

    private Font font = new Font("Arial", Font.PLAIN, 12);
    private String text;
    
    public void setFont(Font aFont) {
        if (aFont != null) {
            this.font = aFont;
        }
    }
    
    public Font getFont() {
        return this.font;

    }
    
    public void setText(String aText) {
        this.text = aText;
    }
    
    public String getText() {
        return this.text;
    }
    
    @Override
    protected void draw(Graphics g) {
        if (text != null && font != null) {
            g.setFont(font);
            g.drawString(text, getX1(), getY1());
        }
    }

}
