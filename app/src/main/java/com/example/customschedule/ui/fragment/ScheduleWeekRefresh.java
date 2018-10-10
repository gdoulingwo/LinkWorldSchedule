package com.example.customschedule.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.customschedule.R;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.bean.DIYDaySchedule;
import com.example.customschedule.http.message.RefreshEvent;
import com.example.customschedule.util.DateUtil;
import com.example.customschedule.view.Dialog_ShowMessage;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author hyt
 * @date 2018/2/24
 */
public class ScheduleWeekRefresh {
    private WeakReference<Context> contextWeakReference;

    private RelativeLayout day1;
    private RelativeLayout day2;
    private RelativeLayout day3;
    private RelativeLayout day4;
    private RelativeLayout day5;
    private RelativeLayout day6;
    private RelativeLayout day7;

    public ScheduleWeekRefresh(Context context, View view) {
        this.contextWeakReference = new WeakReference<>(context);

        day1 = view.findViewById(R.id.day1);
        day2 = view.findViewById(R.id.day2);
        day3 = view.findViewById(R.id.day3);
        day4 = view.findViewById(R.id.day4);
        day5 = view.findViewById(R.id.day5);
        day6 = view.findViewById(R.id.day6);
        day7 = view.findViewById(R.id.day7);
    }

    /**
     * 保存数据
     */
    private static void setDaySchedule(String clsName, String clsSite,
                                       int clsStartNumber, int clsCountNumber,
                                       int iID, int day) {
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

    public static void refreshWidget(int position) {
        //清空天表
        DataSupport.deleteAll(DIYDaySchedule.class);
        //重新加载
        List<DIYCourses> listWidgetlist;
        listWidgetlist = DataSupport.findAll(DIYCourses.class);
        for (int i = 0; i < listWidgetlist.size(); i++) {
            DIYCourses tempCourse = listWidgetlist.get(i);
            String tempClsName = tempCourse.getName();
            String tempClsSite = tempCourse.getRoom();
            int tempDay = tempCourse.getDay();
            int tempNumber = tempCourse.getStart();
            int tempCountNumber = tempCourse.getStep();
            // iId
            int tempID = tempCourse.getIId();
            //用原生查询查，先利用iID查询相应课程的position相对应的周是否为0
            Cursor cursor = DataSupport.findBySQL("select * from DIYWeek where iid = ?", String.valueOf(tempID));
            if (!cursor.moveToPosition(0)) {
                System.out.println("moveToPosition return fails, maybe table not created!!!");
                return;
            }
            cursor.moveToFirst();

            String getWeek = "week" + String.valueOf(position + 1);
            // getColumnIndex获取对应列的index值
            int show = cursor.getInt(cursor.getColumnIndex(getWeek));
            //如果不为零则显示
            if (show != 0 && tempDay > 0 && tempDay <= 7) {
                setDaySchedule(tempClsName, tempClsSite, tempNumber, tempCountNumber, tempID, tempDay);
            }
        }
    }

    public void refresh(int position) {
        //清除所有view
        day1.removeAllViews();
        day2.removeAllViews();
        day3.removeAllViews();
        day4.removeAllViews();
        day5.removeAllViews();
        day6.removeAllViews();
        day7.removeAllViews();
        //清空天表
        DataSupport.deleteAll(DIYDaySchedule.class);
        // 重新加载
        List<DIYCourses> listDiyCourses = DataSupport.findAll(DIYCourses.class);
        for (int i = 0; i < listDiyCourses.size(); i++) {
            DIYCourses tempCourse = listDiyCourses.get(i);

            String tempClsName = tempCourse.getName();
            String tempClsSite = tempCourse.getRoom();
            int tempDay = tempCourse.getDay();
            int start = tempCourse.getStart();
            int step = tempCourse.getStep();
            int tempTypeId = tempCourse.getTypeId();
            // iId
            int tempID = tempCourse.getIId();
            // 此处可以进行优化，将课程名和地点名集合到setTextView方法中，可以顺带优化输入的逻辑
            tempClsName = tempClsName.length() > 7 ? tempClsName.substring(0, 7).concat("...") : tempClsName;
            String tempTxt = tempClsName + "@" + tempClsSite;
            //用原生查询查
            //先利用iID查询相应课程的position相对应的周是否为0
            Cursor cursor = DataSupport.findBySQL("select * from DIYWeek where iId = ?", String.valueOf(tempID));
            if (!cursor.moveToPosition(0)) {
                LogUtils.i("moveToPosition return fails, maybe table not created!!!");
                return;
            }
            cursor.moveToFirst();
            String getWeek = "week" + String.valueOf(position + 1);
            // getColumnIndex获取对应列的index值
            int show = cursor.getInt(cursor.getColumnIndex(getWeek));
            if (show == 0) {
                continue;
            }
            LogUtils.i(tempClsName, tempClsSite, start, step, "tempID -> " + tempID,
                    "tempDay -> " + tempDay);
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

            // 重置天表
            if (tempDay == DateUtil.getDayIndexOnWeek()) {
                setDaySchedule(tempClsName, tempClsSite, start, step, tempID, tempDay);
                EventBus.getDefault().post(new RefreshEvent(true));
            }
        }
    }

    /**
     * 传入相关数值设置text的参数，并将TextView设置到布局上
     *
     * @param text   显示的内容
     * @param id
     * @param start  课程是第几节开始的
     * @param r1     课程在周几
     * @param step   课程是多少小节的
     * @param typeId 课程的类型
     */
    private void setTextView(String text, final int id, int start,
                             RelativeLayout r1, int step, int typeId) {
        final TextView tv1 = new TextView(contextWeakReference.get());
        tv1.setText(text);
        tv1.setTextColor(contextWeakReference.get().getResources().getColor(R.color.white));
        tv1.setTextSize(11);

        // 设置宽度和高度
        int heightDP;
        if (step <= 0 || step > 4) {
            //课程左边比右边大的错误处理方法
            ToastUtils.showShort("课程节数设置错误");
            return;
        } else {
            heightDP = 50 * step;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ConvertUtils.dp2px(150), ConvertUtils.dp2px(heightDP)
        );
        int topMargin = ConvertUtils.dp2px(start);
        params.setMargins(0, topMargin, 0, 0);

        tv1.setBackground(contextWeakReference.get().getResources().getDrawable(R.drawable.diytextview));
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
            Dialog showDialog = new Dialog_ShowMessage(contextWeakReference.get(), id, v);
            showDialog.show();
        });
        r1.addView(tv1);
    }

    private void setTVBackground(TextView tv, int drawableId) {
        tv.setBackground(contextWeakReference.get().getResources().getDrawable(drawableId));
    }
}
