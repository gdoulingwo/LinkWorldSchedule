package com.example.customschedule.ui.fragment;

import android.database.Cursor;

import com.blankj.utilcode.util.LogUtils;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.bean.DIYDaySchedule;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * @author wangyu
 * @describe 课程表刷新
 */
public class ScheduleWeekRefresh {
    /**
     * 保存数据
     */
    private void setDaySchedule(String clsName, String clsSite,
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

    /**
     * 刷新课程表
     *
     * @param position    位置
     * @param doSomething 函数接口
     */
    void refresh(int position, DoSomething doSomething) {
        // 清空天表
        DataSupport.deleteAll(DIYDaySchedule.class);
        // 重新加载
        List<DIYCourses> listWidgetlist = DataSupport.findAll(DIYCourses.class);
        for (int i = 0; i < listWidgetlist.size(); i++) {
            DIYCourses tempCourse = listWidgetlist.get(i);
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
            if (show == 0) {
                continue;
            }

            doSomething.settingSchedule(tempCourse);
        }
    }

    public void refreshWidget(int position) {
        refresh(position, ScheduleWeekRefresh.this::settingSchedule);
    }

    /**
     * 设置课程表
     *
     * @param tempCourse 课程
     */
    private void settingSchedule(DIYCourses tempCourse) {
        //如果不为零则显示
        if (tempCourse.getDay() > 0 && tempCourse.getDay() <= 7) {
            setDaySchedule(tempCourse.getName(), tempCourse.getRoom(),
                    tempCourse.getStart(), tempCourse.getStep(),
                    tempCourse.getIId(), tempCourse.getDay());
        }
    }

    @FunctionalInterface
    public interface DoSomething {
        /**
         * 填充课表
         *
         * @param diyCourses 课程
         */
        void settingSchedule(DIYCourses diyCourses);
    }
}
