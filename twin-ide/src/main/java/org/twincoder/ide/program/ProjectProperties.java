/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.program;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gbornia
 */
public class ProjectProperties {
    
    private static ProjectProperties projectProperties = null;
    public static final int MAX_LAST_USED_FILE_COUNT = 10;
    
    private Properties properties;
    private File file;
    private ArrayList<String> openFiles;
    private ArrayList<String> lastUsedFiles;
    
    public static ProjectProperties getProjectProperties() {  
        return ProjectProperties.getProjectProperties(new File("twin-properties.xml"));
    }     
    
    public static ProjectProperties getProjectProperties(File file) {       
        if (projectProperties == null) {
            projectProperties = new ProjectProperties(file);
       }
        return projectProperties;
    }
    
    private ProjectProperties(File file) {
        this.properties = new Properties();
        this.file = file;
        this.openFiles = new ArrayList<>();
        this.lastUsedFiles = new ArrayList<>();
        
        try {
            if (file != null && file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    properties.loadFromXML(fis);
                    openFiles.addAll(getArrayFromProperties("file.open.list"));
                    lastUsedFiles.addAll(getArrayFromProperties("file.lastUsed.list"));
                }
            } else {
                // Initialize it for the first time with default properties
                properties.put("file.auto.open", "true");
            }
        } catch (IOException e) {
            Logger.getLogger(ProjectProperties.class.getName()).log(Level.SEVERE, "Unable to load default properties for the project",e);
        }        
    }
    
    public void save() {
        if (file != null) {
            try {
                syncArrayToProperties("file.open.list", getOpenedFiles());
                syncArrayToProperties("file.lastUsed.list", getLastUsedFiles());
                
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    properties.storeToXML(fos, "Default Twin Code project properties");
                }
            } catch (Exception e) {
                Logger.getLogger(ProjectProperties.class.getName()).log(Level.SEVERE, "Error saving properties for the project", e);

            }
        } else {
            Logger.getLogger(ProjectProperties.class.getName()).log(Level.SEVERE, "Unable to save properties for the project (no file specified)");

        }
    }
    
    public boolean isFileAutoOpen() {
        return "true".equalsIgnoreCase((String)properties.getOrDefault("file.auto.open", "true"));
    }
    
    public void setFileAutoOpen(boolean fileAutoOpen) {
        properties.put("file.auto.open", fileAutoOpen?"true":"false");
    }
    
    public boolean isCacheResources() {
        return "true".equalsIgnoreCase((String)properties.getOrDefault("cache.resources", "true"));
    }
    
    public void setCacheResources(boolean cacheResources) {
        properties.put("cache.resources", cacheResources?"true":"false");
    }
    
    public String getCacheFolder() {
        return (String) properties.getOrDefault("cache.folder", System.getProperty("user.dir") + "/cache");
    }
    
    public void setCacheFolder(String cacheFolder) {
        properties.put("cache.folder", cacheFolder);
    }
    
    public void addOpenFile(String filePath) {
        if (!openFiles.contains(filePath)) {
            // Add it to the last position
            openFiles.add(filePath);
        } else {
            // Remove it from previous position and add it to the last
            openFiles.remove(filePath);
            openFiles.add(filePath);
        }
        addLastUsedFile(filePath);
    }
    
    public void removeOpenFile(String filePath) {
        openFiles.remove(filePath);
    }
    
    public void addLastUsedFile(String filePath) {
        
       if (lastUsedFiles.contains(filePath)) {
           lastUsedFiles.remove(filePath);
           lastUsedFiles.add(filePath);
       } else {
           // Remove the latest position until there is room for the new one
           while (! (lastUsedFiles.size() < MAX_LAST_USED_FILE_COUNT)) {
               lastUsedFiles.remove(0);
           }
           lastUsedFiles.add(filePath);
       }
    }
    
    public String[] getOpenedFiles() {
        return openFiles.toArray(new String[0]);
    }

    public String[] getLastUsedFiles() {
        return lastUsedFiles.toArray(new String[0]);
    }
    
    private void syncArrayToProperties(String propertyName, String[] array) {
        if (propertyName != null && array != null) {
            properties.put(propertyName + ".size", array.length + "");
            for (int i=0; i < array.length; i++) {
                properties.put(propertyName + ".value[" + i + "]", array[i]);
            }
        }
    }
    
    private ArrayList<String> getArrayFromProperties(String propertyName) {
        ArrayList<String> result = new ArrayList<>();
        if (propertyName != null) {
            int size = Integer.parseInt(properties.getProperty(propertyName + ".size","0"));
            for (int i=0; i < size; i++) {
                result.add(properties.getProperty(propertyName + ".value[" + i + "]"));
            }
        }
        return result;
    }
}
