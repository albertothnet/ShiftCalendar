package com.khloke.ShiftCalendar.utils;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 2:38 AM
 */
public class StringUtils {

    public static boolean notEmptyOrNull(String aString) {
        return aString != null && !aString.trim().equals("");
    }
}
