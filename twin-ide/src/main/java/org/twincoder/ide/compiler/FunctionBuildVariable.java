/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.Variable;

/**
 *
 * @author gbornia
 */
public class FunctionBuildVariable extends FunctionBuildingBlock {

    public FunctionBuildVariable(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) {
        
        if (functionStr != null && getBuilder() != null && getBuilder().getVariables() != null) {
            if (getBuilder().getVariables().contains(functionStr.trim().toLowerCase())) {
                return (new Variable(functionStr.trim().toLowerCase()));
            }
        }
        return null;
    }

}   
