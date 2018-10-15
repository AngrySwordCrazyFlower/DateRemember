package com.example.crazyflower.dateremember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CrazyFlower on 2018/4/23.
 */

public class RemindDaysList {

    private static List<RemindDayElement> list;

    static {
        list = new ArrayList<>();
        list.add(new RemindDayElement(MyApplication.getContext().getResources().getString(R.string.remind_the_day), 0));
        list.add(new RemindDayElement(MyApplication.getContext().getResources().getString(R.string.remind_one_day_before), 1));
        list.add(new RemindDayElement(MyApplication.getContext().getResources().getString(R.string.remind_one_week_before), 7));
        list.add(new RemindDayElement(MyApplication.getContext().getResources().getString(R.string.remind_one_month_before), 30));
        list.add(new RemindDayElement(MyApplication.getContext().getResources().getString(R.string.remind_one_year_before), 365));
    }

    private RemindDaysList() {

    }

    public static List<RemindDayElement> getList() {
        return list;
    }

    public static RemindDayElement getItemByIndex(int index) {
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return getList().get(index);
        }

        return null;
    }

    static class RemindDayElement {

        private String text;

        private int remindDays;

        private RemindDayElement(String text, int remindDays) {
            this.text = text;
            this.remindDays = remindDays;
        }

        public String getText() {
            return text;
        }

        public int getRemindDays() {
            return remindDays;
        }
    }

}
