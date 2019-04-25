/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.program.ProgramFileController;

/**
 *
 * @author gbornia
 */
public class ReadFile extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        Function[] args = getArguments();
        if (args != null && args.length == 2 && context != null) {
            if (args[0] instanceof Variable && args[1] != this) {
                context.setVariableValue(((Variable)args[0]).getName(), getValue(context));
                return true;
            } else {
                context.logError("Set", "Invalid set command, check syntax", null);
            }
        }
        return false;
    }
    

    @Override
    public String getValue(ProgramContext context) {
        Function[] args = getArguments();
        if (args != null && args.length == 2 && context != null) {
            try {
                String fileName = args[1].getValue(context);
                return ProgramFileController.getInstance().getFileContent(fileName, context);
            } catch (Exception e) {
                context.logError("ReadFile", "Error trying to open file", e);                
            }
        }
        return "";
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 2 || !(args[0] instanceof Variable))
            throw new CommandArgumentException("The readFile command needs two arguments, first the variable and then the filename");

    }
    
}
