package com.example.crazyflower.dateremember;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.example.crazyflower.dateremember.Data.DatabaseManager;
import com.example.crazyflower.dateremember.Data.Event;
import com.example.crazyflower.dateremember.Util.CalendarUtil;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NO_EVENT_ID = -1;

    private long eventId;

    private ShareActivityHandler handler;

    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check whether exist
        Intent intent = getIntent();
        eventId = intent.getLongExtra("event_id", NO_EVENT_ID);
        if (NO_EVENT_ID == eventId)
            finish();

        setContentView(R.layout.share_activity);
        //when create, the info is null, need to be update, it means info is updated.
        updated = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new ShareActivityHandler(this);
        if (updated) {
            handler.findEvent(eventId);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler = null;
        findViewById(R.id.share_activity_return).setOnClickListener(null);
        findViewById(R.id.share_activity_edit).setOnClickListener(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL == requestCode) {
            if (FutureEventActivity.FUTURE_EVENT_DETAIL_CHANGE == resultCode) {
                updated =true;
            }
        }
    }

    private void updateEventInfo(Event event) {
        TextView eventNote = findViewById(R.id.share_activity_event_note);
        TextView eventDate = findViewById(R.id.share_activity_event_date);
        TextView eventLeftDays = findViewById(R.id.share_activity_event_left_days);
        eventNote.setText(event.getNote());
        eventDate.setText(CalendarUtil.getStringByMills(event.getMills()));
        eventLeftDays.setText(String.valueOf(CalendarUtil.getDifferentDays(System.currentTimeMillis(), event.getMills())));
        setClickListener();
    }

    private void setClickListener() {
        findViewById(R.id.share_activity_return).setOnClickListener(this);
        findViewById(R.id.share_activity_edit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_activity_return:
                finish();
                break;
            case R.id.share_activity_edit:
                Intent intent = new Intent(this, FutureEventActivity.class);
                intent.putExtra("event_id", eventId);
                intent.putExtra("model", FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL);
                startActivityForResult(intent, FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL);
                break;
        }
    }

    static private class ShareActivityHandler extends Handler {

        ShareActivity shareActivity;

        DatabaseManager databaseManager;

        ShareActivityHandler(ShareActivity shareActivity) {
            this.shareActivity = shareActivity;
            databaseManager = new DatabaseManager(shareActivity);
        }

        void findEvent(long eventId) {
            databaseManager.findFutureEventById(this, eventId);
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DatabaseManager.FIND_EVENT_OK:
                    shareActivity.updateEventInfo((Event) msg.obj);
                    break;
                case DatabaseManager.FIND_EVENT_ERROR:
                    shareActivity.finish();
                    break;
            }
        }
    }

}
