package com.example.customschedule.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.customschedule.R;
import com.example.customschedule.http.Schedule.Import2Database;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.bean.DIYDaySchedule;
import com.example.customschedule.ui.DIYScheduleActivity;
import com.example.customschedule.util.DateUtil;

import lombok.Getter;

/**
 * @author wangyu
 * @date 10/10/18
 * @describe 课表View
 */

public class ScheduleView extends LinearLayout {
    private int iID;
    private TextView tvNowWeek;
    @Getter
    private RelativeLayout day1;
    @Getter
    private RelativeLayout day2;
    @Getter
    private RelativeLayout day3;
    @Getter
    private RelativeLayout day4;
    @Getter
    private RelativeLayout day5;
    @Getter
    private RelativeLayout day6;
    @Getter
    private RelativeLayout day7;
    private ScheduleInterface scheduleInterface;

    public ScheduleView(Context context, ScheduleInterface scheduleInterface) {
        super(context);
        this.scheduleInterface = scheduleInterface;
        initView(context);
    }

    public ScheduleView(Context context) {
        this(context, (ScheduleInterface) null);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs) {
        this(context, (ScheduleInterface) null);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, (ScheduleInterface) null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, (ScheduleInterface) null);
    }

    /**
     * 保存数据
     */
    public void setDaySchedule(String clsName, String clsSite, int clsStartNumber,
                               int clsCountNumber, int iID, int day) {
        LogUtils.d(clsName, clsSite, clsStartNumber, clsCountNumber, iID, day);

        DIYDaySchedule diyDaySchedule = new DIYDaySchedule();
        diyDaySchedule.setName(clsName);
        diyDaySchedule.setRoom(clsSite);
        diyDaySchedule.setStartWeek(clsStartNumber);
        diyDaySchedule.setStep(clsCountNumber);
        diyDaySchedule.setIId(iID);
        diyDaySchedule.setDay(day);
        // 保存数据
        diyDaySchedule.save();
    }

    private void initView(Context context) {
        // 设置参数
        LayoutInflater.from(context).inflate(R.layout.schedule_week, this, true);
        this.setOrientation(VERTICAL);
        this.setBackgroundColor(this.getResources().getColor(R.color.transparent));
        this.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        this.day1 = this.findViewById(R.id.day1);
        this.day2 = this.findViewById(R.id.day2);
        this.day3 = this.findViewById(R.id.day3);
        this.day4 = this.findViewById(R.id.day4);
        this.day5 = this.findViewById(R.id.day5);
        this.day6 = this.findViewById(R.id.day6);
        this.day7 = this.findViewById(R.id.day7);
        this.tvNowWeek = this.findViewById(R.id.tab_tv_nowweek);

        int weekNow = DateUtil.getWeekNow();
        String temp = String.valueOf(weekNow + 1) + "周";
        this.tvNowWeek.setText(temp);

        this.tvNowWeek.setOnClickListener(v -> {
            DialogSelectWeek dialogSelectWeek = new DialogSelectWeek(getContext(), tvNowWeek) {
                @Override
                void afterClick(int position) {
                    ScheduleView.this.refresh(position);
                }
            };
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
    }

    private void daySetOnClick(RelativeLayout rl, final int day) {
        rl.setOnClickListener(v -> {
            searchIID();
            Intent intentDay = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(Import2Database.START, day);
            bundle.putInt(Import2Database.IID, iID);
            bundle.putString(Import2Database.NAME, "");
            bundle.putString(Import2Database.ROOM, "");
            intentDay.putExtras(bundle);
            intentDay.setClass(getContext(), DIYScheduleActivity.class);
            scheduleInterface.startActivity(intentDay);
        });
    }

    /**
     * 查询iID的值
     */
    private void searchIID() {
        // 查询表中最后一条数据的iID
        DIYCourses forIID = scheduleInterface.findLast();
        if (forIID == null) {
            iID = 1;
        } else {
            iID = forIID.getIId() + 1;
        }
    }

    /**
     * 刷新show页面
     */
    public void refreshScreen() {
        day1.removeAllViews();
        day2.removeAllViews();
        day3.removeAllViews();
        day4.removeAllViews();
        day5.removeAllViews();
        day6.removeAllViews();
        day7.removeAllViews();
    }

    public void refresh(int position) {
        // 清空内容
        refreshScreen();
        // 刷新
        scheduleInterface.refresh(position);
    }

    /**
     * 传入相关数值设置text的参数，并将TextView设置到布局上
     *
     * @param text   显示的内容
     * @param id     资源ID
     * @param start  课程是第几节开始的
     * @param r1     课程在周几
     * @param step   课程是多少小节的
     * @param typeId 课程的类型
     */
    private void setTextView(String text, final int id, int start, RelativeLayout r1, int step, int typeId) {
        final TextView tv1 = new TextView(getContext());
        tv1.setText(text);
        tv1.setTextColor(getContext().getResources().getColor(R.color.white));
        tv1.setTextSize(11);
        tv1.setPadding(0, 0, 0, 0);

        // 设置宽度和高度
        int heightDP;
        if (step <= 0 || step > 4) {
            scheduleInterface.outOfCourse();
            return;
        } else {
            heightDP = 50 * step;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ConvertUtils.dp2px(150), ConvertUtils.dp2px(heightDP)
        );
        int topMargin = ConvertUtils.dp2px(start);
        params.setMargins(0, topMargin, 0, 0);

        tv1.setBackground(getContext().getResources().getDrawable(R.drawable.diytextview));
        // 如果不为零则显示
        switch (typeId) {
            case 0:
                setTVBackground(tv1, R.drawable.diytextview);
                break;
            case 1:
                setTVBackground(tv1, R.drawable.diytextview1);
                break;
            case 2:
                setTVBackground(tv1, R.drawable.diytextview2);
                break;
            case 3:
                setTVBackground(tv1, R.drawable.diytextview3);
                break;
            case 4:
                setTVBackground(tv1, R.drawable.diytextview4);
                break;
            case 5:
                setTVBackground(tv1, R.drawable.diytextview5);
                break;
            case 6:
                setTVBackground(tv1, R.drawable.diytextview6);
                break;
            case 7:
                setTVBackground(tv1, R.drawable.diytextview7);
                break;
            case 8:
                setTVBackground(tv1, R.drawable.diytextview8);
                break;
            case 9:
                setTVBackground(tv1, R.drawable.diytextview9);
                break;
            default:
                setTVBackground(tv1, R.drawable.diytextview);
                break;
        }
        tv1.setLayoutParams(params);
        tv1.setOnClickListener(v -> {
            Dialog showDialog = new DialogShowMessage(getContext(), id, v);
            showDialog.show();
        });
        r1.addView(tv1);
    }

    /**
     * 填充课表
     */
    public void fullSchedule(int tempDay, String tempTxt, int tempID, int start, int step, int tempTypeId) {
        // 如果不为零则显示
        switch (tempDay) {
            case 1:
                setTextView(tempTxt, tempID, start * 50, day1, step, tempTypeId);
                break;
            case 2:
                setTextView(tempTxt, tempID, start * 50, day2, step, tempTypeId);
                break;
            case 3:
                setTextView(tempTxt, tempID, start * 50, day3, step, tempTypeId);
                break;
            case 4:
                setTextView(tempTxt, tempID, start * 50, day4, step, tempTypeId);
                break;
            case 5:
                setTextView(tempTxt, tempID, start * 50, day5, step, tempTypeId);
                break;
            case 6:
                setTextView(tempTxt, tempID, start * 50, day6, step, tempTypeId);
                break;
            case 7:
                setTextView(tempTxt, tempID, start * 50, day7, step, tempTypeId);
                break;
            default:// 希望不会翻车
                LogUtils.i("翻车日常～" + tempTxt);
                break;
        }
    }

    /**
     * 修改TextView的背景
     *
     * @param tv         TextView
     * @param drawableId 资源ID
     */
    private void setTVBackground(TextView tv, int drawableId) {
        tv.setBackground(getContext().getResources().getDrawable(drawableId));
    }

    public void setCurrentWeek() {
        String temp = String.valueOf(DateUtil.getWeekNow() + 1) + "周";
        tvNowWeek.setText(temp);
    }
}
