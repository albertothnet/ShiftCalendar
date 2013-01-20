package com.khloke.ShiftCalendar.utils;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 20/01/13
 * Time: 6:15 PM
 */
public class CalendarUtil {

    public static final int MILLIS_IN_DAY = 86400000;

    public static int roundMillisToDate(int aMillis) {
        Double dateMillis = (double) aMillis;
        Double dateInMillis = Math.floor(dateMillis/86400000) * MILLIS_IN_DAY;
        return dateInMillis.intValue();
    }
}
