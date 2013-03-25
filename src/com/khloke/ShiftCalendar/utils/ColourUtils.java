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
        DARK_BLUE(Color.rgb(46, 49, 146)),
        RED(Color.RED),
        DARK_GREEN(Color.rgb(0, 166, 81)),
        MAGENTA(Color.MAGENTA),
        DARK_YELLOW(Color.rgb(238, 226, 0)),
        ORANGE(Color.rgb(247, 148, 29)),
        BROWN(Color.rgb(140, 98, 57)),
        DARK_GREY(Color.DKGRAY);

        private int colourCode;

        private ShiftColours(int aColourCode) {
            colourCode = aColourCode;
        }

        public int getColourCode() {
            return colourCode;
        }
    }

    public static int getColourInt(String aColour) {
        return ShiftColours.valueOf(aColour.toUpperCase().replace(" ", "_")).getColourCode();
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
