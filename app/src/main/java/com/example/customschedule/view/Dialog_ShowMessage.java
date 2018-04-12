package com.example.customschedule.view;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.example.customschedule.R;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.ui.DIYScheduleActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * @author hyt
 * @date 2018/2/6
 */

public class Dialog_ShowMessage extends Dialog {
    private int iID;
    private Context context;
    private String clsName;
    private String clsSite;
    private String clsNumber;
    private TextView tv_clsName;
    private TextView tv_clsSite;
    private TextView tv_clsNumber;
    private TextView tv_setting;

    public Dialog_ShowMessage(Context context, int iID, View v) {
        super(context);
        this.iID = iID;
        this.context = context;
    }

    @TargetApi(16)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_showmessage);
        //数据查询
        List<DIYCourses> listDiycourses = DataSupport.where("iId = ?", String.valueOf(iID)).find(DIYCourses.class);
        // 因为对应的iID之可能有一个，直接获取相关数据行
        final DIYCourses diyCourses = listDiycourses.get(0);

        // 获取课程名称
        tv_clsName = findViewById(R.id.dialog_tv_ClsName);
        clsName = diyCourses.getName();
        String tempClsName = clsName;
        tv_clsName.setText(tempClsName);
        // 获取教室
        tv_clsSite = findViewById(R.id.dialog_tv_clsSite);
        clsSite = diyCourses.getRoom();
        String tempClsSite = "教室" + "  " + clsSite;
        tv_clsSite.setText(tempClsSite);

        // 获取所在节数（可有可无）
        tv_clsNumber = findViewById(R.id.dialog_tv_clsNumber);
        int startNumber = diyCourses.getStart();
        int countNumber = diyCourses.getStep();
        int start = startNumber + 1;
        int end = startNumber + countNumber;
        clsNumber = String.valueOf(start) + "—" + String.valueOf(end) + "节";
        tv_clsNumber.setText(clsNumber);

        // 生成25个textView
        RelativeLayout rlWeeks = findViewById(R.id.dialog_table_weeks);
        for (int i = 1; i < 26; i++) {
            TextView tv = new TextView(getContext());
            String week = "week" + String.valueOf(i);
            Cursor cursor = DataSupport.findBySQL("select * from DIYWeek where iId = ?", String.valueOf(iID));
            cursor.moveToFirst();
            int show = cursor.getInt(cursor.getColumnIndex(week));
            //show=0的时候无法为text赋值，直接赋值为当前的i
            if (show != 0) {
                int top = show / 5;
                if (show % 5 == 0) {
                    top = show / 5 - 1;
                }
                int left = show % 5 - 1;
                if (left == -1) {
                    left = 4;
                }

                int margintop = ConvertUtils.dp2px(top * 40);
                int marginleft = ConvertUtils.dp2px(left * 50);

                //运用param方法对tv进行生成
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(marginleft, margintop, 0, 0);
                tv.setLayoutParams(params);
                tv.setBackground(getContext().getResources().getDrawable(R.drawable.dialog_message_week));
            } else {
                show = i;
                int top = show / 5;
                if (show % 5 == 0) {
                    top = show / 5 - 1;
                }
                int left = show % 5 - 1;
                if (left == -1) {
                    left = 4;
                }
                int margintop = ConvertUtils.dp2px(top * 40);
                int marginleft = ConvertUtils.dp2px(left * 50);
                //运用param方法对tv进行生成
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(marginleft, margintop, 0, 0);
                tv.setLayoutParams(params);
                tv.setBackground(getContext().getResources().getDrawable(R.drawable.dialog_message_week0));
            }
            tv.setText(String.valueOf(show));
            tv.setGravity(17);
            rlWeeks.addView(tv);
        }

        // 编辑按钮
        tv_setting = findViewById(R.id.dialog_tv_setting);
        tv_setting.setOnClickListener(v -> {
            Dialog_ShowMessage.this.dismiss();
            int day = diyCourses.getDay() - 1;
            int iID = diyCourses.getIId();
            Intent intentEditor = new Intent();
            Bundle bundleEditor = new Bundle();
            bundleEditor.putInt("start", day);
            bundleEditor.putInt("iId", iID);
            bundleEditor.putString("name", clsName);
            bundleEditor.putString("room", clsSite);
            intentEditor.putExtras(bundleEditor);
            intentEditor.setClass(context, DIYScheduleActivity.class);
            getContext().startActivity(intentEditor);
        });
    }
}
