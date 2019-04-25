/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.utils.CompilerUtils;
import java.util.EmptyStackException;
import org.twincoder.ide.command.Add;
import org.twincoder.ide.command.Command;
import org.twincoder.ide.command.Divide;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.Multiply;
import org.twincoder.ide.command.StringLiteral;
import org.twincoder.ide.command.Subtract;
import org.twincoder.ide.command.Variable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gbornia
 */
public class FunctionBuildMathExpression extends FunctionBuildingBlock {

    public FunctionBuildMathExpression(FunctionBuilder builder, FunctionBuildingBlock next) {
        super(builder, next);
    }


    @Override
    public Function buildFunction(String aString) throws FunctionBuildException {

        Function result = null;
        
        if (aString != null && getBuilder() != null &&
            CompilerUtils.isMathExpression(aString, getBuilder().getVariables(), getBuilder().getFunctionNames()) && 
            !CompilerUtils.isCommand(aString)) {
            try {
                Stack<Function> values = new Stack<>();
                Stack<String> operands = new Stack<>();
                HashSet<String> variables = getBuilder().getVariables();

                // Clean commands from the expression
                String functionStr = aString.trim();
                Map<String,String> commandMap = CompilerUtils.getCommandMap(functionStr);
                Iterator<String> i = commandMap.keySet().iterator();
                while (i.hasNext()) {
                    String key = i.next();
                    functionStr = functionStr.replace(commandMap.get(key), key);
                }

                // Process function string
                StringTokenizer st = new StringTokenizer(functionStr.trim(), "+-*/()", true);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken().trim();

                    if (!"".equals(token)) {
                        if (CompilerUtils.isNumber(token)) {
                            values.push(new StringLiteral(token));
                        } else {
                            if ( variables.contains(token.toLowerCase())) {
                                values.push(new Variable(token));              
                            } else {
                                if (commandMap.get(token) != null) {
                                    try {
                                        Function aFunction = getBuilder().getFunction(commandMap.get(token));
                                        if (aFunction != null) {
                                            values.push(aFunction);
                                        }
                                    } catch (FunctionBuildException ex) {
                                        Logger.getLogger(FunctionBuildMathExpression.class.getName()).log(Level.SEVERE, null, ex);
                                        throw ex;
                                    }
                                } else {
                                    if ("+-*/(".contains(token)) {                              
                                        if ("+-*/".contains(token)) {
                                            // Precedence treatment:
                                            // While the top of the stack operand has a bigger precedence, calculate it
                                            while (!operands.empty() && hasPrecedence(token, operands.peek())) {
                                                values.push(buildFunction(operands.pop(), values.pop(), values.pop()));
                                            }
                                        }
                                        // Add current operand
                                        operands.push(token);
                                    } else {
                                        if (")".contains(token)) {
                                            while (!"(".equals(operands.peek())) {
                                                values.push(buildFunction(operands.pop(), values.pop(), values.pop()));
                                            }  
                                            operands.pop();
                                        }
                                    }
                                }
                            }
                        }    
                    }
                }

                // Process until it ends
                while (!operands.isEmpty()) {
                    values.push(buildFunction(operands.pop(), values.pop(), values.pop()));
                }

                // Last value is the result (it should be size() == 1)
                if (values.size() > 0) {
                    result = values.pop();               
                }
            } catch (EmptyStackException e) {
                throw new FunctionBuildException("Error processing formula [" + aString + "], please check the correctness, including parenthesis");
            }
        }

        return result;
    }    

    
    /**
     * Returns true is op2 has bigger precedence than op1
     */
    private boolean hasPrecedence(String op1, String op2) {
        if ("(".equals(op2) || ")".equals(op2))
            return false;
        
        return !(("*".equals(op1) || "/".equals(op1)) && ("+".equals(op2) || "-".equals(op2)));     
    }
    
    private Function buildFunction(String operand, Function op1, Function op2) {
        Command operation = null;
       
        if (operand != null) {
            switch ("+-*/".indexOf(operand)) {
                case 0:  operation = new Add();
                         break;
                case 1:  operation = new Subtract();
                         break;         
                case 2:  operation = new Multiply();
                         break;                 
                case 3:  operation = new Divide();
                         break;   
            }

            if (operation != null) {
                operation.setArguments(
                        new Function[] {
                            null,
                            op2,
                            op1
                        }
                );
                
                return (Function) operation;
            }   
        }
        return null;
    }    
}   
