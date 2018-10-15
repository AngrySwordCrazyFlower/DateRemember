package com.example.crazyflower.dateremember;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.crazyflower.dateremember.Data.DatabaseManager;
import com.example.crazyflower.dateremember.Data.Event;
import com.example.crazyflower.dateremember.Util.CalendarUtil;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IRecyclerViewClickListener {

    private static final String TAG = "MainActivity";

    private ItemTouchHelper itemTouchHelper;

    RecyclerView recyclerView;

    Button addFutureEvent;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onStart: ");
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        handler = new MainActivityHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        getEvent();
        initCalendarView();

        if (!AlarmService.isRunning) {
            Intent intent = new Intent(this, AlarmService.class);
            startService(intent);
        }
    }

    private void getEvent() {
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.getFutureEventList(handler);
    }

    private void initCalendarView() {
        TextView yearTextView = (TextView) findViewById(R.id.main_ac_year);
        TextView monthTextView = (TextView) findViewById(R.id.main_ac_month);
        TextView dayTextView = (TextView) findViewById(R.id.main_ac_day);
        TextView weekdayTextView = (TextView) findViewById(R.id.main_ac_weekday);

        Calendar now = Calendar.getInstance(Locale.getDefault());
        yearTextView.setText(String.valueOf(now.get(Calendar.YEAR)));
        monthTextView.setText(String.valueOf(now.get(Calendar.MONTH)));
        dayTextView.setText(String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
        weekdayTextView.setText(getResources().getString(CalendarUtil.getResourceIdOfWeekday(now)));
    }

    private void afterGetEvent(List<Event> list) {
        Log.d(TAG, "afterGetEvent: " + list.size());
        addFutureEvent = (Button) findViewById(R.id.addFutureEvent);
        addFutureEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FutureEventActivity.class);
                intent.putExtra("model", FutureEventActivity.FUTURE_EVENT_ADD_MODEL);
                startActivityForResult(intent, FutureEventActivity.FUTURE_EVENT_ADD_MODEL);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.future_content);

        //设置recyclerview的adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FutureRecyclerAdapter adapter = new FutureRecyclerAdapter(list, this);
        recyclerView.setAdapter(adapter);

        //设置recyclerview的item的点击事件
        recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView, this));

//        itemTouchHelper = new ItemTouchHelper(new MyItemTouchHelperCallBack(adapter));
//        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        recyclerView.setAdapter(null);
//        itemTouchHelper.attachToRecyclerView(null);
        addFutureEvent.setOnClickListener(null);
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int adapterPosition) {
        Log.d(TAG, "onItemClick: ");
        switch (recyclerView.getId()) {
            case R.id.future_content:
                FutureRecyclerAdapter.FutureRecyclerViewHolder vh = (FutureRecyclerAdapter.FutureRecyclerViewHolder) recyclerView.findViewHolderForAdapterPosition((adapterPosition));
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

    static private class MainActivityHandler extends Handler {

        WeakReference<MainActivity> mainActivityWeakReference;

        MainActivityHandler(MainActivity mainActivity) {
            mainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: " + msg.what);
            switch (msg.what) {
                case DatabaseManager.ITERATE_FUTURE_OK:
                    mainActivityWeakReference.get().afterGetEvent((List<Event>) msg.obj);
                    break;
            }
        }
    }

}
