package com.example.crazyflower.dateremember;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crazyflower.dateremember.Data.DataInMemory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by CrazyFlower on 2018/4/17.
 */

public class FutureEventActivity extends AppCompatActivity implements MyWheelView.IWheelViewObservable, View.OnClickListener, IRecyclerViewClickListener {

    public static final int FUTURE_EVENT_NO_MODEL = -1;

    public static final int FUTURE_EVENT_ADD_MODEL = 100;

    public static final int FUTURE_EVENT_DETAIL_MODEL = 101;

    public static final int FUTURE_EVENT_ADD_SUCCESS = 10;

    public static final int FUTURE_EVENT_ADD_CANCEL = 11;

    public static final int FUTURE_EVENT_DETAIL_NOT_CHANGE = 12;

    public static final int FUTURE_EVENT_DETAIL_CHANGE = 13;

    private static final String TAG = "FutureEventActivity";

    private static final int DAY_MILLIS = 86400000;

    private int model;
    private FutureEvent event;

    private EditText eventContentView;

    private LinearLayout reminderLayout;
    private int remindDaysIndex;
    private List<String> remindDaysList;
    private TextView daysTextView;

    private TextView leftDaysTextView;
    private long leftDays;
    private MyWheelView yearWheelView;
    private MyWheelView monthWheelView;
    private MyWheelView dayWheelView;
    private Calendar today;
    private Calendar chosenDate;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = getIntent().getIntExtra("model", FUTURE_EVENT_NO_MODEL);
        Log.d(TAG, "onCreate: " + model);
        if (FutureEventActivity.FUTURE_EVENT_NO_MODEL == model)
            finish();
        setContentView(R.layout.add_future_event);
        setCustomActionBar();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
        initViewWithData();
    }

    private void initData() {
        long eventId = getIntent().getLongExtra("event_id", -1);

        today = Calendar.getInstance();
        today.setTimeInMillis(today.getTimeInMillis() / DAY_MILLIS * DAY_MILLIS);

        remindDaysList = RemindDaysList.getList();
        if (-1 != eventId) {
            event = DataInMemory.getInstance().getFutureEventById(eventId);
            remindDaysIndex = event.getRemindDayIndex();
            chosenDate = Calendar.getInstance();
            chosenDate.setTimeInMillis(event.getCalendar().getTimeInMillis() / DAY_MILLIS * DAY_MILLIS);
            eventContentView.setText(event.getNote());
        }
        else {
            remindDaysIndex = 0;
            chosenDate = Calendar.getInstance();
            chosenDate.setTimeInMillis(chosenDate.getTimeInMillis() / DAY_MILLIS * DAY_MILLIS);
        }
    }

    private void initView() {
        eventContentView = (EditText) findViewById(R.id.event_content);

        reminderLayout = (LinearLayout) findViewById(R.id.remind_layout);
        daysTextView = (TextView) findViewById(R.id.remind_days);

        leftDaysTextView = (TextView) findViewById(R.id.left_days);
        yearWheelView = (MyWheelView) findViewById(R.id.year);
        monthWheelView = (MyWheelView) findViewById(R.id.month);
        dayWheelView = (MyWheelView) findViewById(R.id.day);
    }

    private void initViewWithData() {
        reminderLayout.setOnClickListener(this);
        notifyRemindDaysChanged();

        //先初始化数据之后  再添加监听器!! important   不然这个初始化被监听  折腾起来很麻烦
        updateYearWheelView();
        yearWheelView.setCurrentItemIndex(yearWheelView.getData().indexOf(Integer.toString(chosenDate.get(Calendar.YEAR))));
        updateMonthWheelView();
        monthWheelView.setCurrentItemIndex(monthWheelView.getData().indexOf(Integer.toString(chosenDate.get(Calendar.MONTH) + 1)));
        updateDayWheelView();
        dayWheelView.setCurrentItemIndex(dayWheelView.getData().indexOf(Integer.toString(chosenDate.get(Calendar.DAY_OF_MONTH))));

        yearWheelView.setObserver(this);
        monthWheelView.setObserver(this);
        dayWheelView.setObserver(this);

        setLeftDays();
    }

    private void updateYearWheelView() {
        List<String> yearList = new ArrayList<>();
        int year = today.get(Calendar.YEAR);
        for (int i = 0; i < 30; i++) {
            yearList.add((year + i) + "");
        }
        yearWheelView.setData(yearList);
    }

    private void updateMonthWheelView() {
        List<String> monthList = new ArrayList<>();
        if (chosenDate.get(Calendar.YEAR) == today.get(Calendar.YEAR))
            for (int i = today.get(Calendar.MONTH); i <= Calendar.DECEMBER; ++i)
                monthList.add(Integer.toString(i + 1));
        else
            for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; ++i)
                monthList.add(Integer.toString(i + 1));
        monthWheelView.setData(monthList);
    }

    private void updateDayWheelView() {
        List<String> dayList = new ArrayList<>();
        if (chosenDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && chosenDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
            int max = chosenDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = today.get(Calendar.DAY_OF_MONTH); i <= max; ++i)
                dayList.add(Integer.toString(i));
        }
        else {
            int max = chosenDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= max; ++i)
                dayList.add(Integer.toString(i));
        }
        dayWheelView.setData(dayList);
    }

    private void setCustomActionBar() {
        View myActionBarView = null;
        if (FUTURE_EVENT_ADD_MODEL == model) {
            myActionBarView = LayoutInflater.from(this).inflate(R.layout.add_model_action_bar, null);
            Button closeButton = myActionBarView.findViewById(R.id.future_event_add_model_action_bar_cancel);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
            Button checkButton = myActionBarView.findViewById(R.id.future_event_add_model_action_bar_check);
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check();
                }
            });
        } else if (FUTURE_EVENT_DETAIL_MODEL == model) {
            myActionBarView = LayoutInflater.from(this).inflate(R.layout.detail_model_action_bar, null);
            TextView closeButton = myActionBarView.findViewById(R.id.future_event_detail_model_action_bar_cancel);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
            TextView checkButton = myActionBarView.findViewById(R.id.future_event_detail_model_action_bar_check);
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check();
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(myActionBarView);

    }

    private void backgroundAlpha (float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void notifyRemindDaysChanged() {
        daysTextView.setText(remindDaysList.get(remindDaysIndex));
    }

    private void showPopupWindow() {

        backgroundAlpha((float) 0.5);

        final View popupView = FutureEventActivity.this.getLayoutInflater().inflate(R.layout.remind_day_popupwindow, null);

        final RecyclerView recyclerView = (RecyclerView) popupView.findViewById(R.id.remind_days_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RemindDaysAdapter adapter = new RemindDaysAdapter(remindDaysList);
        adapter.setCurrentSelected(remindDaysIndex);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView, this));

        popupWindow.setAnimationStyle(R.style.popup_window_anim);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        popupWindow.setFocusable(true);

        popupWindow.setTouchable(true);

        popupWindow.setOutsideTouchable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((float) 1);
                notifyRemindDaysChanged();
            }
        });

        View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void setLeftDays() {
        leftDays = (long) (chosenDate.getTimeInMillis() - today.getTimeInMillis()) / DAY_MILLIS;
        leftDaysTextView.setText("还有 " + leftDays + " 天");
    }

    private void cancel() {
        switch (model) {
            case FutureEventActivity.FUTURE_EVENT_ADD_CANCEL:
                setResult(FutureEventActivity.FUTURE_EVENT_ADD_CANCEL, null);
                break;
            case FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL:
                setResult(FutureEventActivity.FUTURE_EVENT_DETAIL_NOT_CHANGE, null);
                break;
        }
        finish();
    }

    private void check() {
        if (eventContentView.getText().toString().equals("")) {
            Toast.makeText(this, "Please input event", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        switch (model) {
            case FutureEventActivity.FUTURE_EVENT_ADD_MODEL:
                Log.d(TAG, "Add checking");
                intent.putExtra("the_date", chosenDate);
                intent.putExtra("note", eventContentView.getText().toString());
                intent.putExtra("reminder_index", remindDaysIndex);
                setResult(FutureEventActivity.FUTURE_EVENT_ADD_SUCCESS, intent);
                break;
            case FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL:
                intent.putExtra("the_date", chosenDate);
                intent.putExtra("note", eventContentView.getText().toString());
                intent.putExtra("reminder_index", remindDaysIndex);
                intent.putExtra("id", event.getId());
                setResult(FutureEventActivity.FUTURE_EVENT_DETAIL_CHANGE, intent);
                break;
        }
        finish();
    }

    @Override
    public void notice(View view) {
        switch (view.getId()) {
            case R.id.year:
                Log.d(TAG, "notice: year " + chosenDate.get(Calendar.YEAR) + "  " + chosenDate.get(Calendar.MONTH) + "  " + chosenDate.get(Calendar.DAY_OF_MONTH));
                chosenDate.set(Calendar.YEAR, Integer.parseInt(((MyWheelView) view).getCurrentText()));
                updateMonthWheelView();
                break;
            case R.id.month:
                Log.d(TAG, "notice: month " + chosenDate.get(Calendar.YEAR) + "  " + chosenDate.get(Calendar.MONTH) + "  " + chosenDate.get(Calendar.DAY_OF_MONTH));
                chosenDate.set(Calendar.MONTH, Integer.parseInt(((MyWheelView) view).getCurrentText()) - 1);
                updateDayWheelView();
                break;
            case R.id.day:
                Log.d(TAG, "notice: day " + chosenDate.get(Calendar.YEAR) + "  " + chosenDate.get(Calendar.MONTH) + "  " + chosenDate.get(Calendar.DAY_OF_MONTH));
                chosenDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(((MyWheelView) view).getCurrentText()));
                setLeftDays();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remind_layout:
                showPopupWindow();
                break;
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int adapterPosition) {
        remindDaysIndex = adapterPosition;
        popupWindow.dismiss();
    }

    @Override
    public void onItemLongClick(RecyclerView recyclerView, int adapterPosition) {
        //nothing to do
    }

}
