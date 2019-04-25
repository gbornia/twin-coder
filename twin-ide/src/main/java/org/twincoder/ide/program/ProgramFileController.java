/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.program;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gbornia
 */
public class ProgramFileController {
    
    private static ProgramFileController unique = null;
    
    private final HashMap<String, BufferedImage> images = new HashMap();
    
    private final HashSet<String> errorFiles = new HashSet();
    
    private final ProjectProperties properties;

    public static ProgramFileController getInstance() {
        if (unique == null) {
            unique = new ProgramFileController();
        }
        return unique;
    }
    
    public ProgramFileController() {
        properties = ProjectProperties.getProjectProperties();
    }
    
    public boolean reportFileError(String program, String path) {
        if (errorFiles.contains(program + ":" + path)) {
            return false;
        } else {
            errorFiles.add(program + ":" + path);
            return true;
        }
    }
    
    /**
     * Return an image for a path that could be a file or a url
     * @param path
     * @param context
     * @return 
     */
    public Image getImage(String path, ProgramContext context) {
        BufferedImage result = null;
        if(path != null && context != null) {            
            result = images.get(path);
            if (result == null) {
                try {
                    if (path.startsWith("http")) {
                        // Check cache for file first
                        if (properties.isCacheResources()) {
                            ProgramCache cache = ProgramCache.getCache();
                            File file = cache.getCachedURL(path);
                            if (file.exists()) {
                                result = ImageIO.read(file);
                            }
                        }
                        
                        if (result == null) {
                            result = ImageIO.read(new URL(path));
                        }
                    } else {                        
                        result = ImageIO.read(new File(path));
                    }
                    images.put(path, result);
                } catch (IOException e) {
                    if (reportFileError("ProgramFileController", path)) {
                        context.logError("Program", "Problem loading image [" + path +"]", e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the file content as a string for a path that could be a file or a url
     * @param path
     * @param context
     * @return 
     */
    public String getFileContent(String path, ProgramContext context) {
        String result = null;
        if(path != null && context != null) {            
            try {
                if (path.startsWith("http")) {
                    // Check cache for file first
                    ProgramCache cache = ProgramCache.getCache();
                    File file = cache.getCachedURL(path);
                    if (file.exists()) {
                        result = getFileContent(file);
                        
                        // If file is not supposed to be cached, then delete it
                        if (!properties.isCacheResources()) {
                            file.delete();
                        }
                        
                    }
                } else {                        
                    result = getFileContent(new File(path));
                }
            } catch (IOException e) {
                if (reportFileError("ProgramFileController", path)) {
                    context.logError("Program", "Problem loading file [" + path +"]", e);
                }
            }
        }
        return result;        
    }
    
    /**
     * Returns the content of a file as a string
     * @param file
     * @return
     * @throws IOException 
     */
    public String getFileContent(File file) throws IOException {

        return FileUtils.readFileToString(file);
    }
}
