package com.example.crazyflower.dateremember;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.crazyflower.dateremember.Data.DataInMemory;
import com.example.crazyflower.dateremember.Data.DateRememberDatabaseHelper;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IRecyclerViewClickListener {

    private static final String TAG = "MainActivity";

    private List<FutureEvent> list;

    private Button addFutureEvent;

    private RecyclerView recyclerView;

    private TextView yearTextView;
    private TextView monthTextView;
    private TextView dayTextView;
    private TextView weekdayTextView;

    private Calendar calendar;

    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
        initViewWithData();
        if (!AlarmService.isRunning) {
            Intent intent = new Intent(this, AlarmService.class);
            startService(intent);
        }
    }

    private void initView() {
        addFutureEvent = (Button) findViewById(R.id.addFutureEvent);
        recyclerView = (RecyclerView) findViewById(R.id.future_content);

        yearTextView = (TextView) findViewById(R.id.main_ac_year);
        monthTextView = (TextView) findViewById(R.id.main_ac_month);
        dayTextView = (TextView) findViewById(R.id.main_ac_day);
        weekdayTextView = (TextView) findViewById(R.id.main_ac_weekday);
    }

    private void initData() {
        list = DataInMemory.getInstance().getFutureEvents();
        calendar = Calendar.getInstance();
    }

    private void initViewWithData() {

        //设置recyclerview的adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FutureRecyclerAdapter adapter = new FutureRecyclerAdapter(list, this);
        recyclerView.setAdapter(adapter);

        //设置recyclerview的item的点击事件
        recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView, this));

//        itemTouchHelper = new ItemTouchHelper(new MyItemTouchHelperCallBack(adapter));
//        itemTouchHelper.attachToRecyclerView(recyclerView);
        addFutureEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FutureEventActivity.class);
                intent.putExtra("model", FutureEventActivity.FUTURE_EVENT_ADD_MODEL);
                startActivityForResult(intent, FutureEventActivity.FUTURE_EVENT_ADD_MODEL);
            }
        });

        monthTextView.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        dayTextView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        yearTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        weekdayTextView.setText(MyCalendar.getWeekday(calendar));
    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerView.setAdapter(null);
//        itemTouchHelper.attachToRecyclerView(null);
        addFutureEvent.setOnClickListener(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (FutureEventActivity.FUTURE_EVENT_ADD_MODEL == requestCode) {
            if (FutureEventActivity.FUTURE_EVENT_ADD_SUCCESS == resultCode) {
                if (null != data) {
                    DataInMemory.getInstance().addFutureEventByAttributes(data.getStringExtra("note"), (Calendar) data.getSerializableExtra("the_date"), data.getIntExtra("reminder_index", 0));
                }
            } else {

            }
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int adapterPosition) {
        Log.d(TAG, "onItemClick: ");
        switch (recyclerView.getId()) {
            case R.id.future_content:
                FutureRecyclerAdapter.FutureRecyclerViewHolder vh = (FutureRecyclerAdapter.FutureRecyclerViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(adapterPosition));
                Intent intent = new Intent();
                intent.setClass(this, ShareActivity.class);
                intent.putExtra("event_id", vh.event.getId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemLongClick(RecyclerView recyclerView, int adapterPosition) {

    }

}
