package com.khloke.ShiftCalendar.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.khloke.ShiftCalendar.database.ShiftCalendarDbOpenHelper;
import com.khloke.ShiftCalendar.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 2:50 AM
 */
public class Shift implements DatabaseObject, Serializable {

    public static final String TABLE_NAME = "Shifts";

    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String COLOUR_COLUMN = "colour";
    public static final String TIME_FROM_COLUMN = "timeFrom";
    public static final String TIME_TO_COLUMN = "timeTo";
    public static final String SORT_ORDER_COLUMN = "sortOrder";

    public static final String[] COLUMNS = {
            ID_COLUMN,
            NAME_COLUMN,
            COLOUR_COLUMN,
            TIME_FROM_COLUMN,
            TIME_TO_COLUMN,
            SORT_ORDER_COLUMN
    };

    private int mId = -1;
    private String mName;
    private int mColour;
    private String mTimeFrom;
    private String mTimeTo;
    private int mSortOrder = -1;

    public Shift() {
    }

    public Shift(String aName, int aColour, String aTimeFrom, String aTimeTo) {
        mName = aName;
        mColour = aColour;
        mTimeFrom = aTimeFrom;
        mTimeTo = aTimeTo;
    }

    private Shift(int aId, String aName, int aColour, String aTimeFrom, String aTimeTo, int aSortOrder) {
        mId = aId;
        mName = aName;
        mColour = aColour;
        mTimeFrom = aTimeFrom;
        mTimeTo = aTimeTo;
        mSortOrder = aSortOrder;
    }

    public int getId() {
        return mId;
    }

    private void setId(int aId) {
        mId = aId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String aName) {
        mName = aName;
    }

    public int getColour() {
        return mColour;
    }

    public void setColour(int aColour) {
        mColour = aColour;
    }

    public String getTimeFrom() {
        return mTimeFrom;
    }

    public void setTimeFrom(String aTimeFrom) {
        mTimeFrom = aTimeFrom;
    }

    public String getTimeTo() {
        return mTimeTo;
    }

    public void setTimeTo(String aTimeTo) {
        mTimeTo = aTimeTo;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(int aSortOrder) {
        mSortOrder = aSortOrder;
    }

    public void save(Context aContext) {
        if (mSortOrder < 0) {
            mSortOrder = loadAll(aContext).size();
        }
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        ContentValues contentValues = toContentValues();
        contentValues.remove(ID_COLUMN);
        if (getId() < 0) {
            mId = Long.valueOf(db.insert(TABLE_NAME, null, contentValues)).intValue();
        } else {
            Long.valueOf(db.update(TABLE_NAME, contentValues, ID_COLUMN + "=" + getId(), null)).intValue();
        }
        db.close();
        dbOpener.close();
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COLUMN, mId);
        contentValues.put(NAME_COLUMN, mName);
        contentValues.put(COLOUR_COLUMN, mColour);
        contentValues.put(TIME_FROM_COLUMN, mTimeFrom);
        contentValues.put(TIME_TO_COLUMN, mTimeTo);
        contentValues.put(SORT_ORDER_COLUMN, mSortOrder);
        return contentValues;
    }

    public boolean isValid() {
        return StringUtils.notEmptyOrNull(mName) &&
                mColour >= 0;
    }

    public static Shift load(Context aContext, int aId) {
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getReadableDatabase();
        Cursor query = db.query(TABLE_NAME, null, "id="+String.valueOf(aId), new String[0], null, null, null);

        try {
            if (query.moveToNext()) {
                Shift shift = new Shift(
                        query.getInt(query.getColumnIndex(ID_COLUMN)),
                        query.getString(query.getColumnIndex(NAME_COLUMN)),
                        query.getInt(query.getColumnIndex(COLOUR_COLUMN)),
                        query.getString(query.getColumnIndex(TIME_FROM_COLUMN)),
                        query.getString(query.getColumnIndex(TIME_TO_COLUMN)),
                        query.getInt(query.getColumnIndex(SORT_ORDER_COLUMN))
                );
                db.close();
                dbOpener.close();
                return shift;
            } else {
                db.close();
                dbOpener.close();
                return null;
            }
        } finally {
            query.close();
        }
    }

    public static List<Shift> loadAll(Context aContext) {
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getReadableDatabase();
        Cursor query = db.query(Shift.TABLE_NAME, null, "", new String[0], "", "", SORT_ORDER_COLUMN);
        ArrayList<Shift> shifts = new ArrayList<Shift>();

        try {
            while (query.moveToNext()) {
                shifts.add(
                        new Shift(
                                query.getInt(query.getColumnIndex(ID_COLUMN)),
                                query.getString(query.getColumnIndex(NAME_COLUMN)),
                                query.getInt(query.getColumnIndex(COLOUR_COLUMN)),
                                query.getString(query.getColumnIndex(TIME_FROM_COLUMN)),
                                query.getString(query.getColumnIndex(TIME_TO_COLUMN)),
                                query.getInt(query.getColumnIndex(SORT_ORDER_COLUMN))));
            }
        } finally {
            query.close();
        }

        return shifts;
    }

    public void delete(Context aContext) {
        ShiftCalendarDbOpenHelper dbOpener = new ShiftCalendarDbOpenHelper(aContext);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        db.delete(TABLE_NAME, ID_COLUMN + "=" + getId(), null);
        db.delete(ShiftCalendar.TABLE_NAME, ShiftCalendar.SHIFT_ID_COLUMN + "=" + getId(), null);
        db.close();
        dbOpener.close();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Shift && mId == ((Shift) o).getId();
    }

    @Override
    public String toString() {
        return mName;
    }

    public static class ShiftComparator implements Comparator<Shift> {
        @Override
        public int compare(Shift lhs, Shift rhs) {
            if (lhs.getSortOrder() > rhs.getSortOrder()) {
                return 1;
            } else if (lhs.getSortOrder() < rhs.getSortOrder()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
