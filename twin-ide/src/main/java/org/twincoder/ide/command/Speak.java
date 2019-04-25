/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import com.sun.speech.freetts.Voice;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.program.ProgramController;

/**
 *
 * @author gbornia
 */
public class Speak extends Command {

    @Override
    public boolean run(ProgramContext context) {
        Function args[] = getArguments();
        
        if (args.length > 0 && context != null) {
            try {
                Voice voice = ProgramController.getController().getVoice();
                if (voice != null) {
                    for (int i=0; i < args.length; i++) {
                        String value = args[i].getValue(context);
                        if (value != null) {
                            voice.speak(value);
                        }
                    }
                }                    
            } catch (Exception e) {
                context.logError("Speak", "Error with speak, check arguments", e);
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 0)
            throw new CommandArgumentException("The speak function needs at least 1 argument, e.g speak(string)");

    }
    
}
