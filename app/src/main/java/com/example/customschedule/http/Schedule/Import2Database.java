package com.example.customschedule.http.Schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.customschedule.util.MyDatabaseHelper;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangyu
 * @date 10/9/18
 * @describe TODO
 */

public class Import2Database {
    public static final String DIY_WEEK = "DIYWeek";
    public static final String DIY_COURSES = "DIYCourses";
    public static final String TYPE_ID = "typeId";
    public static final String IID = "iId";
    public static final String NAME = "name";
    public static final String ROOM = "room";
    public static final String DAY = "day";
    public static final String START = "start";
    public static final String STEP = "step";
    public static final String WEEK = "week";
    /**
     * 保存课程表课程的颜色类型
     */
    private static final HashMap<String, Integer> TYPE = new HashMap<>();
    private static final String SQL_NAME = "Schedule.db";
    @Setter
    @Getter
    private int iId;
    /**
     * 课程表颜色代码ID
     */
    private int typeId;
    private MyDatabaseHelper dbHelper;

    public Import2Database(Context context) {
        this.typeId = 0;
        this.dbHelper = new MyDatabaseHelper(context, SQL_NAME, null, 3);
    }

    public int save(int start, int step, int day, String txtClsName, String txtClsSite) {
        int id;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IID, iId);
        // 判断类型
        if (!TYPE.containsKey(txtClsName.trim())) {
            TYPE.put(txtClsName, typeId);
            id = typeId;
            values.put(TYPE_ID, typeId);
            typeId++;
        } else {
            id = TYPE.get(txtClsName);
            values.put(TYPE_ID, TYPE.get(txtClsName));
        }
        values.put(NAME, txtClsName);
        values.put(ROOM, txtClsSite);
        // 周几
        values.put(DAY, day);
        // 节数
        values.put(START, start);
        // 几小节
        values.put(STEP, step);
        // 类型
        db.insert(DIY_COURSES, null, values);
        values.clear();

        iId++;
        return id;
    }

    public void insertData(String table, ContentValues values) {
        dbHelper.getWritableDatabase().insert(table, null, values);
    }
}
