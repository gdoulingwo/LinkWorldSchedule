package com.example.customschedule.util;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Calendar;

/**
 * @author hyt
 * @date 2018/2/16
 */

public class DateUtil {
    private static final int FINAL_WEEK = 26;
    private static Calendar calendar;

    public static int getDayIndexOnWeek() {
        calendar = Calendar.getInstance();
        int resultWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (resultWeek == 0) {
            resultWeek = 7;
        }
        return resultWeek;
        //注意1代表星期天，其余按顺序推导
    }

    private static int getDayOfYear() {
        calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private static int getWeekOfNow(int firstMonday) {
        int dayOfYear = DateUtil.getDayOfYear();
        int dayOfWeek = DateUtil.getDayIndexOnWeek();
        int mondayOfNowWeek = dayOfYear - (dayOfWeek - 1);
        return (mondayOfNowWeek - firstMonday) / 7 + 1;
    }

    public static int getWeekNow() {
        //读取第一周，如果为零则不进行计算，直接设置为第一周，不为零则进行计算，设置为当前周
        int firstMonday = SPUtils.getInstance().getInt(Constants.FIRST_DAYOF_SEMESTER, 0);
        if (firstMonday != 0) {
            // 防止周数超过25后出现闪退
            if (DateUtil.getWeekOfNow(firstMonday) < FINAL_WEEK) {
                SPUtils.getInstance().put(Constants.WEEK_NOW, DateUtil.getWeekOfNow(firstMonday) - 1);
            } else {
                SPUtils.getInstance().put(Constants.WEEK_NOW, 0);
            }
        }
        //读取当前星期值
        return SPUtils.getInstance().getInt(Constants.WEEK_NOW, 0);
    }

    /**
     * 设置当前学期的第一天
     *
     * @param position 本周的位置
     */
    public static void setFirstDayOfNewTerm(int position) {
        SPUtils.getInstance().put(Constants.WEEK_NOW, position);
        // 此处推算写入第一周的日期
        int dayOfYear = DateUtil.getDayOfYear();
        int dayOfWeek = DateUtil.getDayIndexOnWeek();
        int mondayOfNowWeek = dayOfYear - (dayOfWeek - 1);
        int firstDayOfSemester = mondayOfNowWeek - position * 7;
        // 如果设置错误，强制设置当前周为第一周
        if (firstDayOfSemester < 0) {
            firstDayOfSemester = mondayOfNowWeek;
        }
        //写入当前学期第一天的日期
        SPUtils.getInstance().put(Constants.FIRST_DAYOF_SEMESTER, firstDayOfSemester);
    }

    public static int getWeekFromSP() {
        return SPUtils.getInstance().getInt(Constants.WEEK_NOW, 0);
    }
}
