package com.example.crazyflower.dateremember;

import java.util.Calendar;

/**
 * Created by CrazyFlower on 2018/5/2.
 */

public class MyCalendar {
    public static String getWeekday(Calendar calendar) {
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        switch (weekday) {
            case Calendar.MONDAY:
                return "星期一";
            case Calendar.TUESDAY:
                return "星期二";
            case Calendar.WEDNESDAY:
                return "星期三";
            case Calendar.THURSDAY:
                return "星期四";
            case Calendar.FRIDAY:
                return "星期五";
            case Calendar.SATURDAY:
                return "星期六";
            case Calendar.SUNDAY:
                return "星期日";
        }
        return "未知错误";
    }
}
