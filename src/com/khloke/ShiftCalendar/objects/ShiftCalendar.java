package com.khloke.ShiftCalendar.objects;

import android.content.ContentValues;
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
    public static final String TABLE_NAME = "ShiftCalendar";
    public static final String DATE_COLUMN = "date";
    public static final String SHIFT_ID_COLUMN = "shiftId";

    private long mDate;
    private Shift mShift;
    private Shift mOriginal;

    public ShiftCalendar(long aDate, Shift aShift) {
        mDate = CalendarUtil.roundMillisToDate(aDate);
        mShift = aShift;
        mOriginal = null;
    }

    public ShiftCalendar(long aDate, Shift aShift, boolean aNew) {
        mDate = aDate;
        mShift = aShift;
        if (!aNew) {
            mOriginal = aShift;
        } else {
            mOriginal = null;
        }
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

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_COLUMN, mDate);
        contentValues.put(SHIFT_ID_COLUMN, mShift.getId());

        return contentValues;
    }

    public boolean isDirty() {
        return !mShift.equals(mOriginal);
    }

    @Override
    public void save(Context aContext) {
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        if (mOriginal == null) {
            db.insert(TABLE_NAME, null, toContentValues());
            mOriginal = mShift;
        } else {
            if (!mOriginal.equals(mShift)) {
                db.update(TABLE_NAME, toContentValues(), DATE_COLUMN + "=" + String.valueOf(mDate), null);
                mOriginal = mShift;
            }
        }
        db.close();
        dbOpener.close();
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

        try {
            while (query.moveToNext()) {
                Long date = query.getLong(query.getColumnIndex(DATE_COLUMN));
                shiftCalendars.put(
                        date,
                        new ShiftCalendar(
                                date,
                                Shift.load(aContext, query.getInt(query.getColumnIndex(SHIFT_ID_COLUMN))),
                                false));
            }
        } finally {
            query.close();
        }
        db.close();
        dbOpener.close();

        return shiftCalendars;
    }
}
