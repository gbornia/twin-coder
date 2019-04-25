/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.utils.DateUtils;

/**
 *
 * @author gbornia
 */
public class Now extends Command implements Function {

    
    @Override
    public boolean run(ProgramContext context) {        
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {        
        return DateUtils.millisToDays(System.currentTimeMillis()) + "";
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args != null && args.length > 0)
            throw new CommandArgumentException("Now does not require any argument");

    }
    
}
