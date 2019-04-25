/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.twincoder.ide.compiler.CompilerCommand;

/**
 *
 * @author gbornia
 */
public class CompilerUtils {
 
    public static String getCleanFunctionStr(String functionStr) {
        String result = "";
        if (functionStr != null) {
            // Clear functionStr for processing
            result = functionStr.trim();
            if (result.startsWith("(") && result.endsWith(")")) {
                result = result.substring(1,result.length()-1);
            }
        }
        return result;
    }
    
    public static boolean isValidCommand(String commandName) {
        if (!"".equals(commandName)) {
            return (CompilerCommand.getClassForCommand(commandName.toLowerCase()) != null);
        }        
        return false;
    }
    
    public static boolean isCommand(String functionStr) {
        boolean result = false;
        
        if (functionStr != null) {           
            functionStr = getCleanFunctionStr(functionStr);
                               
            int parenthesis = 0;
            int separator = 0;
            String command = "";           
            for (int i=0; i < functionStr.length(); i++) {
                if (functionStr.charAt(i) == '(') {
                    separator++;
                    parenthesis++;
                } else
                if (functionStr.charAt(i) == ')') {
                    separator++;
                    parenthesis--;
                } else 
                if (parenthesis == 0) {
                    if (separator > 0) {
                        // A new command trying to be formed after a separator (no good)
                        return false;
                    }
                    if (Character.isAlphabetic(functionStr.charAt(i))) {    
                        command = command + functionStr.charAt(i);
                    } else {
                        // Some other character that will separate the command into another
                        separator++;
                    }
                          
                }
            }
            
            // True if command is not empty
            result = isValidCommand(command);
        }
        return result;
    }
    
    public static boolean isComparison(String functionStr) {
        if (functionStr != null) {
            // Clean string
            functionStr = getCleanFunctionStr(functionStr);
            String string = getTransformedComparison(functionStr);     
            StringTokenizer st = new StringTokenizer(string, "|&~^{}:!", true);
            return st.countTokens() > 1;
        }
        return false;
    }
    
    public static String getTransformedComparison(String functionStr) {
        if (functionStr != null) {
            functionStr = getCleanFunctionStr(functionStr);
            functionStr = functionStr.replaceAll("\\b(or)\\b", "|");
            functionStr = functionStr.replaceAll("\\b(and)\\b", "&");
            functionStr = functionStr.replaceAll(">=", "}");
            functionStr = functionStr.replaceAll("<=", "{");
            functionStr = functionStr.replaceAll("==", ":");
            functionStr = functionStr.replaceAll("!=", "!");
            functionStr = functionStr.replaceAll(">", "~");
            functionStr = functionStr.replaceAll("<", "^");
        }
        
        return functionStr;
    }
    
    public static String[] getComparisonBlocks(String string) {
        if (string != null) {
            return string.split("or|and");
        }
        return null;
    }
    
    public static boolean isAttribution(String line) {
        if (line != null) {
            line = getCleanFunctionStr(line);
            String parts[] = line.split(RegexUtils.REGEX_ATTRIBUTION);
            if (parts != null && parts.length == 2) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns the first word (command) for a line
     * @param string
     * @return The name of the command
     */
    public static String getCommandName(String string) {
        if (string != null) {
            string = getCleanFunctionStr(string);
            string = string.trim();
            Pattern r = Pattern.compile("^([\\w\\-]+)");
            Matcher m = r.matcher(string);
            if (m.find()) {
                return m.group(0).trim().toLowerCase();            
            }
        }
        return null;
    }    
    
    public static String getCommandArgumentString(String string) {
        
        if (string != null) {
            string = getCleanFunctionStr(string);
            int start = string.indexOf("(");
            int end = string.lastIndexOf(")");
            
            if ((start > 0) && (end > -1) && (end > start)) {
                String command = string.substring(0, start).toLowerCase().trim();
                if ("if".equals(command) || "while".equals(command)) {
                   return string.substring(start, end+1).trim();
                } else {
                    return string.substring(start+1, end).trim();
                }
            }
        }
        
        return null;
    }
    
    public static boolean isMathExpression(String string, HashSet<String> variables, HashSet<String> functionNames) {
        boolean result = false;
        boolean operand = false;
        if (string != null) {
            if (!isCommand(string) && !isFunction(string, functionNames)) {
                string = getCleanFunctionStr(string);
                if (string.endsWith("++")) {
                    return false;
                }
                StringTokenizer st = new StringTokenizer(string, "+-*/()", true);
                result = (st.countTokens()>1);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken().trim();
                    if ("+-*/".contains(token)) {
                        operand = true;
                    }
                    result = (isNumber(token) || variables.contains(token.toLowerCase()) || "+-*/()".contains(token) || isCommand(token));
                }
            }
        }
        return result && operand;
    }    
    
    public static boolean isIncrement(String string, HashSet variables) {
        if (string != null && variables != null) {
            string = string.trim().toLowerCase();
            if (string.endsWith("++")) {
                String variable = string.replaceAll("\\+\\+", "");
                if (variables.contains(variable)) {
                    return true;
                }
            }
        }
        return false;
    }    
      
    public static String[] getCommandParameters(String commandLine) {
        ArrayList<String> parameters = new ArrayList<>();
        
        if (commandLine != null) {
            commandLine = getCleanFunctionStr(commandLine);
            String parameter = "";
            boolean quote = false;
            int parenthesis = 0;
            
            for (int i=0; i < commandLine.length(); i++) {
                switch (commandLine.charAt(i)) {
                    case '\"':
                        quote = !quote;
                        parameter = parameter + commandLine.charAt(i);
                        break;
                    case '(':
                        if (!quote) {
                            parenthesis++;
                        }
                        parameter = parameter + commandLine.charAt(i);
                        break;
                    case ')':
                        if (!quote) {
                            parenthesis--;
                        }
                        parameter = parameter + commandLine.charAt(i);
                        break;
                    case ',':
                        if (!quote && parenthesis == 0) {
                            parameters.add(parameter.trim());
                            parameter = "";                           
                        } else {
                            parameter = parameter + commandLine.charAt(i);
                        }
                        break;
                    default:
                        parameter = parameter + commandLine.charAt(i);
                } 
            }
            parameters.add(parameter.trim());
        }
         
        return parameters.toArray(new String[]{});
    }
    
    public static boolean isString(String string) {
        if (string != null) {
            string = getCleanFunctionStr(string);
            return (string.trim().startsWith("\"") && string.trim().endsWith("\""));
        }
        return false;
    }
    
    public static boolean isNumber(String string) {
        if (string != null) {
            string = getCleanFunctionStr(string);
            try {
                Double.parseDouble(string);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }    
    
    
    public static String clearCodeComments(String code) {
        String result = code;
        
        if (result != null) {
            // Temporarily solve the http:// from being removed
            result = result.replaceAll("://",";!!");
            
            // Replace single line comment that starts with //
            result = result.replaceAll(RegexUtils.REGEX_SINGLE_LINE_COMMENT," ");
            
            // Replace block comment that starts with /* and ends with */
            Pattern pt = Pattern.compile(RegexUtils.REGEX_MULTI_LINE_COMMENT, Pattern.DOTALL);
            Matcher matcher = pt.matcher(code);
            while (matcher.find()) {
                String comment = matcher.group(0);
                if (comment != null) {
                    String lines[] = comment.split("\n");
                    String replacementLines = "";
                    for (int i=0; i < lines.length-1; i++) {
                        replacementLines = replacementLines + "\n";
                    }
                    result = result.replaceAll(Pattern.quote(comment), replacementLines);
                }
            }  
            
            // Restore the http:// 
            result = result.replaceAll(";!!", "://");
        }
        return result;
    }
    
    /**
     *
     * @param line
     * @return
     */
    public static Map<String,String> getCommandMap(String line) {
        HashMap<String,String> result = new HashMap();
        
        if (line != null) {
            // Process commands with parenthesis
            String command = "";
            int parenthesis = 0;
            int index = 0;
            boolean insideString = false;
            
            // Clear and remove surrounding parenthesis if exists
            String commandStr = getCleanFunctionStr(line);;
            if (commandStr.startsWith("(") && commandStr.endsWith(")")) {
                commandStr = commandStr.substring(1, commandStr.length()-1);
            }
            
            for (int i=0; i < commandStr.length(); i++) {
                if (!"".equals(command) && commandStr.charAt(i) == '(' && !insideString) {
                    parenthesis++;
                } 
                else if (!"".equals(command) && commandStr.charAt(i) == ')' && parenthesis > 0 && !insideString) {
                    command = command + commandStr.charAt(i);
                    parenthesis--;
                    
                    // Closed commnad
                    if (parenthesis == 0) {
                        index++;
                        result.put("$"+index, command);
                        command="";
                    }
                } 
                else if (Character.isLetter(commandStr.charAt(i)) && "".equals(command) && !insideString) {
                    command = command + commandStr.charAt(i);
                }
                else if ((Character.isLetterOrDigit(commandStr.charAt(i)) || ",".contains(commandStr.charAt(i)+"")) && !"".equals(command) && !insideString) {
                    command = command + commandStr.charAt(i);
                } 
                else if (commandStr.charAt(i) == '"') {
                    insideString = !insideString;
                    command = command + commandStr.charAt(i);
                } else if (insideString) {
                    command = command + commandStr.charAt(i);
                }
                
                if (" +-*/()".contains(commandStr.charAt(i)+"") & !"".equals(command) && !insideString) {
                    if (parenthesis == 0) {
                        command = "";
                    } else {
                        command = command + commandStr.charAt(i);
                    }
                }
            }
            
            // Process commands that were left over
            String mappedExpression = getMappedExpression(line, result);
            StringTokenizer st = new StringTokenizer(mappedExpression.trim(), "+-*/() ", true);
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                if (CompilerUtils.isCommand(token)) {
                    index++;
                    result.put("$"+index, token);
                }
            }         
        }
        
        return result;
    }
    
    public static String getMappedExpression(String expression, Map<String,String> commandMap) {
        String result = "";
        if (expression != null) {
            // Clean commands from the expression
            result = getCleanFunctionStr(expression);
            Iterator<String> i = commandMap.keySet().iterator();
            while (i.hasNext()) {
                String key = i.next();
                result = result.replace(commandMap.get(key), key);
            }
        }
        return result;
    }
    
    public static boolean isArray(String expression, HashSet<String> variables) {

        if (variables != null) {
            expression = getCleanFunctionStr(expression);
            String arrayParts[] = getInLineMapParts(expression);
            if (arrayParts != null && arrayParts.length == 2 && arrayParts[0] != null && arrayParts[1] != null) {
                return variables.contains(arrayParts[0]);
            }
        }
        return false;
    }
    
    public static String[] getInLineMapParts(String expression) {
          if (expression != null) {
            expression = getCleanFunctionStr(expression);
            if (expression.indexOf("[") > -1 && expression.lastIndexOf("]") > -1) {
                int start = expression.indexOf("[");
                int end = expression.lastIndexOf("]");
                String name = expression.substring(0, start);
                String index = expression.substring(start+1, end);
                
                return new String[] {name, index};
            } 
            
            if (expression.endsWith(".length")) {
                String name = expression.replaceAll(".length", "");
                String command = "length";
                
                return new String[] {name, command};
            }
        }      
        return null;
    }    
    
    
    public static boolean isCompilableLine(String line, HashSet<String> variables, HashSet<String> functionNames) {
        return isCommand(line) || isAttribution(line) || isIncrement(line, variables) || isFunction(line, functionNames);
    }
    
    
    public static boolean isFunction(String line,HashSet<String> functionNames) {
        if (line != null && functionNames != null) {
            String command = getCommandName(line);
            if (command != null) {
                return functionNames.contains(command.trim().toLowerCase());
            }
        }
        return false;
    }
    
    /**
     * Returns the name of the function
     * @param line
     * @return 
     */
    public static String getFunctionName(String line) {
        String result = "";
        
        if (line != null) {
            String command = line.toLowerCase().trim();
            for (int i=0; i < command.length(); i++) {
                if (" (".contains(command.charAt(i)+"")) {
                    break;
                } else {
                    result = result + command.charAt(i);
                }
            }
        }
        
        return result;
    }
    
   
}
