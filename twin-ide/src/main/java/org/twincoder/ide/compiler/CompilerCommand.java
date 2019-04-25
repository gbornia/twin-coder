/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gbornia
 */
public class CompilerCommand {
    private static Map<String,String> commandMap;
    private static String[] availableCommands; 
    
    
    private static Map<String,String> getCommandMap() {
        if (commandMap == null) {
            commandMap = new HashMap<>();
            commandMap.put("abs", "org.twincoder.ide.command.Abs");
            commandMap.put("add", "org.twincoder.ide.command.Add");
            commandMap.put("arc", "org.twincoder.ide.command.Arc");
            commandMap.put("avg", "org.twincoder.ide.command.Avg");
            commandMap.put("background", "org.twincoder.ide.command.Background");
            commandMap.put("call", "org.twincoder.ide.command.Call");
            commandMap.put("circle", "org.twincoder.ide.command.Circle");
            commandMap.put("clear", "org.twincoder.ide.command.Clear");
            commandMap.put("concat", "org.twincoder.ide.command.Concat");
            commandMap.put("cos", "org.twincoder.ide.command.Cos");
            commandMap.put("equal", "org.twincoder.ide.command.Equal");
            commandMap.put("divide", "org.twincoder.ide.command.Divide");
            commandMap.put("div", "org.twincoder.ide.command.Div");
            commandMap.put("for", "org.twincoder.ide.command.For");
            commandMap.put("formatdate", "org.twincoder.ide.command.FormatDate");
            commandMap.put("greater", "org.twincoder.ide.command.Greater");
            commandMap.put("greaterorequal", "org.twincoder.ide.command.GreaterOrEqual");
            commandMap.put("height", "org.twincoder.ide.command.WindowHeight");            
            commandMap.put("if", "org.twincoder.ide.command.If");
            commandMap.put("image", "org.twincoder.ide.command.Image");
            commandMap.put("increment", "org.twincoder.ide.command.Increment");
            commandMap.put("isnumber", "org.twincoder.ide.command.IsNumber");
            commandMap.put("keypressed", "org.twincoder.ide.command.KeyPressed");
            commandMap.put("length", "org.twincoder.ide.command.Length");
            commandMap.put("lesser", "org.twincoder.ide.command.Lesser");
            commandMap.put("lesserorequal", "org.twincoder.ide.command.LesserOrEqual");
            commandMap.put("line", "org.twincoder.ide.command.Line");
            commandMap.put("log", "org.twincoder.ide.command.Log");
            commandMap.put("lower", "org.twincoder.ide.command.Lower");
            commandMap.put("min", "org.twincoder.ide.command.Min");
            commandMap.put("maplength", "org.twincoder.ide.command.MapLength");
            commandMap.put("mapkeys", "org.twincoder.ide.command.MapKeys");
            commandMap.put("mapvalue", "org.twincoder.ide.command.MapValue");
            commandMap.put("mapset", "org.twincoder.ide.command.MapSet");
            commandMap.put("max", "org.twincoder.ide.command.Max");
            commandMap.put("mod", "org.twincoder.ide.command.Mod");
            commandMap.put("multiply", "org.twincoder.ide.command.Multiply");
            commandMap.put("not", "org.twincoder.ide.command.Not");
            commandMap.put("now", "org.twincoder.ide.command.Now");
            commandMap.put("oval", "org.twincoder.ide.command.Oval");
            commandMap.put("pi", "org.twincoder.ide.command.Pi");
            commandMap.put("polygon", "org.twincoder.ide.command.Polygon");
            commandMap.put("position", "org.twincoder.ide.command.Position");
            commandMap.put("power", "org.twincoder.ide.command.Power");
            commandMap.put("print", "org.twincoder.ide.command.Print");
            commandMap.put("program", "org.twincoder.ide.command.Program");
            commandMap.put("println", "org.twincoder.ide.command.Println");
            commandMap.put("random", "org.twincoder.ide.command.Random");
            commandMap.put("read", "org.twincoder.ide.command.Read");
            commandMap.put("readfile", "org.twincoder.ide.command.ReadFile");
            commandMap.put("rectangle", "org.twincoder.ide.command.Rectangle");
            commandMap.put("replace", "org.twincoder.ide.command.Replace");
            commandMap.put("return", "org.twincoder.ide.command.Return");
            commandMap.put("rotateimage", "org.twincoder.ide.command.RotateImage");
            commandMap.put("round", "org.twincoder.ide.command.Round");
            commandMap.put("set", "org.twincoder.ide.command.Set");
            commandMap.put("sin", "org.twincoder.ide.command.Sin");
            commandMap.put("speak", "org.twincoder.ide.command.Speak");
            commandMap.put("sqrt", "org.twincoder.ide.command.Sqrt");
            commandMap.put("substring", "org.twincoder.ide.command.Substring");
            commandMap.put("subtract", "org.twincoder.ide.command.Subtract");
            commandMap.put("text", "org.twincoder.ide.command.Text");
            commandMap.put("todate", "org.twincoder.ide.command.ToDate");
            commandMap.put("trim", "org.twincoder.ide.command.Trim");
            commandMap.put("upper", "org.twincoder.ide.command.Upper");
            commandMap.put("wait", "org.twincoder.ide.command.Wait");
            commandMap.put("while", "org.twincoder.ide.command.While");
            commandMap.put("width", "org.twincoder.ide.command.WindowWidth");
        }
        return commandMap;
    }
    
    public static String getClassForCommand(String command) {
        if (command != null) {
            return getCommandMap().get(command);
        }
        return null;
    }
    
    public static String[] getAvailableReservedWords() {
        return new String[] {"program", "variables","begin","end","if","then","else","while","for","case","when","function"};
    }
    
    public static String[] getAvailableCommandNames() {
        if (availableCommands == null) {
            availableCommands = getCommandMap().keySet().toArray(new String[]{});
        }
        return availableCommands;
    }    
}
