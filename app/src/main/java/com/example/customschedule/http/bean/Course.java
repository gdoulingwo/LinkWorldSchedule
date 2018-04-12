package com.example.customschedule.http.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YZune
 * @date 2017/9/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    /**
     * 开始上课节次， 一共几节课，课程编号
     */
    private int start, step, day, startWeek, endWeek, isOdd, id;
    /**
     * 课程名称、上课教室，教师
     */
    private String name, room, teach;
}

