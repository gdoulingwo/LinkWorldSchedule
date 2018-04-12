package com.example.customschedule.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.customschedule.R;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.message.RefreshEvent;
import com.example.customschedule.util.DateUtil;
import com.example.customschedule.ui.DIYScheduleActivity;
import com.example.customschedule.view.DialogSelectWeek;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

/**
 * @author hyt
 * @date 2018/2/15
 */
public class ScheduleWeekFragment extends android.support.v4.app.Fragment {

    protected Context mContent;
    private View view;
    private int iID;
    private ScheduleWeekRefresh scheduleWeekRefresh;
    private RelativeLayout day1;
    private RelativeLayout day2;
    private RelativeLayout day3;
    private RelativeLayout day4;
    private RelativeLayout day5;
    private RelativeLayout day6;
    private RelativeLayout day7;
    private TextView tvNowWeek;
    private boolean isFirst;

    /**
     * 刷新show页面
     */
    public void refreshOFMain() {
        day1.removeAllViews();
        day2.removeAllViews();
        day3.removeAllViews();
        day4.removeAllViews();
        day5.removeAllViews();
        day6.removeAllViews();
        day7.removeAllViews();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_schedule_week, container, false);
        scheduleWeekRefresh = new ScheduleWeekRefresh(mContent, view);

        day1 = view.findViewById(R.id.day1);
        day2 = view.findViewById(R.id.day2);
        day3 = view.findViewById(R.id.day3);
        day4 = view.findViewById(R.id.day4);
        day5 = view.findViewById(R.id.day5);
        day6 = view.findViewById(R.id.day6);
        day7 = view.findViewById(R.id.day7);
        tvNowWeek = view.findViewById(R.id.tab_tv_nowweek);

        int weekNow = DateUtil.getWeekNow();
        scheduleWeekRefresh.refresh(weekNow);
        isFirst = true;

        String temp = "第" + String.valueOf(weekNow + 1) + "周";
        tvNowWeek.setText(temp);

        tvNowWeek.setOnClickListener(v -> {
            DialogSelectWeek dialogSelectWeek = new DialogSelectWeek(mContent, view, tvNowWeek);
            dialogSelectWeek.show();
        });

        // 星期设置
        daySetOnClick(day1, 0);
        daySetOnClick(day2, 1);
        daySetOnClick(day3, 2);
        daySetOnClick(day4, 3);
        daySetOnClick(day5, 4);
        daySetOnClick(day6, 5);
        daySetOnClick(day7, 6);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void daySetOnClick(RelativeLayout rl, final int day) {
        rl.setOnClickListener(v -> {
            searchiID();
            Intent intentDay = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("start", day);
            bundle.putInt("iId", iID);
            bundle.putString("name", "");
            bundle.putString("room", "");
            intentDay.putExtras(bundle);
            intentDay.setClass(mContent, DIYScheduleActivity.class);
            startActivity(intentDay);
        });
    }

    /**
     * 查询iID的值
     */
    private void searchiID() {
        //查询表中最后一条数据的iID
        DIYCourses foriID = DataSupport.findLast(DIYCourses.class);
        if (foriID == null) {
            iID = 1;
        } else {
            iID = foriID.getIId() + 1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            return;
        }
        //刷新页面
        scheduleWeekRefresh.refresh(DateUtil.getWeekFromSP());
    }

    @Override
    public void onStart() {
        super.onStart();
        String temp = "第" + String.valueOf(DateUtil.getWeekNow() + 1) + "周";
        tvNowWeek.setText(temp);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshEvent event) {
        if (!event.isRefresh()) {
            refreshOFMain();
        }
    }
}
