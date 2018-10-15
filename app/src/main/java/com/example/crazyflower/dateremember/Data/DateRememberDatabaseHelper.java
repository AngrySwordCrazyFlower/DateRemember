package com.example.crazyflower.dateremember.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.crazyflower.dateremember.MyApplication;

public class DateRememberDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "date_remember.db";

    private static final int VERSION = 2;

    public static final String FUTURE_EVENT_TABLE_NAME = "future_event";

    public static final String EVENT_COLUMN_ID = "id";

    public static final String EVENT_COLUMN_DATE_MILLS = "date_mills";

    public static final String EVENT_COLUMN_NOTE = "note";

    public static final String EVENT_COLUMN_REMINDER_INDEX = "reminder_index";

    private static final String CREATE_TABLE_PREVIOUS_EVENT = "Create Table " + FUTURE_EVENT_TABLE_NAME + " (id Integer Primary Key Autoincrement, date_mills Integer, note Text, reminder_index Integer);";

    DateRememberDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PREVIOUS_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("Alter Table future_event Add Column reminder_index Integer");
        }
    }

}
