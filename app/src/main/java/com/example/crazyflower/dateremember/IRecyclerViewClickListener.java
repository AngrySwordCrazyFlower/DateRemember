package com.example.crazyflower.dateremember;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface IRecyclerViewClickListener {

    void onItemClick(RecyclerView recyclerView, int adapterPosition);

    void onItemLongClick(RecyclerView recyclerView, int adapterPosition);
}
