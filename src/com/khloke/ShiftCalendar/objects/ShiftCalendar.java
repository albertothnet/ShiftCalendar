package com.khloke.ShiftCalendar.objects;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.khloke.ShiftCalendar.database.ShiftCalendarDbOpenHelper;
import com.khloke.ShiftCalendar.utils.CalendarUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 2:48 PM
 */
public class ShiftCalendar implements DatabaseObject {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TABLE_NAME = "ShiftCalendar";
    public static final String DATE_COLUMN = "date";
    public static final String SHIFT_ID_COLUMN = "shiftId";

    private long mDate;
    private Shift mShift;

    public ShiftCalendar(long aDate, Shift aShift) {
        mDate = CalendarUtil.roundMillisToDate(aDate);
        mShift = aShift;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(int aDate) {
        mDate = CalendarUtil.roundMillisToDate(aDate);
    }

    public Shift getShift() {
        return mShift;
    }

    public void setShift(Shift aShift) {
        mShift = aShift;
    }

    @Override
    public void save(Context aContext) {
        save(aContext, mDate, mShift.getId());
    }

    public static void save(Context aContext, long aDate, int aShiftId) {
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        db.execSQL("INSERT OR REPLACE INTO " + TABLE_NAME + "(date, shiftId) VALUES (" + aDate + ", " + aShiftId + ")");
    }

    public static HashMap<Long, ShiftCalendar> load(Context aContext) {
        Calendar today = Calendar.getInstance();
        return loadFromDate(aContext, today);
    }

    public static HashMap<Long, ShiftCalendar> loadFromDate(Context aContext, Calendar aDate) {
        HashMap<Long, ShiftCalendar> shiftCalendars = new HashMap<Long, ShiftCalendar>();
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getReadableDatabase();
        Cursor query = db.query(TABLE_NAME, null, DATE_COLUMN + " >=" + CalendarUtil.roundMillisToDate(aDate.getTimeInMillis()), new String[0], null, null, DATE_COLUMN);

        while (query.moveToNext()) {
            Long date = query.getLong(query.getColumnIndex(DATE_COLUMN));
            shiftCalendars.put(
                    date,
                    new ShiftCalendar(
                            date,
                            Shift.load(aContext, query.getInt(query.getColumnIndex(SHIFT_ID_COLUMN)))));
        }

        return shiftCalendars;
    }
}
