/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.test;

import org.twincoder.ide.utils.RegexUtils;
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
public class RegexUtilsTest {
    
    public RegexUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /*
    Test REGEX epression to split parameters
    */
    @Test
    public void testSplitParameters() {
      
        assertArrayEquals("Simple parameters", 
            new String[] {"age", " 20"}, 
            "age, 20".split(RegexUtils.REGEX_SPLIT_PARAMETERS));
        
        assertArrayEquals("Simple string without comma", 
            new String[] {"\"Hello World\""}, 
            "\"Hello World\"".split(RegexUtils.REGEX_SPLIT_PARAMETERS));
        
        
         assertArrayEquals("Simple string with comma", 
            new String[] {"\"Hello World, I'm alive\""}, 
            "\"Hello World, I'm alive\"".split(RegexUtils.REGEX_SPLIT_PARAMETERS));
         
         assertArrayEquals("String parameters", 
            new String[] {"\"My age is \"", " 20", " \", and I have 2 pets, one is a dog.\""},  
            "\"My age is \", 20, \", and I have 2 pets, one is a dog.\"".split(RegexUtils.REGEX_SPLIT_PARAMETERS));
    }    
    
    @Test
    public void testAttribution() {
      
        assertArrayEquals("Simple attribiution", 
            new String[] {"a", "2"}, 
            "a=2".split(RegexUtils.REGEX_ATTRIBUTION));
        
        assertArrayEquals("Two simple equations", 
            new String[] {"a + 1 ", " 2 + b"}, 
            "a + 1 = 2 + b".split(RegexUtils.REGEX_ATTRIBUTION));
        
    } 
    
    @Test
    public void testComparison() {
      
        assertArrayEquals("Simple comparison", 
            new String[] {"a", "2"}, 
            "a=2".split(RegexUtils.REGEX_COMPARISON));
        
        assertArrayEquals("Two simple equations", 
            new String[] {"a + 1 ", " 2 + b"}, 
            "a + 1 = 2 + b".split(RegexUtils.REGEX_COMPARISON));
        
        assertArrayEquals("Greater than", 
            new String[] {"a + 1 ", " 2 + b"}, 
            "a + 1 > 2 + b".split(RegexUtils.REGEX_COMPARISON));
        
        assertArrayEquals("Lesser than", 
            new String[] {"a + 1 ", " 2 + b"}, 
            "a + 1 < 2 + b".split(RegexUtils.REGEX_COMPARISON));
        
        assertArrayEquals("Greater or equal than", 
            new String[] {"a + 1 ", " 2 + b"}, 
            "a + 1 >= 2 + b".split(RegexUtils.REGEX_COMPARISON));
        
        assertArrayEquals("Lesser or equal than", 
            new String[] {"a + 1 ", " 2 + b"}, 
            "a + 1 <= 2 + b".split(RegexUtils.REGEX_COMPARISON));
    
    } 
}
