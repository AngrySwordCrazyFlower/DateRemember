package com.example.crazyflower.dateremember.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.crazyflower.dateremember.MyApplication;

public class DateRememberDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "date_remember.db";

    public static final String FUTURE_EVENT_TABLE_NAME = "future_event";

    private static final int VERSION = 2;

    private static final String CREATE_TABLE_PREVIOUS_EVENT = "Create Table " + FUTURE_EVENT_TABLE_NAME + " (id Integer Primary Key Autoincrement, the_date Date, note Text, reminder_index Integer);";

    private static DateRememberDatabaseHelper instance;

    private DateRememberDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
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

    public static DateRememberDatabaseHelper getInstance() {
        if (null == instance) {
            synchronized (DateRememberDatabaseHelper.class) {
                if (null == instance)
                    instance = new DateRememberDatabaseHelper(MyApplication.getContext(), DATABASE_NAME, null, VERSION);
            }
        }
        return instance;
    }

}
