package com.example.crazyflower.dateremember;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.crazyflower.dateremember.Data.DataInMemory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ShareActivity";

    private FutureEvent event;

    private TextView eventNote;

    private TextView eventDate;

    private TextView eventLeftDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);
        initView();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        initData();
        initViewWithData();
    }

    private void initData() {
        long eventId = getIntent().getLongExtra("event_id", -1);
        if (-1 == eventId) {
            finish();
        }
        event = DataInMemory.getInstance().getFutureEventById(eventId);
        if (null == event)
            finish();
    }

    private void initView() {
        eventNote = findViewById(R.id.share_activity_event_note);
        eventDate = findViewById(R.id.share_activity_event_date);
        eventLeftDays = findViewById(R.id.share_activity_event_left_days);

        findViewById(R.id.share_activity_return).setOnClickListener(this);
        findViewById(R.id.share_activity_edit).setOnClickListener(this);
    }

    private void initViewWithData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        eventNote.setText(event.getNote());
        eventDate.setText(simpleDateFormat.format(event.getCalendar().getTime()));
        eventLeftDays.setText(String.valueOf((Calendar.getInstance().getTimeInMillis() - event.getCalendar().getTimeInMillis()) / 86400000) + "天");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.share_activity_return:
                finish();
                break;
            case R.id.share_activity_edit:
                intent = new Intent(this, FutureEventActivity.class);
                intent.putExtra("event_id", event.getId());
                intent.putExtra("model", FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL);
                startActivityForResult(intent, FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL == requestCode) {
            if (FutureEventActivity.FUTURE_EVENT_DETAIL_CHANGE == resultCode) {
                if (null != data) {
                    Log.d(TAG, "onActivityResult: " + data.getStringExtra("note"));
                    DataInMemory.getInstance().updateFutureEventByAttributes(data.getLongExtra("id", 0), data.getStringExtra("note"), (Calendar) data.getSerializableExtra("the_date"), data.getIntExtra("reminder_index", 0));
                    Log.d(TAG, "onActivityResult: " + event.getNote());
                }
            } else {

            }
        }
    }

}
