package com.example.crazyflower.dateremember.Util;

import android.annotation.SuppressLint;

import com.example.crazyflower.dateremember.MyApplication;
import com.example.crazyflower.dateremember.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtil {

    public static int MILL_OF_ONE_DAY = 86400000;

    public static int getResourceIdOfWeekday(Calendar calendar) {
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        switch (weekday) {
            case Calendar.MONDAY:
                return R.string.MONDAY;
            case Calendar.TUESDAY:
                return R.string.TUESDAY;
            case Calendar.WEDNESDAY:
                return R.string.WEDNESDAY;
            case Calendar.THURSDAY:
                return R.string.THURSDAY;
            case Calendar.FRIDAY:
                return R.string.FRIDAY;
            case Calendar.SATURDAY:
                return R.string.SATURDAY;
            case Calendar.SUNDAY:
                return R.string.SUNDAY;
        }

        return -1;
    }


    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String getStringByCalendar(Calendar calendar) {
        return format.format(calendar.getTime());
    }

    public static String getStringByMills(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return format.format(calendar.getTime());
    }

    public static Calendar getCalendarByMilliseconds(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return calendar;
    }

    public static int getDifferentDays(long millsBig, long millsSmall) {
        long result = millsBig / MILL_OF_ONE_DAY - millsSmall / MILL_OF_ONE_DAY;
        return (int) result;
    }

}
