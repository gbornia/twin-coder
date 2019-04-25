/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import java.util.ArrayList;
import java.util.HashMap;
import org.twincoder.ide.program.ProgramContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.twincoder.ide.utils.JSONUtils;

/**
 *
 * @author gbornia
 */
public class MapValue extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
            String result = "";
        if (args.length == 2 && context != null) {
            // Arguments
            String mapJson = args[0].getValue(context);
            String key =args[1].getValue(context);

            if (mapJson != null) {
                HashMap<String,String> map = JSONUtils.jsonToStringHashMap(mapJson);
                if (map != null) {
                    if (key != null) {                          
                        result = map.get(key);
                    } else {
                        context.logWarning("MapValue", "The key is required for the map");
                    }
                } else {
                    context.logWarning("MapValue", "The map was not initizalized properly");
                }
            }
        }
        
        return result + "";
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 2)
            throw new CommandArgumentException("The mapValue function needs 2 arguments, e.g mapValue(array, \"myKey\")");

    }
    
}
