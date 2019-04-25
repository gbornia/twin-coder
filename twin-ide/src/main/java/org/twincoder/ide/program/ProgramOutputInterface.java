/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.program;

import org.twincoder.ide.canvas.CanvasObject;

/**
 *
 * @author gbornia
 */
public interface ProgramOutputInterface extends ResourceDisposable {

    /**
     * Initialize the output for a given context
     * @param context 
     */
    public void initialize(ProgramContext context);

    /**
     * Clears all the output display
     */
    public void clear();
    
    /**
     * Prints a line of text without a carriage return at the end
     * @param text 
     */
    public void print(String text);
    
    /**
     * Prints a line and automatically add a carriage return at the end
     * @param text 
     */
    public void println(String text);
    
    /**
     * Add a canvas object to be drawn in the display
     * @param object 
     */
    public void addCanvasObject(CanvasObject object);
    
    /**
     * Starts to listen for input, callback is performed to the InputListener
     * @param inputListener
     * @param threadControl
     */
    public void startInput(InputListener inputListener, ThreadControl threadControl);
    
    
    /**
     * Set property for the output
     * @param property
     * @param value 
     */
    public void setProperty(String property, String value);
    
    
    /**
     * Set focus to the component so it can detect keyboard events
     */
    public void setFocus();
    
    /**
     * Get Width of the output
     * @return 
     */
    public int getWindowWidth();
    
    /**
     * Get Height of the output
     * @return 
     */
    public int getWindowHeight();
    
}
