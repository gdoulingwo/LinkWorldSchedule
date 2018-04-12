package com.example.customschedule.http.bean;

import org.litepal.crud.DataSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hyt
 * @date 2018/2/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DIYCourses extends DataSupport {
    private int iId;
    private String name;
    /**
     * 地点
     */
    private String room;
    /**
     * 星期几
     */
    private int day;
    /**
     * 第几节
     */
    private int start;
    /**
     * 总共几节
     */
    private int step = 0;
    /**
     * 课程的类型
     */
    private int typeId = 0;
}
