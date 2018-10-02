package com.example.crazyflower.dateremember;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by CrazyFlower on 2018/3/19.
 */

public class FutureEvent implements Serializable {

    private static final String TAG = "FutureEvent : ";

    private long id;

    private String note;

    private Calendar calendar;

    private int remindDayIndex;

    public FutureEvent(long id, String event, Calendar calendar, int remindDayIndex) {
        this.id = id;
        this.note = event;
        this.calendar = calendar;
        this.remindDayIndex = remindDayIndex;
        Log.d(TAG, "FutureEvent: " + this.calendar.toString());
    }

    public String getNote() {
        return note;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public int getRemindDayIndex() {
        return remindDayIndex;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setRemindDayIndex(int remindDayIndex) {
        this.remindDayIndex = remindDayIndex;
    }

    public long getId() {
        return id;
    }
}
