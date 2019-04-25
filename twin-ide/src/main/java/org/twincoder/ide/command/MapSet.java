/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import java.util.ArrayList;
import java.util.HashMap;
import org.twincoder.ide.program.ProgramContext;
import java.util.logging.Logger;
import org.twincoder.ide.utils.JSONUtils;

/**
 *
 * @author gbornia
 */
public class MapSet extends Command {

    
    @Override
    public boolean run(ProgramContext context) {
        Function args[] = getArguments();
        
        if (args.length == 3 && context != null) {
            // Arguments
            String mapJson = args[0].getValue(context);
            String key = args[1].getValue(context);
            String value = args[2].getValue(context);

            // Initialize list 
            HashMap<String,String> map = null;
            if (mapJson != null) {
                map = JSONUtils.jsonToStringHashMap(mapJson);
            }           
            if (map == null) {
                map = new HashMap();
            }

            // Set value in the map
            map.put(key, value);

            // Set the variable with the new array
            context.setVariableValue(((Variable)args[0]).getName(), JSONUtils.stringHashMapToJson(map));

            return true;
        }
        
        return false;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length != 3)
            throw new CommandArgumentException("The arrayValue function needs 3 arguments, e.g arraySet(array, 2, value)");

    }
    
}
