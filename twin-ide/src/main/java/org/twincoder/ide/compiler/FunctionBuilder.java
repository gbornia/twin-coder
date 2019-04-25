/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.command.Function;
import java.util.HashSet;

/**
 *
 * @author gbornia
 */
public class FunctionBuilder {
    private HashSet<String> variables = null;
    private HashSet<String> functionNames = null;
    private CommandBuilder commandBuilder = null;
    private FunctionBuildingBlock buildingBlock = null;

    public FunctionBuilder(CommandBuilder commandBuilder, HashSet<String> variables, HashSet<String> functionNames) {
        this.variables = variables;
        this.functionNames = functionNames;
        this.commandBuilder = commandBuilder;
        
        // Build chain of responsibility
        FunctionBuildingBlock buildFunctionCall = new FunctionBuildFunctionCall(this, null);
        FunctionBuildingBlock buildCommand = new FunctionBuildCommand(this, buildFunctionCall);
        FunctionBuildingBlock buildAttribution = new FunctionBuildAttribution(this, buildCommand);
        FunctionBuildingBlock buildIncrement = new FunctionBuildIncrement(this, buildAttribution);
        FunctionBuildingBlock buildMap = new FunctionBuildMap(this, buildIncrement);
        FunctionBuildingBlock buildComparison = new FunctionBuildComparison(this, buildMap);
        FunctionBuildingBlock buildMathExpression = new FunctionBuildMathExpression(this, buildComparison);
        FunctionBuildingBlock buildVariable = new FunctionBuildVariable(this, buildMathExpression);
        FunctionBuildingBlock buildNumber = new FunctionBuildNumber(this, buildVariable);
        FunctionBuildingBlock buildString = new FunctionBuildString(this, buildNumber);               
        this.buildingBlock = buildString;
    }
    
    public Function getFunction(String functionStr) throws FunctionBuildException {
        return buildingBlock.getFunction(functionStr);
    }
    
    /**
     * @return the variables
     */
    public HashSet<String> getVariables() {
        return variables;
    }
    
    /**
     * @return the variables
     */
    public HashSet<String> getFunctionNames() {
        return functionNames;
    }    

    /**
     * @param variables the variables to set
     */
    public void setVariables(HashSet<String> variables) {
        this.variables = variables;
    }

    /**
     * @return the commandBuilder
     */
    public CommandBuilder getCommandBuilder() {
        return commandBuilder;
    }
}
