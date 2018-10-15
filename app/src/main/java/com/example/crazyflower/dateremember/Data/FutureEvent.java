package com.example.crazyflower.dateremember.Data;

import com.example.crazyflower.dateremember.R;

public class FutureEvent extends Event {

    public FutureEvent(long id, long mills, String note, int remindIndex) {
        super(id, EventType.FUTURE_EVENT, mills, note, remindIndex);
    }

    @Override
    int sourceIdOfVerb() {
        return R.string.future_event_verb;
    }
}
