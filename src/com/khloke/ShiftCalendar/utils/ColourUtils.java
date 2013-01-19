package com.khloke.ShiftCalendar.utils;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 12:07 PM
 */
public class ColourUtils {

    public static int getColourInt(String aColour) {
        HashMap<String, Integer> colourMap = new HashMap<String, Integer>();
        colourMap.put("White", 16777215);
        colourMap.put("Blue", 255);
        colourMap.put("Red", 16711680);
        colourMap.put("Green", 65280);

        return colourMap.get(aColour);
    }

    public static String colourIntToHex(int aColour) {
        return "#" + Integer.toHexString(aColour);
    }
}
