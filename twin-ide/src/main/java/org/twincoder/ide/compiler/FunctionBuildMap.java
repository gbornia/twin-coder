/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;


import org.twincoder.ide.command.Command;
import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.MapLength;
import org.twincoder.ide.command.MapValue;
import org.twincoder.ide.command.Variable;

/**
 *
 * @author gbornia
 */
public class FunctionBuildMap extends FunctionBuildingBlock {

    public FunctionBuildMap(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) throws FunctionBuildException {
        if (functionStr != null && getBuilder() != null && getBuilder().getVariables() != null) {
            if (CompilerUtils.isArray(functionStr, getBuilder().getVariables())) {
                String inlineMapParts[] = CompilerUtils.getInLineMapParts(functionStr);
                if (inlineMapParts != null && inlineMapParts.length == 2 && inlineMapParts[0]!= null & inlineMapParts[1] != null) {
                    if ("length".equals(inlineMapParts[1])) {
                        Command mapLength = new MapLength();
                        mapLength.setArguments(new Function[] {new Variable(inlineMapParts[0])});
                        return (Function) mapLength;
                    } else {
                        Command mapValue = new MapValue();
                        mapValue.setArguments(new Function[] {new Variable(inlineMapParts[0]), getBuilder().getFunction(inlineMapParts[1])});
                        return (Function) mapValue;
                    }
                }
            }
        }
        return null;
    }
    
}   
