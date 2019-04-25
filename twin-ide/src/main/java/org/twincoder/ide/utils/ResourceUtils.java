/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.utils;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author gbornia
 */
public class ResourceUtils {

    
    public static String getResource(String resourcePath) {
        String result = null;

        try {
            StringBuilder content = new StringBuilder("");
            
            //Get file from resources folder
            try (Scanner scanner = new Scanner(ResourceUtils.class.getResource(resourcePath).openStream())) {
              while (scanner.hasNextLine()) {
                      String line = scanner.nextLine();
                      content.append(line).append("\n");
              }

              scanner.close();

              result = content.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return result;
    }
}
