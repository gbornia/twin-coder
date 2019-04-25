/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.test;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.twincoder.ide.utils.JSONUtils;

/**
 *
 * @author Gabriel
 */
public class JSONUtilsTest {
    
    public JSONUtilsTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testArrayToJson() {
        ArrayList<String> array = new ArrayList();
        array.add("a");
        array.add("b");
        
        assertEquals("Simple array","[\"a\",\"b\"]", JSONUtils.stringArrayListToJson(array));        
    }
    
    
    @Test
    public void testJsonToArray() {
        ArrayList<String> array = JSONUtils.jsonToStringArrayList("[\"a\",\"b\"]");
        assertEquals("Simple array",2, array.size() );        
        assertEquals("Simple array","a", array.get(0));        
        assertEquals("Simple array","b", array.get(1));        
    }    
}
