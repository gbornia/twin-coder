/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Command;
import org.twincoder.ide.command.CommandArgumentException;
import org.twincoder.ide.command.CompositeCommand;
import org.twincoder.ide.command.If;
import org.twincoder.ide.program.Program;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Stack;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.FunctionBlock;

/**
 *
 * @author Gabriel
 */
public class Compiler extends Observable {
    

        
    private final ArrayList<CompilerMessage> messages;

    private HashSet<String> variables = null;
    private HashSet<String> functionNames = null;
    private CommandBuilder commandBuilder = null;
    private FunctionBuilder builder = null;
    private int errorCount = 0;
    
    public Compiler() {
        messages = new ArrayList<>();
        variables = new HashSet<>();
        functionNames = new HashSet<>();
        commandBuilder = new CommandBuilder(variables, functionNames);  
    }

    public Program compileProgram(String name, String source) {
        messages.clear();
        variables.clear();
        
        String[] sourceLines = null;
        source = CompilerUtils.clearCodeComments(source);
        if (source != null) {
            sourceLines = source.split("\n");
        }
        return compileProgram(name, sourceLines);
    }
    
    private void preCompileProgram(String[] code) {
        if (code != null) {
            for (int i=0; i < code.length; i++) {

                if (code[i] != null) {
                    String line = code[i].trim();
                    
                    if (line.toLowerCase().startsWith("function ")) {
                        String functionLine = line.toLowerCase().replaceAll("function ","");
                        String functionName = CompilerUtils.getCommandName(functionLine);
                        functionNames.add(functionName);
                    }
                    
                }
            }
        }        
    }
    
    public Program compileProgram(String name, String[] code) {
        Program program = null;
        HashMap<String, FunctionBlock> functions = new HashMap();
        
        messages.clear();
        errorCount = 0;
        
        Stack commandStack = new Stack();
        
        if (code != null) {
            // Pre-compile for function names and other attributes
            preCompileProgram(code);
            
            // Compile code
            for (int i=0; i < code.length; i++) {

                if (code[i] != null) {
                    String line = code[i].trim();

                    if ("".equals(line)) {
                        continue;
                    }
                    
                    // Start of variables control
                    if ("variables".equalsIgnoreCase(line) && commandStack.isEmpty()) {
                        commandStack.add("Variables");
                        continue;
                    }                    
                    
                    // Start main program
                    if ("begin".equalsIgnoreCase(line) && (commandStack.isEmpty() || "Variables".equals(commandStack.peek()))) {
                        if (!commandStack.isEmpty()) {
                            commandStack.pop();
                         }
                        program = new Program(name);
                        commandStack.add(program);
                        continue;
                    }

                    // Start main program
                    if (line.toLowerCase().startsWith("function ") && (commandStack.isEmpty())) {
                        String functionLine = line.toLowerCase().replaceAll("function ","");
                        String functionName = CompilerUtils.getCommandName(functionLine);
                        String functionArgs[] = CompilerUtils.getCommandParameters(CompilerUtils.getCommandArgumentString(functionLine));
                         if (functions.get(functionName)==null) {
                            FunctionBlock function = new FunctionBlock(functionName, functionArgs);
                            functions.put(functionName, function);
                            functionNames.add(functionName);
                            commandStack.add(function);
                            
                            if (functionArgs != null) {
                                for (int a=0; a < functionArgs.length; a++) {
                                    variables.add(functionArgs[a].toLowerCase());
                                }
                            }
                        } else {
                            logMessage(CompilerMessage.ERROR, i, "Program", "Function " + functionName + " was already defined");
                        }
                        continue;
                    }

                    
                    // End composite blocks
                    if ("end".equalsIgnoreCase(line) && !commandStack.isEmpty() && (commandStack.peek() instanceof CompositeCommand)) {
                        commandStack.pop();
                        continue;
                    }

                    // Process else (If special case)
                    if ("else".equalsIgnoreCase(line) && !commandStack.isEmpty() && (commandStack.peek() instanceof If)) {                       
                        // Switch block to Else
                        ((If)commandStack.peek()).setCurrentBlock(If.ELSE_BLOCK);
                        continue;
                    }
                    
                    // Process variables
                    if (!commandStack.isEmpty() && "Variables".equals(commandStack.peek())) {
                        String vars[] = line.split(" |,");
                        if (vars != null) {
                            for (int v=0; v < vars.length; v++) {
                                String varName = vars[v].trim().toLowerCase();
                                if (!"".equals(varName)) {
                                    if (!variables.contains(varName)) {
                                        variables.add(varName);
                                    } else {
                                        logMessage(CompilerMessage.ERROR, i, "Variables", "Variable " + varName + " was already defined");
                                    }
                                }
                            }
                        }
                        continue;
                    }
                    
                    // Process commands and add it to the composite current block
                    if (!commandStack.isEmpty() && (commandStack.peek() instanceof CompositeCommand)) {
                        
                        CompositeCommand cm = (CompositeCommand)commandStack.peek(); 
                        
                        // Check for a command
                        if (CompilerUtils.isCompilableLine(line, variables, functionNames)) {
                             try {                                
                                Command command = commandBuilder.buildCommand(line);
                                if (command != null) {
                                    try {
                                        command.validateArguments();

                                        cm.addCommand(command);                                      
                                        if (command instanceof CompositeCommand) {
                                            commandStack.add(command);
                                        }        
                                    } catch (CommandArgumentException c) {
                                        logMessage(CompilerMessage.ERROR, i, "Program", "Invalid argument: " + c.getMessage());
                                    }
                                } else {
                                    // Error loading command
                                    logMessage(CompilerMessage.ERROR, i, "Program", "Not able to create command [" + line + "]");
                                }
                             } catch (CompilerException e) {
                                 logMessage(CompilerMessage.ERROR, i, "Program", "Error creating command [" + line + "]: " + e.getMessage());
                             }
                             continue;
                        } else {
                            // Check for command spelling
                            String commandName = CompilerUtils.getCommandName(line);
                            if (commandName != null 
                                    && !"".equals(commandName) 
                                    && Character.isAlphabetic(commandName.charAt(0)) 
                                    && !CompilerUtils.isValidCommand(commandName)) {                                    
                                logMessage(CompilerMessage.ERROR, i, "Program", "Invalid command name [" + commandName + "], check spelling"); 
                                continue;
                            }                           
                        }
                    }
                    
                    // Line with error
                    logMessage(CompilerMessage.ERROR, i, "Main Program", "Line '" + line + "' was not processed.");
                }
            }
            
            if (!commandStack.empty()) {
                logMessage(CompilerMessage.ERROR, code.length-1, "Main Program", "Program not ended well.");
            }
        }

        if (program != null) {
            
            // No error messages, add an information message
            if (getMessages()!=null) {
                if (getMessages().isEmpty()) {
                    logMessage(CompilerMessage.INFORMATION, code!=null?code.length-1:0, "Main Program", "Program has been successfully compiled.");
                } else {
                    logMessage(CompilerMessage.WARNING, code!=null?code.length-1:0, "Main Program", "Program has been compiled with problems or warnings.");
                }
            }
            
            // Register functions if any
            if (functions.size() > 0) {
                Iterator<String> fn = functions.keySet().iterator();
                while (fn.hasNext()) {
                    String functionName = fn.next();                    
                    program.registerFunction(functionName, functions.get(functionName));
                    logMessage(CompilerMessage.INFORMATION, code!=null?code.length-1:0, "Main Program", "Function " + functionName + " has been successfully registered for the Program.");
                    
                }
            }
            
            // Mark error status
            program.setCompilerError(getErrorCount()>0);
        } else {
            logMessage(CompilerMessage.ERROR, 0, "Main Program", "Programs should have 'begin' and 'end'.");
        }
  
        setChanged();
        notifyObservers();
        
        return program;
    }

    /**
     * @return the messages
     */
    public ArrayList<CompilerMessage> getMessages() {
        return messages;
    }
    
    
    private void logMessage(int errorCode, int line, String group, String message) {
        getMessages().add(new CompilerMessage(errorCode, line, group, message));
        
        if (errorCode == CompilerMessage.ERROR) {
            errorCount++;
        }
    }
    
    public int getErrorCount() {
        return errorCount;
    }
}
