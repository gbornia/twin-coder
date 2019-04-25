/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;

/**
 *
 * @author gbornia
 */
public class StringLiteral implements Function {
    String value;
    
    public StringLiteral(String value) {
        this.value = value;
    }
    
    @Override
    public String getValue(ProgramContext context) {
        return value;
    }
    
}
