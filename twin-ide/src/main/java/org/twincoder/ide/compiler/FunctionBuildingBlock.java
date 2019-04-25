/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.compiler;

import org.twincoder.ide.command.Function;

/**
 * Implements a chain of responsibility to build Functions
 * @author gbornia
 */
public abstract class FunctionBuildingBlock {
    private FunctionBuildingBlock next;
    private final FunctionBuilder builder;
    
    public FunctionBuildingBlock(FunctionBuilder builder, FunctionBuildingBlock next) {
        this.builder = builder;
        this.next = next;
    }
    
    /**
     * Builds the function if possible, otherwise calls the nest building block
     * @param functionStr
     * @return 
     * @throws org.twincoder.ide.compiler.FunctionBuildException 
     */
    public Function getFunction(String functionStr) throws FunctionBuildException {
        Function result = buildFunction(functionStr);
        
        if (result == null && getNext() != null) {
            result = getNext().getFunction(functionStr);
        }
        
        return result;
    }
    
    /**
     * Builds a function from a string (if possible)
     * @param functionStr
     * @return 
     * @throws org.twincoder.ide.compiler.FunctionBuildException 
     */
    public abstract Function buildFunction(String functionStr) throws FunctionBuildException;

    /**
     * @return the next
     */
    public FunctionBuildingBlock getNext() {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(FunctionBuildingBlock next) {
        this.next = next;
    }

    /**
     * @return the builder
     */
    public FunctionBuilder getBuilder() {
        return builder;
    }
}
