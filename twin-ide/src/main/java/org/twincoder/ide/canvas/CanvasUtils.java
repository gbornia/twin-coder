/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.canvas;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gbornia
 */
public class CanvasUtils {
  
    /**
     * Returns a Color object from a string (returns Black in case of problems)
     * @param value
     * @return 
     */
    public static Color stringToColor(String value) {
        return stringToColor(value, Color.black);
    }
    
    /**
     * Returns a Color object from a string (returns defaultColor in case of problems)
     * @param value
     * @param defaultColor
     * @return 
     */
    public static Color stringToColor(String value, Color defaultColor) {
        Color result = defaultColor;
        
        if (value != null) {
            try {
                result = Color.decode(value);
            } catch (NumberFormatException n) {
                try {
                    result = (Color) Color.class.getField(value).get(null);
                } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                    Logger.getLogger(CanvasUtils.class.getName()).log(Level.SEVERE, "Error decoding color (" + value + "), using black", e);
                }
            }
        }
        
        return result;
    }
}
