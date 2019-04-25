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
import org.twincoder.ide.utils.CompilerUtils;
import org.twincoder.ide.command.Comparison;
import org.twincoder.ide.command.Different;
import org.twincoder.ide.command.Equal;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.Greater;
import org.twincoder.ide.command.GreaterOrEqual;
import org.twincoder.ide.command.Lesser;
import org.twincoder.ide.command.LesserOrEqual;
import org.twincoder.ide.command.Or;
import org.twincoder.ide.command.Variable;

/**
 *
 * @author gbornia
 */
public class FunctionBuildComparison extends FunctionBuildingBlock {

    public FunctionBuildComparison(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String functionStr) throws FunctionBuildException {
        if (CompilerUtils.isComparison(functionStr) && !CompilerUtils.isCommand(functionStr)) {
            return getComparisonCommand(functionStr);       
        }
        return null;
    }
    
    
    private Function getComparisonCommand(String aString) throws FunctionBuildException {
        Function result = null;
        
        try {
            Stack<Function> values = new Stack<>();
            Stack<String> operands = new Stack<>();
            HashSet<String> variables = getBuilder().getVariables();

            String functionStr = aString.trim().toLowerCase();

            // Remove mult-char comparison by simple char symbols
            functionStr = functionStr.replaceAll("\\b(or)\\b", "|");
            functionStr = functionStr.replaceAll("\\b(and)\\b", "&");
            functionStr = functionStr.replaceAll(">=", "}");
            functionStr = functionStr.replaceAll("<=", "{");
            functionStr = functionStr.replaceAll("==", ":");
            functionStr = functionStr.replaceAll("!=", "!");
            
            // Clean commands from the expression
            Map<String,String> commandMap = CompilerUtils.getCommandMap(functionStr);
            Iterator<String> i = commandMap.keySet().iterator();
            while (i.hasNext()) {
                String key = i.next();
                functionStr = functionStr.replace(commandMap.get(key), key);
            }

            // Process function string
            StringTokenizer st = new StringTokenizer(functionStr.trim(), "|&<>{}:!()", true);
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();

                if (!"".equals(token)) {
                    if (CompilerUtils.isNumber(token) || CompilerUtils.isString(token)) {
                        values.push(getBuilder().getFunction(token));
                    } else {
                        if (variables.contains(token.toLowerCase())) {
                            values.push(new Variable(token));              
                        } else {
                            if (commandMap.get(token) != null) {
                                try {
                                    Function aFunction = getBuilder().getFunction(commandMap.get(token));
                                    if (aFunction != null) {
                                        values.push(aFunction);
                                    }
                                } catch (FunctionBuildException ex) {
                                    Logger.getLogger(FunctionBuildComparison.class.getName()).log(Level.SEVERE, null, ex);
                                    throw ex;
                                }
                            } else {
                                if (CompilerUtils.isArray(token, getBuilder().getVariables())) {
                                    try {
                                        Function aFunction = getBuilder().getFunction(token);
                                        if (aFunction != null) {
                                            values.push(aFunction);
                                        }
                                    } catch (FunctionBuildException ex) {
                                        Logger.getLogger(FunctionBuildComparison.class.getName()).log(Level.SEVERE, null, ex);
                                        throw ex;
                                    }                                    
                                } else {
                                    if ("|&<>{}:!(".contains(token)) {                              
                                        if ("|&<>{}:!".contains(token)) {
                                            // Precedence treatment:
                                            // While the top of the stack operand has a bigger precedence, calculate it
                                            while (!operands.empty() && hasPrecedence(token, operands.peek())) {
                                                values.push(buildComparison(operands.pop(), values.pop(), values.pop()));
                                            }
                                        }
                                        // Add current operand
                                        operands.push(token);
                                    } else {
                                        if (")".contains(token)) {
                                            while (!"(".equals(operands.peek())) {
                                                values.push(buildComparison(operands.pop(), values.pop(), values.pop()));
                                            }  
                                            operands.pop();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }    
                
            }

            // Process until it ends
            while (!operands.isEmpty()) {
                values.push(buildComparison(operands.pop(), values.pop(), values.pop()));
            }

            // Last value is the result (it should be size() == 1)
            if (values.size() > 0) {
                result = values.pop();               
            }
        } catch (EmptyStackException e) {
            throw new FunctionBuildException("Error processing comparison [" + aString + "], please check the correctness, including parenthesis");
        }
        

        return result;

    }

    /**
     * Returns true is op2 has bigger precedence than op1
     */
    private boolean hasPrecedence(String op1, String op2) {
        if ("(".equals(op2) || ")".equals(op2))
            return false;
        
        return !(("&".equals(op1)) && ("|".equals(op2)));     
    }
    
    
    private Function buildComparison(String comparisonStr, Function partB, Function partA) throws FunctionBuildException {
        if (comparisonStr != null && partA != null && partB != null) {
            Comparison result = null;
            switch ("&|><}{:!".indexOf(comparisonStr)) {
                case 0: result = new And();
                        break;
                case 1: result = new Or();
                        break;
                case 2: result = new Greater();
                        break;            
                case 3: result = new Lesser();
                        break;                    
                case 4: result = new GreaterOrEqual();
                        break;                    
                case 5: result = new LesserOrEqual();
                        break;                    
                case 6: result = new Equal();
                        break;    
                case 7: result = new Different();
                        break;    
            }

            if (result != null) {
                result.setArguments(new Function[] {partA, partB});
            }
            return (Function)result;
        } else {
            throw new FunctionBuildException( "Malformed comparison command");
        }      
    }
}   
