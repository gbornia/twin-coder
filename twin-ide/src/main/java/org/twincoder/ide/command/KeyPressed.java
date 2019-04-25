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
public class KeyPressed extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {        
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {      
        if (context != null) {
            String key = context.getKeyPressed();
            context.setKeyPressed(null);
            return key!=null?key:"";
        }
        return "";
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args != null && args.length > 0)
            throw new CommandArgumentException("keyPressed does not require any argument");

    }
    
}
