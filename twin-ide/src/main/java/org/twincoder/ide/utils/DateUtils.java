/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.utils;

import java.util.Date;

/**
 *
 * @author gbornia
 */
public class DateUtils {
 
    public static final int MILLIS_TO_DAY = (1000 * 60 * 60 * 24);

    public static double millisToDays(long millis) {
        return (double)millis / (double)MILLIS_TO_DAY;
    }
    
    public static long daysToMillis(double days) {
        return Math.round(days * MILLIS_TO_DAY);
    }
    
    public static Date daysToDate(double days) {
        return new Date(daysToMillis(days));
    }
    
    public static double dateToDays(Date date) {
        if (date != null) {
            return millisToDays(date.getTime());
        }
        return 0;
    }
}
