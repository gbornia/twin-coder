/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

/**
 *
 * @author gbornia
 */
public abstract class Comparison extends Command implements Function {
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length < 2)
            throw new CommandArgumentException("Not enough arguments, the comparison needs at least two");

    }    
}
