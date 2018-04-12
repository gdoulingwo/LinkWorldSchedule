package com.example.customschedule.http.bean;

import org.litepal.crud.DataSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hyt
 * @date 2018/2/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DIYDaySchedule extends DataSupport {
    private int iId;
    /**
     * 周几
     */
    private int day;
    /**
     * 课程名
     */
    private String name;
    /**
     * 上课位置
     */
    private String room;
    /**
     * 课程开始的周数
     */
    private int startWeek;
    /**
     * 上课节数
     */
    private int step;
}
