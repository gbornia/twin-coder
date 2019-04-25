/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.utils.JSONUtils;

/**
 *
 * @author gbornia
 */
public class MapKeys extends Command implements Function {

    @Override
    public boolean run(ProgramContext context) {
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        String result = "";
        if (args.length == 1 && context != null) {
            String mapJson = args[0].getValue(context);
            if (mapJson != null) {
                HashMap<String,String> map = JSONUtils.jsonToStringHashMap(mapJson);
                if (map != null) {
                    // Process the keys and insert it as a new map
                    java.util.Set<String> keys = map.keySet();
                    Iterator<String> i = keys.iterator();
                    HashMap<String, String> resultKeys = new HashMap();
                    int count = 0;
                    while (i.hasNext()) {
                        resultKeys.put(count++ + "", i.next());
                    }
                    
                    // Return the map with the keys
                    result = JSONUtils.stringHashMapToJson(resultKeys);
                } else {
                    context.logWarning("MapKeys", "Map doesn't seem to have been initialized correctly, keys will be empty");
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
            throw new CommandArgumentException("The mapKeys function needs 1 argument, e.g mapKeys(map)");

    }
    
}
