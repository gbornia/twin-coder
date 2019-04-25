/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.command;

import java.util.ArrayList;

/**
 *
 * @author gabri
 */
public abstract class CompositeCommand extends Command {

    private final ArrayList<Command> commands;
    
    public CompositeCommand() {
        this.commands = new ArrayList<>();
    }

    /**
     * @return the commands
     */
    public ArrayList<Command> getCommands() {
        return commands;
    }
    
    
    public void addCommand(Command aCommand) {
        commands.add(aCommand);
    }
    
}
