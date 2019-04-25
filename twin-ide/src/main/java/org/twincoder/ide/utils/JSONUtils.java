/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class JSONUtils {
    
    public static String stringArrayListToJson(ArrayList<String> array) {
        String result = null;
        if (array != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                result =  mapper.writeValueAsString(array);
            } catch (JsonProcessingException ex) {
                Logger.getLogger(JSONUtils.class.getName()).log(Level.SEVERE, "Error convering String array to JSON string", ex);
            }
        }
        return result;
    }
    
    public static ArrayList<String> jsonToStringArrayList(String json) {
        ArrayList<String> result = new ArrayList();
        if (json != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                result =  mapper.readValue(json, result.getClass());
            } catch (JsonProcessingException ex) {
                Logger.getLogger(JSONUtils.class.getName()).log(Level.SEVERE, "Error convering JSON string to array", ex);
            } catch (IOException ex) {
                Logger.getLogger(JSONUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }    

    public static String stringHashMapToJson(HashMap<String,String> map) {
        String result = null;
        if (map != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                result =  mapper.writeValueAsString(map);
            } catch (JsonProcessingException ex) {
                Logger.getLogger(JSONUtils.class.getName()).log(Level.SEVERE, "Error convering HashMap to JSON string", ex);
            }
        }
        return result;
    }
    
    public static HashMap<String,String> jsonToStringHashMap(String json) {
        HashMap<String,String> result = new HashMap();
        if (json != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                result =  mapper.readValue(json, result.getClass());
            } catch (JsonProcessingException ex) {
                Logger.getLogger(JSONUtils.class.getName()).log(Level.SEVERE, "Error convering JSON string to hash map", ex);
            } catch (IOException ex) {
                Logger.getLogger(JSONUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }    
}
