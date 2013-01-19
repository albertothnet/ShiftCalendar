package com.khloke.ShiftCalendar.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.khloke.ShiftCalendar.database.ShiftCalendarDbOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 2:48 PM
 */
public class ShiftCalendar implements DatabaseObject {

    private HashMap<String, Shift> mShiftMap = new HashMap<String, Shift>();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String TABLE_NAME = "ShiftCalendar";
    public static final String DATE_COLUMN = "date";
    public static final String SHIFT_ID_COLUMN = "shiftId";

    public void addShift(String aCalendar, Shift aShift) {
        mShiftMap.put(aCalendar, aShift);
    }

    public HashMap<String, Shift> getShiftMap() {
        return mShiftMap;
    }

    @Override
    public void save(Context aContext) {
        for (String cal:mShiftMap.keySet()) {
            ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
            SQLiteDatabase db = dbOpener.getWritableDatabase();
            db.insert(TABLE_NAME, null, toContentValues(cal, mShiftMap.get(cal)));
        }
    }

    private ContentValues toContentValues(String aCalendar, Shift aShift) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_COLUMN, aCalendar);
        contentValues.put(SHIFT_ID_COLUMN, aShift.getId());

        return contentValues;
    }

    public static ShiftCalendar load(Context aContext) {
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getReadableDatabase();
        Cursor query = db.query(TABLE_NAME, null, DATE_COLUMN + " >=" + DATE_FORMAT.format(Calendar.getInstance().getTime()), new String[0], null, null, DATE_COLUMN);

        ShiftCalendar shiftCalendar = new ShiftCalendar();
        while (query.moveToNext()) {
                shiftCalendar.addShift(
                        query.getString(query.getColumnIndex(DATE_COLUMN)),
                        Shift.load(aContext, query.getInt(query.getColumnIndex(SHIFT_ID_COLUMN))));
        }

        return shiftCalendar;
    }
}
