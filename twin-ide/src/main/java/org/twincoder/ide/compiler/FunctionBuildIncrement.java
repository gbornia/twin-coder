/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.Increment;
import org.twincoder.ide.command.Variable;

/**
 *
 * @author gbornia
 */
public class FunctionBuildIncrement extends FunctionBuildingBlock {

    public FunctionBuildIncrement(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) {
        if (CompilerUtils.isIncrement(functionStr, getBuilder().getVariables())) {
            Increment increment = new Increment();
            increment.setArguments(new Function[] { new Variable(functionStr.replaceAll("\\+\\+", "")) });
            return increment;
        }
        return null;
    }
    
}   
