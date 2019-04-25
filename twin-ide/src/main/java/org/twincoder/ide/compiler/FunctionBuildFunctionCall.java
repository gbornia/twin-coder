/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.twincoder.ide.command.And;
import org.twincoder.ide.command.Call;
import org.twincoder.ide.command.Command;
import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Comparison;
import org.twincoder.ide.command.Different;
import org.twincoder.ide.command.Equal;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.FunctionBlock;
import org.twincoder.ide.command.Greater;
import org.twincoder.ide.command.GreaterOrEqual;
import org.twincoder.ide.command.Lesser;
import org.twincoder.ide.command.LesserOrEqual;
import org.twincoder.ide.command.Or;
import org.twincoder.ide.command.StringLiteral;
import org.twincoder.ide.command.Variable;

/**
 *
 * @author gbornia
 */
public class FunctionBuildFunctionCall extends FunctionBuildingBlock {

    public FunctionBuildFunctionCall(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) throws FunctionBuildException {
        String functionName = CompilerUtils.getCommandName(functionStr);
      
        if (CompilerUtils.isFunction(functionName, getBuilder().getFunctionNames())) {
             try {
                Command aCommand = getBuilder().getCommandBuilder().buildFunctionCallCommand(functionStr);
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
