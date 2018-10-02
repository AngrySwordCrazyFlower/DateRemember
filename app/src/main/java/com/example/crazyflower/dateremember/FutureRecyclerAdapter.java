package com.example.crazyflower.dateremember;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by CrazyFlower on 2018/3/19.
 */

public class FutureRecyclerAdapter extends RecyclerView.Adapter<FutureRecyclerAdapter.FutureRecyclerViewHolder> implements IItemTouchListener{


    private static final String TAG = "FutureRecyclerAdapter :";

    private Context context;

    private List<FutureEvent> events;

    static class FutureRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView eventTextView;

        TextView daysTextView;

        TextView yyyyTextView;

        TextView mmddTextView;

        FutureEvent event;

        public FutureRecyclerViewHolder(View view) {
            super(view);

            eventTextView = (TextView) view.findViewById(R.id.future_event);

            daysTextView = (TextView) view.findViewById(R.id.future_days);

            yyyyTextView = (TextView) view.findViewById(R.id.future_yyyy);

            mmddTextView = (TextView) view.findViewById(R.id.future_mm_dd);
        }
    }

    public FutureRecyclerAdapter(List<FutureEvent> list, Context context) {
        events = list;
    }

    @Override
    public FutureRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: " + parent.getId());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_ac_future_item, parent, false);
        return new FutureRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FutureRecyclerViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: " + position);
        FutureEvent event = events.get(position);
        holder.event = event;
        holder.eventTextView.setText(event.getNote());
        Calendar eventCalendar = event.getCalendar();
        holder.yyyyTextView.setText(String.valueOf( eventCalendar.get(Calendar.YEAR) ));
        holder.mmddTextView.setText((eventCalendar.get(Calendar.MONTH) + 1) + "-" + eventCalendar.get(Calendar.DAY_OF_MONTH));

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);
        holder.daysTextView.setText(String.valueOf(getDifferentDays(todayCalendar.getTime(), eventCalendar.getTime())));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private long getDifferentDays(Date now, Date future) {
//        Log.d(TAG, "getDifferentDays: " + now.toString());
//        Log.d(TAG, "getDifferentDays: " + future.toString());
        return (future.getTime() - now.getTime()) / 86400000;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(events, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemRemove(int position) {
//        DateRememberDatabaseHelper.getInstance().deleteRaw(events.get(position).getId());
        events.remove(position);
        notifyItemRemoved(position);
    }

}
