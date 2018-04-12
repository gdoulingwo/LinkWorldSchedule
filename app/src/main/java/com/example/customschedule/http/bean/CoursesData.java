package com.example.customschedule.http.bean;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wangyu
 * @date 18-3-28
 * @describe TODO
 */
@Data
public class CoursesData {
    /**
     * 学生个人的总课表
     */
    @Setter
    @Getter
    private List<CoursesWeek> coursesWeeks = new LinkedList<>();

    /**
     * 周表
     */
    @Data
    public static class CoursesWeek {
        /**
         * 整周的课表
         */
        @Getter(lazy = true)
        private final List<CourseDay> courseDays = new LinkedList<>();
        /**
         * 第几周
         */
        private int index = 0;

        /**
         * 天表
         */
        @Data
        public static class CourseDay {
            /**
             * 一天的课表
             */
            @Getter(lazy = true)
            private final List<Course> courses = new LinkedList<>();
            /**
             * 周几
             */
            private int index = 0;

            /**
             * 单个课程详情
             */
            @Data
            public static class Course {
                /**
                 * 课程的节数
                 */
                private int index = 0;
                /**
                 * 上课位置
                 */
                private String position = null;
                /**
                 * 课程的名称
                 */
                private String courseName = null;
                /**
                 * 任课老师
                 */
                private String teacher = null;
            }
        }
    }
}
