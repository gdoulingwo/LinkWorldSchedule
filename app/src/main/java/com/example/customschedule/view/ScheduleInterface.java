package com.example.customschedule.view;

import android.content.Intent;

import com.blankj.utilcode.util.ToastUtils;
import com.example.customschedule.http.bean.DIYCourses;

/**
 * @author wangyu
 * @date 10/10/18
 * @describe TODO
 */

public interface ScheduleInterface {
    /**
     * 课程节数设置错误
     */
    default void outOfCourse() {
        //课程左边比右边大的错误处理方法
        ToastUtils.showShort("课程节数设置错误");
    }

    /**
     * 刷新课表
     *
     * @param position 课表位置
     */
    void refresh(int position);

    /**
     * 查找表中最后一条数据
     *
     * @return 课表数据
     */
    DIYCourses findLast();

    /**
     * 启动
     *
     * @param intentDay Intent
     */
    void startActivity(Intent intentDay);
}
