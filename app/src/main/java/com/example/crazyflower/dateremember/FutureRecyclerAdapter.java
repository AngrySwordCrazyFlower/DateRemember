package com.example.crazyflower.dateremember;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.crazyflower.dateremember.Data.Event;
import com.example.crazyflower.dateremember.Data.FutureEvent;
import com.example.crazyflower.dateremember.Util.CalendarUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by CrazyFlower on 2018/3/19.
 */

public class FutureRecyclerAdapter extends RecyclerView.Adapter<FutureRecyclerAdapter.FutureRecyclerViewHolder> implements IItemTouchListener{


    private static final String TAG = "FutureRecyclerAdapter :";

    private List<Event> events;

    static class FutureRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView eventTextView;

        TextView daysTextView;

        TextView yyyyTextView;

        TextView mmddTextView;

        Event event;

        public FutureRecyclerViewHolder(View view) {
            super(view);

            eventTextView = (TextView) view.findViewById(R.id.future_event);

            daysTextView = (TextView) view.findViewById(R.id.future_days);

            yyyyTextView = (TextView) view.findViewById(R.id.future_yyyy);

            mmddTextView = (TextView) view.findViewById(R.id.future_mm_dd);
        }
    }

    public FutureRecyclerAdapter(List<Event> list, Context context) {
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
        Event event = events.get(position);
        holder.event = event;
        holder.eventTextView.setText(event.getNote());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getMills());
        holder.yyyyTextView.setText(String.valueOf(calendar.get(Calendar.YEAR) ));
        holder.mmddTextView.setText((calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
        holder.daysTextView.setText(String.valueOf(CalendarUtil.getDifferentDays(event.getMills(), System.currentTimeMillis())));
    }

    @Override
    public int getItemCount() {
        return null == events ? 0 : events.size();
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
