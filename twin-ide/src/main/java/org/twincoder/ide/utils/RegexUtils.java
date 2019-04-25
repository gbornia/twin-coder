/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.utils;

/**
 *
 * @author gbornia
 */
public class RegexUtils {
    
    public static final String REGEX_SPLIT_PARAMETERS = ",(?=(([^\"]*\"[^\"]*\")*[^\"]*$))";
    
    public static final String REGEX_ATTRIBUTION = "(?!\\\\B\\\"[^\\\"]*)=(?![^\\\"]*\\\"\\\\B)";
    
    public static final String REGEX_COMPARISON = "(?!\\\\B\\\"[^\\\"]*)(=|[<>]=|<|>|\\{|\\}|\\||\\&)(?![^\\\"]*\\\"\\\\B)";
    
    public static final String REGEX_SINGLE_LINE_COMMENT = "\\/\\/.*";
    
    public static final String REGEX_MULTI_LINE_COMMENT = "(?s)\\/\\*.*?\\*\\/";
}
