package com.example.crazyflower.dateremember;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CrazyFlower on 2018/4/19.
 */

public class RemindDaysAdapter extends RecyclerView.Adapter<RemindDaysAdapter.RemindDaysViewHolder> {

    private List<String> list;
    private int currentSelected = 0;

    static class RemindDaysViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public View remindDaysView;

        public RemindDaysViewHolder(View itemView) {
            super(itemView);

            remindDaysView = itemView;
            textView = (TextView) itemView.findViewById(R.id.days);
            imageView = (ImageView) itemView.findViewById(R.id.selected);
        }
    }

    @Override
    public RemindDaysViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remind_days_item, parent, false);

        RemindDaysViewHolder viewHolder = new RemindDaysViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RemindDaysViewHolder holder, int position) {
        if (position == currentSelected) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setTextColor(0xff1296db);
        }
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public RemindDaysAdapter(List<String> list) {
        this.list = list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setCurrentSelected(int selected) {
        this.currentSelected = selected;
    }
}
