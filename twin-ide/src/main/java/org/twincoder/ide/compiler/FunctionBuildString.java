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
public class FunctionBuildString extends FunctionBuildingBlock {

    public FunctionBuildString(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) {
        if (CompilerUtils.isString(functionStr)) {
            return (new StringLiteral(removeQuotes(functionStr)));
        }
        return null;
    }
    
    private String removeQuotes(String string) {
        if (string != null) {
            string = string.trim();
            if (string.startsWith("\"")) {
                string = string.substring(1);
            }

            if (string.endsWith("\"")) {
                string = string.substring(0, string.length()-1);
            }
        }
        return string;
    }
}   
