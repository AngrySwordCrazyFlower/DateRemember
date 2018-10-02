package com.example.crazyflower.dateremember.Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.crazyflower.dateremember.FutureEvent;
import com.example.crazyflower.dateremember.FutureEventActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

public class DataInMemory {

    private static DataInMemory instance;

    private List<FutureEvent> futureEvents;

    private DataInMemory() {
        futureEvents = new ArrayList<>();
        DateRememberDatabaseHelper helper = DateRememberDatabaseHelper.getInstance();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME, null, null, null, null, null, "the_date");
        String note, date;
        int remindDayIndex, id;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar;
        while (cursor.moveToNext()) {
            try {
                while (cursor.moveToNext()) {
                    note = cursor.getString(cursor.getColumnIndex("note"));
                    date = cursor.getString(cursor.getColumnIndex("the_date"));
                    remindDayIndex = cursor.getInt(cursor.getColumnIndex("reminder_index"));
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    calendar = Calendar.getInstance();
                    calendar.setTime(sdf.parse(date));
                    futureEvents.add(new FutureEvent(id, note, calendar, remindDayIndex));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        database.close();
        helper.close();
    }

    public static DataInMemory getInstance() {
        if (null == instance) {
            synchronized (DataInMemory.class) {
                if (null == instance)
                    instance = new DataInMemory();
            }
        }
        return instance;
    }

    public List<FutureEvent> getFutureEvents() {
        return futureEvents;
    }

    public void addFutureEventByAttributes(String note, Calendar date, int reminderIndex) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String temp = sdf.format(date.getTime());
        SQLiteDatabase database = DateRememberDatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("note", note);
        contentValues.put("the_date", temp);
        contentValues.put("reminder_index", reminderIndex);
        long rowIndex = database.insert(DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME, null, contentValues);
        database.close();
        FutureEvent result = new FutureEvent(rowIndex, note, date, reminderIndex);
        futureEvents.add(0, result);
    }

    public void updateFutureEventByAttributes(long id, String note, Calendar date, int reminderIndex) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String temp = sdf.format(date.getTime());
        SQLiteDatabase database = DateRememberDatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("note", note);
        contentValues.put("the_date", temp);
        contentValues.put("reminder_index", reminderIndex);
        database.update("future_event", contentValues, "id=?", new String[] {String.valueOf(id)});
        database.close();
        FutureEvent futureEvent = getFutureEventById(id);
        futureEvent.setNote(note);
        futureEvent.setCalendar(date);
        futureEvent.setRemindDayIndex(reminderIndex);
    }

    public FutureEvent getFutureEventById(long id) {
        for (FutureEvent futureEvent : futureEvents)
            if (futureEvent.getId() == id)
                return futureEvent;
        return null;
    }


}
