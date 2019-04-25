/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.test;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.twincoder.ide.command.And;
import org.twincoder.ide.command.Equal;
import org.twincoder.ide.command.Function;
import org.twincoder.ide.command.Greater;
import org.twincoder.ide.command.Lesser;
import org.twincoder.ide.command.Or;
import org.twincoder.ide.command.StringLiteral;
import org.twincoder.ide.command.Variable;
import org.twincoder.ide.compiler.CommandBuilder;
import org.twincoder.ide.compiler.FunctionBuildComparison;
import org.twincoder.ide.compiler.FunctionBuildException;
import org.twincoder.ide.compiler.FunctionBuilder;

/**
 *
 * @author gbornia
 */
public class FunctionBuildComparisonTest {
    
    FunctionBuilder builder;
    HashSet<String> variables;
    HashSet<String> functions;
    
    public FunctionBuildComparisonTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        variables = new HashSet();
        variables.add("a");
        variables.add("b");

        functions = new HashSet();
        
        CommandBuilder commandBuilder = new CommandBuilder(variables, functions);
        builder = new FunctionBuilder(commandBuilder, variables, functions); 
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void buildSimpleComparison() {        
        try {
            Function f = builder.getFunction("10>5");
            assertTrue(f instanceof Greater);
            assertTrue(((Greater)f).getArguments()[0] instanceof StringLiteral);
            assertTrue(((Greater)f).getArguments()[1] instanceof StringLiteral);
        } catch (FunctionBuildException ex) {
            Logger.getLogger(FunctionBuildComparisonTest.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    
    @Test
    public void buildSimpleComparisonWithVariables() {        
        try {
            Function f = builder.getFunction("a<500");
            assertTrue(f instanceof Lesser);
            assertTrue(((Lesser)f).getArguments()[0] instanceof Variable);
            assertTrue(((Lesser)f).getArguments()[1] instanceof StringLiteral);
        } catch (FunctionBuildException ex) {
            Logger.getLogger(FunctionBuildComparisonTest.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    @Test
    public void buildSimpleOrComparison() {        
        try {
            
            Function f = builder.getFunction("(a<500) or (b>10)");
            assertTrue(f instanceof Or);
            assertTrue(((Or)f).getArguments()[0] instanceof Lesser);
            assertTrue(((Or)f).getArguments()[1] instanceof Greater);
        } catch (FunctionBuildException ex) {
            Logger.getLogger(FunctionBuildComparisonTest.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }  
    
    @Test
    public void buildSimpleAndComparison() {        
        try {
            
            Function f = builder.getFunction("(a<500) and (b>10)");
            assertTrue(f instanceof And);
            assertTrue(((And)f).getArguments()[0] instanceof Lesser);
            assertTrue(((And)f).getArguments()[1] instanceof Greater);
        } catch (FunctionBuildException ex) {
            Logger.getLogger(FunctionBuildComparisonTest.class.getName()).log(Level.SEVERE, null, ex);
        }    
    } 
    
    @Test
    public void buildComplexComparisons() {        
        try {
            
            Function f = builder.getFunction("((a<500) and (b>10)) or (b==2)");
            assertTrue(f instanceof Or);
            assertTrue(((Or)f).getArguments()[0] instanceof And);
            assertTrue(((Or)f).getArguments()[1] instanceof Equal);
        } catch (FunctionBuildException ex) {
            Logger.getLogger(FunctionBuildComparisonTest.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
}
