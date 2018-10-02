package com.example.crazyflower.dateremember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CrazyFlower on 2018/4/23.
 */

public class RemindDaysList {

    private List<String> list;

    private static RemindDaysList remindDaysList;

    private RemindDaysList() {
        list = new ArrayList<>();
        list.add("当天");
        list.add("一天前");
        list.add("一周前");
        list.add("一月前");
        list.add("一年前");
    }

    public static List<String> getList() {
        if (null == remindDaysList)
            remindDaysList = new RemindDaysList();
        return remindDaysList.list;
    }

    public static String getItemByIndex(int index) {
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return getList().get(index);
        }

        return "未知错误";
    }

}
