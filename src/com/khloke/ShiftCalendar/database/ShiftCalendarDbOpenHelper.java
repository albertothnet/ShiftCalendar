package com.khloke.ShiftCalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 2:23 AM
 */
public class ShiftCalendarDbOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "ShiftCalendar";
    public static final String CREATE_SHIFTCALENDAR_TABLE_STATEMENT =
            "CREATE  TABLE `ShiftCalendar` (" +
                    "  `date` INTEGER PRIMARY KEY ," +
                    "  `shiftId` INT NOT NULL" +
                    ");";
    private static final String CREATE_SHIFTS_TABLE_STATEMENT =
            "CREATE TABLE `Shifts` (" +
                    "  `id` INTEGER PRIMARY KEY," +
                    "  `name` text NOT NULL DEFAULT ''," +
                    "  `colour` int(11) NOT NULL," +
                    "  `timeFrom` text," +
                    "  `timeTo` text," +
                    "  `sortOrder` int(11)" +
                    ");";


    public ShiftCalendarDbOpenHelper(Context aContext) {
        super(aContext, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(CREATE_SHIFTS_TABLE_STATEMENT);
        db.execSQL(CREATE_SHIFTCALENDAR_TABLE_STATEMENT);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                //Do nothing
        }
    }
}
