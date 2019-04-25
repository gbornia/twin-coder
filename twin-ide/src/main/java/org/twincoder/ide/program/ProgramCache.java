/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.program;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gbornia
 */
public class ProgramCache {
    
    private static ProgramCache cache = null;
    
    public static ProgramCache getCache() {
        if (cache == null) {
            cache = new ProgramCache();
        }
        return cache;
    }
    
    private ProjectProperties properties;
    
    private ProgramCache() {
        properties = ProjectProperties.getProjectProperties();
    }
    
    /**
     * Returns the cached file
     * @param url
     * @return
     * @throws IOException 
     */
    public File getCachedURL(String url) throws IOException {
        File file = null;
        if (url != null) {
            try {
                // Determine file path
                String filePath = getCacheFolder() + "/" + getCachedFilename(url);
                file = new File(filePath);
                
                // If exists, return it from cache
                if (!file.exists()) {
                    // If it doesn't exist, download it
                    FileUtils.copyURLToFile(new URL(url), file);
                }
            } catch (IOException e) {
                throw e;
            }
            
        }
        return file;
    }
    
    /**
     * Return cache folder and verifies if the cache folder exists otherwise it creates it
     * @return 
     */
    private String getCacheFolder() {
        // Check if folder exists
        File dir = new File(properties.getCacheFolder());
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return properties.getCacheFolder();
    }
    
    // Transform a URL into a filename
    private String getCachedFilename(String url) {
        String result = null;
        if (url != null) {
            result = url.replaceAll("\\:", "_");
            result = result.replaceAll("\\/", "_");
            result = result.replaceAll("\\.", "_");
        }
        return result;
    }    
    
}
