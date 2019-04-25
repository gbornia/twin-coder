/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.utils;

/**
 *
 * @author gbornia
 */
public class NumberUtils {
    public static String numberToString(double number) {
        if (Math.abs(number) - Math.round(Math.abs(number)) == 0) {
            return Math.round(number) + "";
        } else {
            return number + "";
        }
    }
}
