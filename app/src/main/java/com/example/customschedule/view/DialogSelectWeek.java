package com.example.customschedule.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.customschedule.R;
import com.example.customschedule.ui.adapter.MyItemClickListener;
import com.example.customschedule.ui.fragment.ScheduleWeekRefresh;
import com.example.customschedule.util.DateUtil;
import com.example.customschedule.util.RecyclerAdapter;

import java.util.ArrayList;


/**
 * @author hyt
 * @date 2018/2/24
 */

public class DialogSelectWeek extends Dialog implements MyItemClickListener {
    private Context mContext;
    private ArrayList<String> listItem;
    private View view;
    private TextView tv;

    public DialogSelectWeek(Context context, View view, TextView tv) {
        super(context);
        this.mContext = context;
        this.view = view;
        this.tv = tv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_selectweek);
        initData();
        initView();
    }

    private void initView() {
        RecyclerView rv = findViewById(R.id.dialog_selectweek_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        RecyclerAdapter myAdapter = new RecyclerAdapter(mContext, listItem);
        //将重写方法的接口传入adater
        myAdapter.setOnItemClickListenner(this);
        rv.setAdapter(myAdapter);
    }

    private void initData() {
        listItem = new ArrayList<>();
        for (int i = 1; i < 26; i++) {
            listItem.add("第" + String.valueOf(i) + "周");
        }
    }

    /**
     * 对继承的接口的方法进行重写，并且把重写的方法传入Adapter
     */
    @Override
    public void onItemClick(View view, int position) {
        DateUtil.setFirstDayOfNewTerm(position);
        String temp = "第" + String.valueOf(position + 1) + "周";
        tv.setText(temp);

        final ScheduleWeekRefresh scheduleWeekRefresh = new ScheduleWeekRefresh(mContext, this.view);
        scheduleWeekRefresh.refresh(position);

        this.dismiss();
    }
}
