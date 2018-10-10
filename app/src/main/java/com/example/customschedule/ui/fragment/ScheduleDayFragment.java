package com.example.customschedule.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.example.customschedule.R;
import com.example.customschedule.http.Schedule.Import2Database;
import com.example.customschedule.http.bean.DIYDaySchedule;
import com.example.customschedule.http.message.RefreshEvent;
import com.example.customschedule.ui.adapter.DayScheduleAdapter;
import com.example.customschedule.util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author hyt
 * @date 2018/2/15
 */
public class ScheduleDayFragment extends android.support.v4.app.Fragment {

    private ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
    private DayScheduleAdapter adapterDaySchedule;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private TextView textView;
    private Context mContext;

    public void clearData() {
        listItem.clear();
        //实例化adapter
        adapterDaySchedule = new DayScheduleAdapter(listItem);
        recyclerView.setAdapter(adapterDaySchedule);
    }

    public void initData() {
        if (listItem.isEmpty()) {
            readyData();
        }
        //实例化adapter
        adapterDaySchedule = new DayScheduleAdapter(listItem);
        recyclerView.setAdapter(adapterDaySchedule);
        adapterDaySchedule.notifyDataSetChanged();
    }

    public void resetData() {
        listItem.clear();
        readyData();
        //实例化adapter
        adapterDaySchedule = new DayScheduleAdapter(listItem);
        recyclerView.setAdapter(adapterDaySchedule);
        adapterDaySchedule.notifyDataSetChanged();
    }

    private void readyData() {
        int day = DateUtil.getDayIndexOnWeek();
        //读取当天的数据
        List<DIYDaySchedule> diyDayScheduleList = DataSupport
                .where("day = ?", String.valueOf(day))
                .order("startWeek")
                .find(DIYDaySchedule.class);
        if (diyDayScheduleList.size() == 0) {
            linearLayout.removeView(textView);
            textView = new TextView(mContext);
            textView.setText("今天没有课");
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            linearLayout.addView(textView);
        } else {
            linearLayout.removeView(textView);
        }

        for (int i = 0; i < diyDayScheduleList.size(); i++) {
            String clsName = diyDayScheduleList.get(i).getName();
            String clsSite = diyDayScheduleList.get(i).getRoom();
            int clsStartNumber = diyDayScheduleList.get(i).getStartWeek();
            int clsCountNumber = diyDayScheduleList.get(i).getStep();
            // 课程节数
            int start = clsStartNumber + 1;
            int end = clsStartNumber + clsCountNumber;
            String clsNumber = String.valueOf(start) + "—" + String.valueOf(end) + "节";

            HashMap<String, Object> map = new HashMap<>();
            map.put(Import2Database.NAME, clsName);
            map.put(Import2Database.ROOM, clsSite);
            map.put("clsNumber", clsNumber);
            LogUtils.i("name" + clsName, "room" + clsSite, "clsNumber" + clsNumber);
            listItem.add(map);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_schedule_day, container, false);
        linearLayout = view.findViewById(R.id.tab_dayshcedule_ll);
        recyclerView = view.findViewById(R.id.tab_day_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshEvent event) {
        if (event.isRefresh()) {
            resetData();
        } else {
            clearData();
        }
    }
}
