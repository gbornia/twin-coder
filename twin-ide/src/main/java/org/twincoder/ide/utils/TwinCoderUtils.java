/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.utils;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author gbornia
 */
public class TwinCoderUtils {
    
    public static String getStringInBetween(String content, String start, String end) {
        String result = null;
        
        if (content != null && start != null && end != null) {
            int startIndex = content.toLowerCase().indexOf(start.toLowerCase());
            int endIndex = content.toLowerCase().indexOf(end.toLowerCase());
            if ((startIndex > -1) && (endIndex > -1) && (endIndex > startIndex)) {
                result = content.substring(startIndex + start.length(), endIndex);
            }
        }
        
        return result;
    }
    
    
    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }    
}
