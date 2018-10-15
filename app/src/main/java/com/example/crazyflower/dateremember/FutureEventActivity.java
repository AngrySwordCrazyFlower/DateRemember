package com.example.crazyflower.dateremember;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.crazyflower.dateremember.Data.DatabaseManager;
import com.example.crazyflower.dateremember.Data.Event;
import com.example.crazyflower.dateremember.Util.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by CrazyFlower on 2018/4/17.
 */

public class FutureEventActivity extends AppCompatActivity implements MyWheelView.IWheelViewObservable, View.OnClickListener, IRecyclerViewClickListener {

    public static final int FUTURE_EVENT_NO_MODEL = -1;

    public static final int FUTURE_EVENT_NO_ID = -1;

    public static final int FUTURE_EVENT_ADD_MODEL = 100;

    public static final int FUTURE_EVENT_DETAIL_MODEL = 101;

    public static final int FUTURE_EVENT_ADD_SUCCESS = 10;

    public static final int FUTURE_EVENT_ADD_CANCEL = 11;

    public static final int FUTURE_EVENT_DETAIL_NOT_CHANGE = 12;

    public static final int FUTURE_EVENT_DETAIL_CHANGE = 13;

    private static final String TAG = "FutureEventActivity";

    private int model;
    private long eventId;

    private EditText eventContentView;

    private LinearLayout reminderLayout;
    private int remindDaysIndex;
    private TextView daysTextView;

    private TextView leftDaysTextView;
    private MyWheelView yearWheelView;
    private MyWheelView monthWheelView;
    private MyWheelView dayWheelView;
    private Calendar today;
    private Calendar chosenDate;

    private EventActivityHandler handler;

    private PopupWindow popupWindow;

    private int onStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        model = getIntent().getIntExtra("model", FUTURE_EVENT_NO_MODEL);
        Log.d(TAG, "onStart: " + model);
        if (FutureEventActivity.FUTURE_EVENT_NO_MODEL == model)
            finish();
        setContentView(R.layout.add_future_event);
        setCustomActionBar();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onStartTime++;
        handler = new EventActivityHandler(this);
        if (1 == onStartTime) {
            if (FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL == model) {
                //详情模式
                eventId = getIntent().getLongExtra("event_id", FUTURE_EVENT_NO_ID);
                handler.getEvent(eventId);
            } else {
                //增加模式
                initViewWithData(null);
            }
        }
    }

    @Override
    protected void onStop() {
        handler = null;
        super.onStop();
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

    private void initViewWithData(Event event) {
        today = Calendar.getInstance();
        if (null == event) {
            remindDaysIndex = 0;
            chosenDate = Calendar.getInstance(Locale.getDefault());
        } else {
            remindDaysIndex = event.getRemindIndex();
            chosenDate = Calendar.getInstance(Locale.getDefault());
            chosenDate.setTimeInMillis(event.getMills());
        }

        chosenDate.set(Calendar.HOUR, 0);
        chosenDate.set(Calendar.MINUTE, 0);
        chosenDate.set(Calendar.SECOND, 0);
        chosenDate.set(Calendar.MILLISECOND, 0);
        reminderLayout.setOnClickListener(this);
        notifyRemindDaysChanged();

        //先初始化数据之后  再添加监听器!! import ant   不然这个初始化被监听  折腾起来很麻烦
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

    private void notifyRemindDaysChanged() {
        daysTextView.setText(RemindDaysList.getItemByIndex(remindDaysIndex).getText());
    }

    private void setLeftDays() {
        long leftDays = chosenDate.getTimeInMillis() / CalendarUtil.MILL_OF_ONE_DAY - System.currentTimeMillis() / CalendarUtil.MILL_OF_ONE_DAY;
        leftDaysTextView.setText("还有 " + leftDays + " 天");
    }

    private void showRemindDayChoicePopupWindow() {
        View parent = getWindow().getDecorView();
        View popupView = this.getLayoutInflater().inflate(R.layout.remind_day_popupwindow, (ViewGroup) parent, false);

        RecyclerView recyclerView = (RecyclerView) popupView.findViewById(R.id.remind_days_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RemindDaysAdapter adapter = new RemindDaysAdapter(RemindDaysList.getList());
        adapter.setCurrentSelected(remindDaysIndex);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView, this));

        RelativeLayout relativeLayout = popupView.findViewById(R.id.remind_day_popup_window_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setClippingEnabled(false);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                notifyRemindDaysChanged();
            }
        });

        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void showWaitingPopupWindow() {
        View parent = getWindow().getDecorView();
        View view = this.getLayoutInflater().inflate(R.layout.waiting, (ViewGroup) parent, false);
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setClippingEnabled(false);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void dismissWaitingPopupWindow() {
        if (null != popupWindow) {
            if (popupWindow.isShowing())
                popupWindow.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();
        Log.d(TAG, "finish: " + Thread.currentThread().getId());
        dismissWaitingPopupWindow();
    }

    private void cancel() {
        switch (model) {
            case FUTURE_EVENT_DETAIL_MODEL:
                setResult(FUTURE_EVENT_DETAIL_NOT_CHANGE, null);
                break;
            case FUTURE_EVENT_ADD_MODEL:
                setResult(FUTURE_EVENT_ADD_CANCEL, null);
                break;
        }
        finish();
    }

    private void check() {
        //check whether event content blank
        if (eventContentView.getText().toString().equals("")) {
            Toast.makeText(this, "Please input event", Toast.LENGTH_LONG).show();
            return;
        }

        //show popup window to show we are dealing, please wait.
        showWaitingPopupWindow();

        switch (model) {
            case FutureEventActivity.FUTURE_EVENT_ADD_MODEL:
                Log.d(TAG, "Add checking");
                handler.addEvent(chosenDate.getTimeInMillis(), eventContentView.getText().toString(), remindDaysIndex);
                break;
            case FutureEventActivity.FUTURE_EVENT_DETAIL_MODEL:
                handler.updateEvent(eventId, chosenDate.getTimeInMillis(), eventContentView.getText().toString(), remindDaysIndex);
                break;
        }
    }

    private void successAddEvent() {
        setResult(FUTURE_EVENT_ADD_SUCCESS, null);
        this.finish();
    }

    private void successUpdateEvent() {
        setResult(FUTURE_EVENT_DETAIL_CHANGE, null);
        this.finish();
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
                showRemindDayChoicePopupWindow();
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

    static private class EventActivityHandler extends Handler {

        FutureEventActivity futureEventActivity;

        DatabaseManager databaseManager;

        EventActivityHandler(FutureEventActivity futureEventActivity) {
            this.futureEventActivity = futureEventActivity;
            this.databaseManager = new DatabaseManager(futureEventActivity);
        }

        void getEvent(long eventId) {
            databaseManager.findFutureEventById(this, eventId);
        }

        void addEvent(long mills, String note, int reminderIndex) {
            databaseManager.addFutureEventByAttributes(this, mills, note, reminderIndex);
        }
        void updateEvent(long id, long mills, String note, int reminderIndex) {
            databaseManager.updateFutureEventByAttributes(this, id, mills, note, reminderIndex);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DatabaseManager.FIND_EVENT_OK:
                    futureEventActivity.initViewWithData((Event) msg.obj);
                    break;
                case DatabaseManager.ADD_EVENT_OK:
                    futureEventActivity.successAddEvent();
                    break;
                case DatabaseManager.UPDATE_EVENT_OK:
                    futureEventActivity.successUpdateEvent();
                    break;
            }
        }
    }

}
