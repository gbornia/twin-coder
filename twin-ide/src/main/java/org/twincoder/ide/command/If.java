/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import org.twincoder.ide.program.ProgramContext;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author gbornia
 */
public class If extends CompositeCommand {
    public static final int DEFAULT_BLOCK = 0;
    public static final int ELSE_BLOCK = 1;
    
    private final ArrayList<Command> elseCommands;
    private int currentBlock = DEFAULT_BLOCK;
    
    public If() {
        super();
        this.elseCommands = new ArrayList<>();
    }
    
    public void setCurrentBlock(int block) {
        this.currentBlock = block;
    }
    
    /**
     * @return the commands
     */
    public ArrayList<Command> getElseCommands() {
        return elseCommands;
    }
    
    @Override
    public void addCommand(Command aCommand) {
        if (currentBlock == DEFAULT_BLOCK) {
            super.addCommand(aCommand);
        } else {
            elseCommands.add(aCommand);
        }
    }
    
    @Override
    public boolean run(ProgramContext context) {
        boolean result = false;
        
        // Get condition
        Function args[] = getArguments();
        if (args.length > 0 && args[0]!= null && context != null) {
            result = true;
            String block;
            Iterator<Command> i = null;
            if ("true".equals(args[0].getValue(context))) {
                i = getCommands().iterator(); 
                block = "MAIN";
            } else {
                i = getElseCommands().iterator();
                block = "ELSE";
            }        
            
            // Procecss the command
            int step = 0;
            while (i.hasNext() && result) {
                
                Command aCommand = i.next();
                step++;
                
                try {
                    result &= aCommand.run(context);
                } catch (Exception e) {
                    context.logError("If", "Error inside the " + block + " block on step #" + step, e);
                    return false;
                }
            }
        }
               
        return result;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 1)
            throw new CommandArgumentException("The IF command needs only one comparison as argument");

    }
}
