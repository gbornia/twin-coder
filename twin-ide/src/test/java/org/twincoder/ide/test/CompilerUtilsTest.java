/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.test;

import org.twincoder.ide.utils.CompilerUtils;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gbornia
 */
public class CompilerUtilsTest {
    
    private HashSet<String> variables;
    
    public CompilerUtilsTest() {
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
        variables.add("i");
        variables.add("array");
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void testIsNumber() {
      
        assertFalse("Null command", CompilerUtils.isNumber(null));
        assertFalse("Variable", CompilerUtils.isNumber("age"));
        assertFalse("String", CompilerUtils.isNumber("\"Hello World\""));
        assertFalse("Math Expression", CompilerUtils.isNumber("4+8"));
        assertTrue("Positive number", CompilerUtils.isNumber("5"));
        assertTrue("Negative number", CompilerUtils.isNumber("-5"));
        assertTrue("Float number", CompilerUtils.isNumber("3.1415926535"));
    }
    
    @Test
    public void testIsCommand() {
      
        assertFalse("Null command", CompilerUtils.isCommand(null));
        
        assertTrue("Simple command", CompilerUtils.isCommand("println(\"Hello World\")"));
        
        assertTrue("Simple command", CompilerUtils.isCommand("PrintLn (\"Hello World\")"));
        
        assertTrue("Simple command", CompilerUtils.isCommand("println(35)"));
        
        assertTrue("Multiple arguments command", CompilerUtils.isCommand("println(\"Is your age \", 35, \"?\""));
        
        assertTrue("Composite command",CompilerUtils.isCommand("println(\"Addition:\", age + 5, concat(\" \",\"years\"))"));
        
        assertFalse("Command without parenthesis", CompilerUtils.isCommand("println a"));
        
        assertFalse("Invalid command", CompilerUtils.isCommand("print,line(1)"));
        
        assertTrue("Inner command", CompilerUtils.isCommand("equal(round(a),2)"));
        
        assertTrue("Command withou parenthesis", CompilerUtils.isCommand("clear"));     
        
        assertTrue("Logical function", CompilerUtils.isCommand("isNumber(a)"));

        assertFalse("Logical condition", CompilerUtils.isCommand("isNumber(a) or isNumber(b()"));

        assertFalse("Math expression", CompilerUtils.isCommand("cos(a)*2"));

        assertFalse("Broke command parts", CompilerUtils.isCommand("pri(1)nt"));
        
        assertTrue("Simple Command", CompilerUtils.isCommand("keyPressed"));
        
        assertTrue("Not Command", CompilerUtils.isCommand("not(a=b)"));
        
        assertTrue("Not Command between parenthesis", CompilerUtils.isCommand("(not(a=b))"));
    }
    
    @Test
    public void testIsMathExpression() {
      
        HashSet<String> variables = new HashSet();
        variables.add("a");
        HashSet<String> functionNames = new HashSet();
        
        assertFalse("Null command", CompilerUtils.isMathExpression(null, variables, functionNames));
        
        assertTrue("Simple math", CompilerUtils.isMathExpression("1+1", variables, functionNames));
        
        assertTrue("Simple math with variables", CompilerUtils.isMathExpression("1+2*a", variables, functionNames));
         
        assertTrue("Simple math with parenthesis", CompilerUtils.isMathExpression("1+(2*(2-a))", variables, functionNames));

        assertTrue("Simple math with function", CompilerUtils.isMathExpression("1+(2*(2-cos(a)))", variables, functionNames));

        assertFalse("Command", CompilerUtils.isMathExpression("cos(10)", variables, functionNames));

        assertFalse("Number", CompilerUtils.isMathExpression("10", variables, functionNames));

        assertFalse("String", CompilerUtils.isMathExpression("\"Hellow\"", variables, functionNames));   
                
    }
    
    @Test
    public void testGetCommandName() {
 
        assertNull("Null command", CompilerUtils.getCommandName(null));
        
        assertEquals("Simple command", "println", CompilerUtils.getCommandName("println(\"Hello World\")"));
        
        assertEquals("Simple command", "println", CompilerUtils.getCommandName("PrintLn (\"Hello World\")"));
        
        assertEquals("Simple command", "println", CompilerUtils.getCommandName("println(35)"));
        
        assertEquals("Multiple arguments command", "println", CompilerUtils.getCommandName("println(\"Is your age \", 35, \"?\")"));
        
        assertEquals("Composite command", "println", CompilerUtils.getCommandName("println(\"Addition:\", age + 5, concat(\" \",\"years\"))"));
     
        assertEquals("Multiple inner parenthesis", 
            "compare", 
            CompilerUtils.getCommandName("compare(max(min(1,3),max(2,4), min(min(5,6),min(8,9))), 3)"));
        
        assertEquals("Command with spaces","cos",CompilerUtils.getCommandName(" cos(i)"));
    }
    
    @Test
    public void testGetCommandArgumentString() {
 
        assertNull("Null command", CompilerUtils.getCommandArgumentString(null));
        
        assertNull("Null argument",  CompilerUtils.getCommandArgumentString("println"));

        assertEquals("Simple argument", "\"Hello World\"", CompilerUtils.getCommandArgumentString("println(\"Hello World\")"));
        
        assertEquals("Simple argument", "\"Hello World\"", CompilerUtils.getCommandArgumentString("PrintLn (\"Hello World\")"));
        
        assertEquals("Simple argument", "35", CompilerUtils.getCommandArgumentString("println(35)"));
        
        assertEquals("Multiple arguments command", "\"Is your age \", 35, \"?\"", CompilerUtils.getCommandArgumentString("println(\"Is your age \", 35, \"?\")"));
        
        assertEquals("Composite command arguments", "\"Addition:\", age + 5, concat(\" \",\"years\")", CompilerUtils.getCommandArgumentString("println(\"Addition:\", age + 5, concat(\" \",\"years\"))"));
              
    }    
    
    @Test
    public void testGetCommandParameters() {
        
        assertArrayEquals("Null command", new String[] {}, CompilerUtils.getCommandParameters(null));
        
        assertArrayEquals("Null command", new String[] {"println"}, CompilerUtils.getCommandParameters("println"));
        
        assertArrayEquals("Simple parameters", 
            new String[] {"age", " 20"}, 
            CompilerUtils.getCommandParameters("age, 20"));
        
        assertArrayEquals("Simple string without comma", 
            new String[] {"\"Hello World\""}, 
            CompilerUtils.getCommandParameters("\"Hello World\""));
        
        
         assertArrayEquals("Simple string with comma", 
            new String[] {"\"Hello World, I'm alive\""}, 
            CompilerUtils.getCommandParameters("\"Hello World, I'm alive\""));
         
         assertArrayEquals("String parameters", 
            new String[] {"\"My age is \"", " 20", " \", and I have 2 pets, one is a dog.\""},  
            CompilerUtils.getCommandParameters("\"My age is \", 20, \", and I have 2 pets, one is a dog.\""));
         
        assertArrayEquals("Simple inner command", 
            new String[] {"compare(\"Hello World\", phrase)"}, 
            CompilerUtils.getCommandParameters("compare(\"Hello World\", phrase)"));
        
        assertArrayEquals("Multiple inner commands", 
            new String[] {"compare(\"Gabriel, Briane\", phrase)",
                           " 2 + 5",
                           " lower(\"max(1,2)\""}, 
            CompilerUtils.getCommandParameters("compare(\"Gabriel, Briane\", phrase), 2 + 5, lower(\"max(1,2)\""));

        assertArrayEquals("Multiple inner parenthesis", 
            new String[] {"compare(max(min(1,3),max(2,4), min(min(5,6),min(8,9))), 3)"}, 
            CompilerUtils.getCommandParameters("compare(max(min(1,3),max(2,4), min(min(5,6),min(8,9))), 3)"));
        
        assertArrayEquals("Multiple inner parenthesis, multiple command", 
            new String[] {"max(min(1,3),max(2,4), min(min(5,6),min(8,9)))",
                          " 3"}, 
            CompilerUtils.getCommandParameters("max(min(1,3),max(2,4), min(min(5,6),min(8,9))), 3"));
        
        assertArrayEquals("Multiple inner parenthesis, multiple command", 
            new String[] {"min(1,3)",
                          "max(2,4)",
                          " min(min(5,6),min(8,9))"}, 
            CompilerUtils.getCommandParameters("min(1,3),max(2,4), min(min(5,6),min(8,9))"));        
    }
    
    @Test
    public void testGetCommandArgumentStringForIfOrWhile() {
        assertEquals("If - Simple comparison", "(x>0)", CompilerUtils.getCommandArgumentString("if (x>0)"));
        assertEquals("If - Or comparison", "(x>0) or (y<10)", CompilerUtils.getCommandArgumentString("if (x>0) or (y<10)"));
        assertEquals("While - Simple comparison", "(x>0)", CompilerUtils.getCommandArgumentString("while (x>0)"));
        assertEquals("While - Or comparison", "(x>0) or (y<10)", CompilerUtils.getCommandArgumentString("while (x>0) or (y<10)"));

    }    
    @Test
    public void testIsComparison() {
        assertEquals("Simple comparison", true, CompilerUtils.isComparison("a==2"));
        
        assertEquals("Equation comparison", true, CompilerUtils.isComparison("a+2==2+b"));
        
        assertEquals("Greater comparison", true, CompilerUtils.isComparison("a+2>2+b"));
        
        assertEquals("Greater or equal comparison", true, CompilerUtils.isComparison("a+2>=2+b"));
        
        assertEquals("Lesser comparison", true, CompilerUtils.isComparison("a+2<2+b"));
        
        assertEquals("Lesser or equal comparison", true, CompilerUtils.isComparison("a+2<=2+b"));
        
        
        assertEquals("Not a comparison", false, CompilerUtils.isComparison("a+2-2+b"));
        
        /*
        assertEquals("Not a comparison", false, CompilerUtils.isComparison("a+2=>2+b"));
                
        assertEquals("Not a comparison", false, CompilerUtils.isComparison("=a+2-2+b"));
        
        assertEquals("Not a comparison", false, CompilerUtils.isComparison("a+2-2+b>"));
        */
        
        assertEquals("Comparison with or", true, CompilerUtils.isComparison("(a<10) or (a>20)"));
        
        assertEquals("Comparison of variables", true, CompilerUtils.isComparison("a or b"));

        assertEquals("Comparison of functions", true, CompilerUtils.isComparison("isNumber(a) or isNumber(b)"));
    }
    
    @Test
    public void getCommandMap() {
        assertEquals("Simple command", "round(10)", CompilerUtils.getCommandMap("round(10)").get("$1"));
        assertEquals("Math function without command", 0, CompilerUtils.getCommandMap("2+15").size());
        assertEquals("Math function with parenthesis and without command", 0, CompilerUtils.getCommandMap("2+(5+(10-5))").size());
        assertEquals("Math function with variables", 0, CompilerUtils.getCommandMap("2+(a+(10-year))").size());
        assertEquals("Math function with command", "round(10-year)", CompilerUtils.getCommandMap("2+(a+round(10-year))").get("$1"));
        assertEquals("Math function with multiple commands", "sin(30/2)", CompilerUtils.getCommandMap("2+(a+round(10-year)+(4*sin(30/2)))").get("$2"));  
        assertEquals("Math function with single function", "pi", CompilerUtils.getCommandMap("2 * pi * radius").get("$1"));  
        assertEquals("Function with Strfing", "formatDate(now, \"yyyy-mm-dd\")", CompilerUtils.getCommandMap("now - formatDate(now, \"yyyy-mm-dd\")").get("$1"));  
    }
    
    @Test
    public void testArrayParts() {
        assertArrayEquals(new String[] {"array","0"}, CompilerUtils.getInLineMapParts("array[0]"));
        assertArrayEquals(new String[] {"array","length"}, CompilerUtils.getInLineMapParts("array.length"));
        assertArrayEquals(new String[] {"array","inner[2]"}, CompilerUtils.getInLineMapParts("array[inner[2]]"));
        assertNull(CompilerUtils.getInLineMapParts("invalid"));
        assertNull(CompilerUtils.getInLineMapParts("invalid_length"));
    }
    
    @Test
    public void testIsArray() {
        assertTrue(CompilerUtils.isArray("array[0]", variables));
        assertTrue(CompilerUtils.isArray("array[inner[2]]", variables));
        assertTrue(CompilerUtils.isArray("array.length", variables));
        
        assertFalse(CompilerUtils.isArray("invalid[0]", variables));
        assertFalse(CompilerUtils.isArray("invalid.length", variables));
        assertFalse(CompilerUtils.isArray("array[0]", null));
        assertFalse(CompilerUtils.isArray(null, variables));
        assertFalse(CompilerUtils.isArray(null, null));
        assertFalse(CompilerUtils.isArray("println(array[0])", variables));
    }
    
    @Test
    public void testIsIncrement() {
        // Positive
        assertTrue(CompilerUtils.isIncrement("i++", variables));
        
        // Negative
        assertFalse(CompilerUtils.isIncrement("i", variables));
        assertFalse(CompilerUtils.isIncrement("a++", variables));
        assertFalse(CompilerUtils.isIncrement("++i", variables));
        assertFalse(CompilerUtils.isIncrement("i+++", variables));

    }
    
    @Test
    public void testFunctions() {
        assertEquals("calculate", CompilerUtils.getFunctionName("calculate(3,4,5)"));
        assertEquals("calculate", CompilerUtils.getFunctionName("calculate"));
        assertEquals("calculate", CompilerUtils.getFunctionName("calculate 2"));
        assertEquals("calculate", CompilerUtils.getFunctionName("   calculate 2"));
    }
    
    
}
