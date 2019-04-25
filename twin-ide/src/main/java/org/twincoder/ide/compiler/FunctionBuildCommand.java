/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Command;
import org.twincoder.ide.command.Function;

/**
 *
 * @author gbornia
 */
public class FunctionBuildCommand extends FunctionBuildingBlock {

    public FunctionBuildCommand(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) throws FunctionBuildException {
        if (CompilerUtils.isCommand(functionStr)) {
            try {
                Command aCommand = getBuilder().getCommandBuilder().buildCommand(functionStr);
                if (aCommand != null && aCommand instanceof Function) {
                    return (Function) aCommand;
                }
            } catch (CompilerException ex) {
                throw new FunctionBuildException(ex.getMessage());
            }
        }
        return null;
    }
    
}   
