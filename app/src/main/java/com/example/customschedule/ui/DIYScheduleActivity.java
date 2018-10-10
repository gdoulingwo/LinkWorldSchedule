package com.example.customschedule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.blankj.utilcode.util.ToastUtils;
import com.example.customschedule.R;
import com.example.customschedule.http.Schedule.Import2Database;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.bean.DIYWeek;
import com.example.customschedule.view.DialogChooseWeek;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.LinkagePicker;

/**
 * 修改单节课的详细信息
 *
 * @author wangyu
 */
public class DIYScheduleActivity extends AppCompatActivity {
    List<DIYCourses> listDiyCourses = new ArrayList<>();

    /**
     * 名称
     */
    private String txtClsName;
    /**
     * 地点
     */
    private String txtClsSite;
    /**
     * 星期几
     */
    private int txtDay;
    /**
     * 节数
     */
    private int txtIndex;
    /**
     * 默认每一大节由两小节课构成
     */
    private int txtCountNumber = 2;
    /**
     * 外键
     */
    private int iID;

    private DialogChooseWeek showdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diy_schedule_setting);

        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initView(initData());
    }

    private int initData() {
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        assert b != null;
        int defaultDay = b.getInt(Import2Database.START);
        iID = b.getInt(Import2Database.IID);
        txtClsName = b.getString(Import2Database.NAME);
        txtClsSite = b.getString(Import2Database.ROOM);

        return defaultDay;
    }

    private void initView(int defaultDay) {
        final EditText clsName = findViewById(R.id.tv_ClsName);
        final EditText clsSite = findViewById(R.id.tv_ClsSite);
        clsName.setText(txtClsName);
        clsSite.setText(txtClsSite);
        // 周数选择
        findViewById(R.id.showdialog).setOnClickListener(v -> {
            // 同时传入iID
            showdialog = new DialogChooseWeek(DIYScheduleActivity.this, String.valueOf(iID));
            showdialog.show();
        });

        //获取星期几的值
        Spinner spinnerDay = findViewById(R.id.spinner_day);
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] days = getResources().getStringArray(R.array.day);
                String tempDay = days[position];
                switch (tempDay) {
                    case "周一":
                        txtDay = 1;
                        break;
                    case "周二":
                        txtDay = 2;
                        break;
                    case "周三":
                        txtDay = 3;
                        break;
                    case "周四":
                        txtDay = 4;
                        break;
                    case "周五":
                        txtDay = 5;
                        break;
                    case "周六":
                        txtDay = 6;
                        break;
                    case "周日":
                        txtDay = 7;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                txtDay = 1;
            }
        });
        //设置星期默认值
        spinnerDay.setSelection(defaultDay, true);

        /*
         * region 确定按钮设置
         * 通过iID查询对应的数据是否存在判定用户是否保存了周数
         * 如果周数没有设置则提示用户进行设置
         */
        findViewById(R.id.bt_confirm).setOnClickListener(v -> {
            List<DIYWeek> searchID = DataSupport.where("iId = ?", String.valueOf(iID)).find(DIYWeek.class);
            List<DIYCourses> diyCoursesUpdate = DataSupport.where("iId = ?", String.valueOf(iID)).find(DIYCourses.class);
            if (searchID.size() == 0) {
                ToastUtils.showShort("周数没有设置，请设置周数");
            } else {
                //获取课程名，上课地点，开始周数
                txtClsName = clsName.getText().toString();
                txtClsSite = clsSite.getText().toString();
                if (txtCountNumber != 0) {
                    DIYCourses diyCourse = new DIYCourses();
                    diyCourse.setName(txtClsName);
                    diyCourse.setRoom(txtClsSite);
                    diyCourse.setDay(txtDay);
                    diyCourse.setStep(txtCountNumber);
                    diyCourse.setStart(txtIndex);
                    diyCourse.setTypeId(3);
                    diyCourse.setIId(iID);
                    //不为空则存在通iID数据，直接进行修改
                    if (diyCoursesUpdate.size() != 0) {
                        diyCourse.updateAll("iId = ?", String.valueOf(iID));
                    } else {
                        //为空则不在，直接新建数据与
                        diyCourse.save();
                        ToastUtils.showShort(diyCourse.save() ? "保存成功" : "保存失败");
                        listDiyCourses.add(diyCourse);
                    }
                    finish();
                }
            }
        });
        // endregion
        findViewById(R.id.diy_setting_btn_cancel).setOnClickListener(v -> {
            List<DIYCourses> diyCoursesDelete = DataSupport.where("iId = ?", String.valueOf(iID)).find(DIYCourses.class);
            List<DIYWeek> diyWeeksDelete = DataSupport.where("iId = ?", String.valueOf(iID)).find(DIYWeek.class);
            if (diyCoursesDelete.size() != 0 || diyWeeksDelete.size() != 0) {
                // courses可能没有值
                // 清除原有的值
                DataSupport.deleteAll(DIYWeek.class, "iId = ?", String.valueOf(iID));
                // 清除原有的值
                DataSupport.deleteAll(DIYCourses.class, "iId = ?", String.valueOf(iID));
            }
            finish();
        });
    }

    public void onLinkagePicker(View view) {
        LinkagePicker.DataProvider provider = new LinkagePicker.DataProvider() {

            @Override
            public boolean isOnlyTwo() {
                return true;
            }

            @NonNull
            @Override
            public List<String> provideFirstData() {
                ArrayList<String> firstList = new ArrayList<>();
                for (int i = 1; i < 13; i++) {
                    firstList.add(String.valueOf(i));
                }
                return firstList;
            }

            @NonNull
            @Override
            public List<String> provideSecondData(int firstIndex) {
                ArrayList<String> secondList = new ArrayList<>();
                for (int i = firstIndex + 1; i < firstIndex + 5 && i < 13; i++) {
                    String str = String.valueOf(i);
                    secondList.add(str);
                }
                return secondList;
            }

            @Nullable
            @Override
            public List<String> provideThirdData(int firstIndex, int secondIndex) {
                return null;
            }

        };
        LinkagePicker picker = new LinkagePicker(this, provider);
        picker.setCycleDisable(true);
        // 设置行间距离
        picker.setLineSpaceMultiplier(2.0f);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerVisible(true);
        picker.setPadding(60);
        picker.setUseWeight(true);
        picker.setLabel("至", null);
        picker.setSelectedIndex(0, 1);
        picker.setContentPadding(10, 10);
        picker.setTextSize(15);
        picker.setTitleText("节数选择");
        picker.setGravity(Gravity.CENTER);

        picker.setBackgroundColor(getResources().getColor(R.color.dialog_message_table));
        picker.setDividerColor(getResources().getColor(R.color.dialog_message_setting));
        picker.setSubmitTextColor(getResources().getColor(R.color.dialog_message_setting));
        picker.setTopLineColor(getResources().getColor(R.color.dialog_message_setting));
        picker.setTextColor(getResources().getColor(R.color.dialog_message_setting));
        picker.setCancelTextColor(getResources().getColor(R.color.dialog_message_setting));
        picker.setTitleTextColor(getResources().getColor(R.color.dialog_message_clsName));

        picker.setOnStringPickListener(new LinkagePicker.OnStringPickListener() {
            @Override
            public void onPicked(String first, String second, String third) {
                txtIndex = Integer.parseInt(first) - 1;
                int tempTxtCountNumber = Integer.parseInt(second) - Integer.parseInt(first);
                txtCountNumber = tempTxtCountNumber + 1;
            }
        });
        picker.show();
    }
}
