/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import java.util.ArrayList;
import java.util.HashMap;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.utils.JSONUtils;

/**
 *
 * @author gbornia
 */
public class MapLength extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        int result = 0;
        if (args.length == 1 && context != null) {
            String mapJson = args[0].getValue(context);
            if (mapJson != null) {
                HashMap<String,String> map = JSONUtils.jsonToStringHashMap(mapJson);
                if (map != null) {
                    result = map.size();
                } else {
                    context.logWarning("MapLength", "Map doesn't seem to have been initialized correctly, length will be 0");
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
        
        if (args.length != 1)
            throw new CommandArgumentException("The mapLength function needs 1 argument, e.g mapLength(map)");

    }
    
}
