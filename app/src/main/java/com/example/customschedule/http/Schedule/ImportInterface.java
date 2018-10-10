package com.example.customschedule.http.Schedule;

import android.content.ContentValues;
import android.graphics.Bitmap;

/**
 * @author wangyu
 * @date 10/9/18
 * @describe 课表导入接口
 */

public interface ImportInterface {
    /**
     * 加载验证码
     *
     * @param response 验证码图片
     */
    void loadVerificationCodeSuccess(Bitmap response);

    /**
     * 加载验证码失败
     *
     * @param e 异常
     */
    default void loadVerificationCodeFailure(Exception e) {

    }

    /**
     * 加载课表失败
     */
    default void loadScheduleFailure() {

    }

    /**
     * 解析课表失败
     */
    default void analyzeScheduleFailure() {

    }

    /**
     * 解析课表成功
     */
    void analyzeScheduleSuccess();

    /**
     * 保存课表数据
     *
     * @param key   key
     * @param value value
     */
    default void saveScheduleData(String key, String value) {

    }

    /**
     * 保存课表数据到SQLite
     *
     * @param start      开始节数
     * @param step       多少小节
     * @param day        哪一天
     * @param txtClsName 课程名称
     * @param txtClsSite 教室位置
     */
    default void save2DB(int start, int step, int day, String txtClsName, String txtClsSite) {
    }

    /**
     * 返回表的id
     */
    default int getIID() {
        return 0;
    }

    /**
     * 设置表的id
     */
    default void setIID(int iid) {

    }

    /**
     * 向课表插入数据
     *
     * @param key   key
     * @param value value
     */
    default void insertData(String key, ContentValues value) {

    }
}
