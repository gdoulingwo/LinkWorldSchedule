package com.example.customschedule.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.message.RefreshEvent;
import com.example.customschedule.util.DateUtil;
import com.example.customschedule.view.ScheduleInterface;
import com.example.customschedule.view.ScheduleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

/**
 * @author wangyu
 * @describe 课表展示页面
 */
public class ScheduleWeekFragment extends android.support.v4.app.Fragment {

    private boolean isFirst;
    private ScheduleView scheduleView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scheduleView = new ScheduleView(getContext(), new ScheduleInterface() {
            @Override
            public void refresh(int position) {
                new ScheduleWeekRefresh().refresh(position, diyCourses -> {
                    int step = diyCourses.getStep();
                    int tempID = diyCourses.getIId();
                    int start = diyCourses.getStart();
                    int tempDay = diyCourses.getDay();
                    String tempClsName = diyCourses.getName();
                    String tempClsSite = diyCourses.getRoom();
                    // 此处可以进行优化，将课程名和地点名集合到setTextView方法中，可以顺带优化输入的逻辑
                    tempClsName = tempClsName.length() > 7 ? tempClsName.substring(0, 7).concat("...") : tempClsName;
                    String tempTxt = tempClsName + "@" + tempClsSite;
                    // 填充课表
                    scheduleView.fullSchedule(tempDay, tempTxt, tempID, start, step, diyCourses.getTypeId());
                    // 重置天表
                    if (tempDay == DateUtil.getDayIndexOnWeek()) {
                        scheduleView.setDaySchedule(tempClsName, tempClsSite, start, step, tempID, tempDay);
                        EventBus.getDefault().post(new RefreshEvent(true));
                    }
                });
            }

            @Override
            public DIYCourses findLast() {
                return DataSupport.findLast(DIYCourses.class);
            }

            @Override
            public void startActivity(Intent intentDay) {
                Activity activity = ScheduleWeekFragment.this.getActivity();
                assert activity != null;
                activity.startActivity(intentDay);
            }
        });
        return scheduleView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            return;
        }
        //刷新页面
        scheduleView.refresh(DateUtil.getWeekFromSP());
    }

    @Override
    public void onStart() {
        super.onStart();
        scheduleView.setCurrentWeek();
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
            scheduleView.refreshScreen();
        }
    }
}
