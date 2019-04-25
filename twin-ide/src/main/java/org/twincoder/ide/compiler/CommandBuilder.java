/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.utils.RegexUtils;
import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Command;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.Set;
import org.twincoder.ide.command.Variable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.twincoder.ide.command.Call;
import org.twincoder.ide.command.Increment;
import org.twincoder.ide.command.MapSet;
import org.twincoder.ide.command.StringLiteral;

/**
 *
 * @author gbornia
 */
public class CommandBuilder {

    private FunctionBuilder functionBuilder = null;
    private HashSet<String> functionNames;
    
    public CommandBuilder(HashSet<String> variables, HashSet<String> functionNames) {
        this.functionBuilder = new FunctionBuilder(this, variables, functionNames);
        this.functionNames = functionNames;
    }
    
    /**
     * Build a Command based on a string (line command)
     * @param commandString
     * @return
     * @throws CompilerException 
     */
    public Command buildCommand(String commandString) throws CompilerException {
        Command command = null;
        
        if (CompilerUtils.isCommand(commandString)) {
            String commandName = CompilerUtils.getCommandName(commandString);
            String argumentString = CompilerUtils.getCommandArgumentString(commandString);

            if (commandName != null) {
                try {
                    String commandClass = CompilerCommand.getClassForCommand(commandName);
                    if (commandClass != null) {
                        Class aClass = Class.forName(commandClass);
                        if (aClass != null) {
                            command = (Command) aClass.getConstructors()[0].newInstance();
                            if (argumentString != null) {
                                Function arguments[] = getArgumentsForCommand(argumentString);
                                command.setArguments(arguments);
                            }
                        } else {
                            throw new CompilerException("Command not found: " + commandName);
                        }
                    } else {
                        throw new CompilerException("Command not found: " + commandName);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e) {
                    throw new CompilerException("Invalid command, error building " + commandName);
                } catch (CompilerException e) {
                    throw e;
                }
            }
        } else {
            // Check for an attribution
           if (CompilerUtils.isAttribution(commandString)) {
               command = buildAttributionCommand(commandString);
           } else {
               if (CompilerUtils.isIncrement(commandString, functionBuilder.getVariables())) {
                   command = buildIncrement(commandString);
               }               
           }         
        }
        
        return command;
    }

    public Command buildFunctionCallCommand(String commandString) throws CompilerException {
        if (CompilerUtils.isFunction(commandString, functionNames)) {
             String functionName = CompilerUtils.getCommandName(commandString);
             String functionArgs[] = CompilerUtils.getCommandParameters(CompilerUtils.getCommandArgumentString(commandString));
             if (functionName != null && functionNames.contains(functionName)) {
                 // Build function call
                 Call functionCall = new Call();

                 // Build function call parameters
                 int argsSize = 1 + (functionArgs!=null?functionArgs.length:0);
                 Function[] functionCallArgs;
                 functionCallArgs = new Function[argsSize];
                 functionCallArgs[0] = new StringLiteral(functionName);
                 if (functionArgs!= null) {
                     for (int i=0; i < functionArgs.length; i++) {
                         try {
                             functionCallArgs[i+1] = functionBuilder.getFunction(functionArgs[i]);
                         } catch (FunctionBuildException ex) {
                             throw new CompilerException("Invalid function arguments (" + functionArgs[i] + ")");
                         }
                     }
                     functionCall.setArguments(functionCallArgs);
                 }

                 return functionCall;
             }
                           
        }
        return null;   
    }
    
    /**
     * Returns the content of the arguments for a given command
     * @param command
     * @param line
     * @return 
     */
    private Function[] getArgumentsForCommand(String paramStr) throws CompilerException {
        ArrayList<Function> arguments = new ArrayList<>();
        
        if (paramStr != null) {
            String[] stringArgs = CompilerUtils.getCommandParameters(paramStr);
            for (String stringArg : stringArgs) {
                String arg = stringArg.trim();
                
                Function function;
                try {
                    function = functionBuilder.getFunction(arg);
                } catch (FunctionBuildException e) {
                    throw new CompilerException("Error building arguments: " + e.getMessage());
                }
                
                if (function != null) {
                    arguments.add(function);
                } else {
                    throw new CompilerException("Argument " + stringArg + " was not recognized, check variables or expression");
                }

            }
        }
               
        return (Function[]) arguments.toArray(new Function[1]);
    }
    
    
    /**
     * Build a command attribution (Set) based on the syntax <variable>=<function>
     * for example a=2+5
     * @param line
     * @return
     * @throws CompilerException 
     */    
    public Command buildAttributionCommand(String attributionStr) throws CompilerException {

        if (attributionStr != null) {
            try {
                String parts[] = attributionStr.split(RegexUtils.REGEX_ATTRIBUTION);
                if (parts != null && parts.length == 2 && parts[0]!=null && parts[1]!=null) {
                    if (CompilerUtils.isArray(parts[0], functionBuilder.getVariables())) {
                        String arrayParts[] = CompilerUtils.getInLineMapParts(parts[0]);
                        if (arrayParts != null) {
                            //ArraySet result = new ArraySet();
                            MapSet result = new MapSet();
                            result.setArguments( new Function[] {
                                functionBuilder.getFunction(arrayParts[0]),
                                functionBuilder.getFunction(arrayParts[1]),
                                functionBuilder.getFunction(parts[1])
                            });
                            return result;
                        }
                    } else {
                            Function functionA = functionBuilder.getFunction(parts[0]);
                            Function functionB = functionBuilder.getFunction(parts[1]);
                            if (functionA!=null && functionB!=null) {
                                if (functionA instanceof Variable) {
                                Set result = new Set();
                                    result.setArguments(new Function[] {
                                        functionA,
                                        functionB
                                    });
                                    return result; 
                                } else {
                                    throw new CompilerException("Cannot set value to a non-variable");
                                }
                            } else {
                                throw new CompilerException("Malformed attribution command");
                            }
                    }
                } else {
                    throw new CompilerException("Malformed attribution command");
                }
            } catch (FunctionBuildException e) {
                throw new CompilerException("Problem building functions: " + e.getMessage());
            }

        }       
        return null;
    }
    
    
    public Increment buildIncrement(String incrementStr) {
        Increment result = null;
        if (incrementStr != null && CompilerUtils.isIncrement(incrementStr, functionBuilder.getVariables())) {
            result = new Increment();
            result.setArguments(new Function[] { new Variable(incrementStr.replaceAll("\\+\\+", "")) });
        }
        return result;
    }

}
