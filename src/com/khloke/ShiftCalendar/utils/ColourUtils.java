package com.khloke.ShiftCalendar.utils;

import android.graphics.Color;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 12:07 PM
 */
public class ColourUtils {

    public static enum ShiftColours {
        WHITE(Color.WHITE),
        BLUE(Color.BLUE),
        RED(Color.RED),
        GREEN(Color.GREEN),
        MAGENTA(Color.MAGENTA),
        YELLOW(Color.YELLOW);

        private int colourCode;

        private ShiftColours(int aColourCode) {
            colourCode = aColourCode;
        }

        public int getColourCode() {
            return colourCode;
        }
    }

    public static int getColourInt(String aColour) {
        return ShiftColours.valueOf(aColour.toUpperCase()).getColourCode();
    }

    public static int getColourOrdinal(int aColour) {
        for (ShiftColours colour:ShiftColours.values()) {
            if (aColour == colour.getColourCode()){
                return colour.ordinal();
            }
        }

        return -1;
    }

    public static String colourIntToHex(int aColour) {
        return "#" + Integer.toHexString(aColour);
    }
}
