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
import com.example.customschedule.util.DateUtil;
import com.example.customschedule.util.RecyclerAdapter;

import java.util.ArrayList;


/**
 * @author hyt
 * @date 2018/2/24
 */

public abstract class DialogSelectWeek extends Dialog implements MyItemClickListener {
    private TextView tv;
    private Context mContext;
    private ArrayList<String> listItem;

    public DialogSelectWeek(Context context, TextView tv) {
        super(context);
        this.tv = tv;
        this.mContext = context;
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
            listItem.add(String.valueOf(i) + "周");
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

        afterClick(position);
        this.dismiss();
    }

    /**
     * 在点击之后刷新课表
     *
     * @param position 位置
     */
    abstract void afterClick(int position);
}
