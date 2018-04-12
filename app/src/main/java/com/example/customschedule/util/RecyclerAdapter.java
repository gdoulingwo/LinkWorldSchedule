package com.example.customschedule.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.customschedule.R;
import com.example.customschedule.ui.adapter.MyItemClickListener;

import java.util.ArrayList;

/**
 * @author hyt
 * @date 2018/2/24
 */

public class RecyclerAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private ArrayList<String> listItem;
    private MyItemClickListener myItemClickListener;


    public RecyclerAdapter(Context context, ArrayList<String> listItem) {
        inflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(inflater.inflate(R.layout.dialog_selectweek_item, null));
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Viewholder vh = (Viewholder) holder;
        vh.textWeek.setText(listItem.get(position));
    }

    //接受从上级传入的重写的方法的接口
    public void setOnItemClickListenner(MyItemClickListener listenner) {
        myItemClickListener = listenner;
    }

    class Viewholder extends RecyclerView.ViewHolder {
        private TextView textWeek;

        public Viewholder(final View root) {
            super(root);
            textWeek = root.findViewById(R.id.text_week);

            root.setOnClickListener(v -> {
                //点击事件
                if (myItemClickListener != null) {
                    //这个接口是在上级被重写了的接口，形参为view 和position
                    myItemClickListener.onItemClick(v, getPosition());
                }
            });
        }

    }
}
