/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.program;

import java.io.File;

/**
 *
 * @author gbornia
 */
public interface FileInterface {
    
    public boolean isSaveAvailable();
    
    public boolean isSaveNeeded();
    
    public boolean isSaveAsAvailable();
    
    public boolean loadFile(File file);
    
    public boolean saveToFile(File file);
    
    public boolean save();
    
    public File getFile();
    
    /**
     *
     * @return
     */
    public String getFileName();
    
}
