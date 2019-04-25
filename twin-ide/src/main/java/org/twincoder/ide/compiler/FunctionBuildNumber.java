/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.StringLiteral;

/**
 *
 * @author gbornia
 */
public class FunctionBuildNumber extends FunctionBuildingBlock {

    public FunctionBuildNumber(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) {
        if (CompilerUtils.isNumber(functionStr)) {
            return (new StringLiteral(functionStr.trim()));
        }
        return null;
    }
    
}   
