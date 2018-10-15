package com.example.crazyflower.dateremember.Data;

import android.os.Parcelable;

import com.example.crazyflower.dateremember.R;

import java.io.Serializable;

public abstract class Event implements Serializable {

    enum EventType {
        FUTURE_EVENT,
        PREVIOUS_EVENT
    }

    protected long id;

    protected EventType type;

    protected String note;

    protected long mills;

    protected int remindIndex;

    protected Event (long id, EventType type, long mills, String note, int remindIndex) {
        this.id = id;
        this.type = type;
        this.note = note;
        this.mills = mills;
        this.remindIndex = remindIndex;
    }

    public long getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public int getRemindIndex() {
        return remindIndex;
    }

    public long getMills() {
        return mills;
    }

    public String getNote() {
        return note;
    }

    abstract int sourceIdOfVerb();

    public int sourceIdOfDay() {
        return R.string.event_day;
    }
}
