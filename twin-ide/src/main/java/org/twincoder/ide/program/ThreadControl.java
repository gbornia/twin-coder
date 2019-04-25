/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.program;

/**
 *
 * @author gbornia
 */
public interface ThreadControl {
    
    public void acquire() throws InterruptedException;
    
    public void release();
}
