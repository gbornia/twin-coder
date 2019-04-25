/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

/**
 *
 * @author gbornia
 */
public class CommandArgumentException extends Exception {
    
    public CommandArgumentException(String message) {
        super(message);
    }
    
    public CommandArgumentException(String message, Exception e) {
        super(message, e);
    }
    
}
