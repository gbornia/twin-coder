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
public class Variable implements Function {
    String name;
    
    public Variable(String name) {
        this.name = (name!=null?name.toLowerCase():null);
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String getValue(ProgramContext context) {
        if (context != null) {
            return context.getVariableValue(name);
        }
        return null;
    }
    
}
