package com.example.customschedule.ui;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.customschedule.R;
import com.example.customschedule.http.Schedule.Import2Database;
import com.example.customschedule.http.Schedule.ImportInterface;
import com.example.customschedule.http.Schedule.ImportSchedule;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.bean.DIYWeek;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;

/**
 * 从官网爬取数据
 *
 * @author wangyu
 */
public class ImportScheduleActivity extends AppCompatActivity {

    private ImageView secretImage;
    private EditText secretCode;
    private EditText editAccount;
    private EditText editPassword;
    private ImportSchedule importSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_schedule);

        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        importSchedule = new ImportSchedule(this, new ImportInterface() {
            private WeakReference<Context> contextWeakReference = new WeakReference<>(ImportScheduleActivity.this);
            private Import2Database import2Database = new Import2Database(contextWeakReference.get());

            @Override
            public void loadVerificationCodeSuccess(Bitmap response) {
                secretImage.setImageBitmap(null);
                secretImage.setImageBitmap(response);
            }

            @Override
            public void loadVerificationCodeFailure(Exception e) {
                e.printStackTrace();
                LogUtils.i("onError" + e.getMessage());
            }

            @Override
            public void loadScheduleFailure() {
                ToastUtils.showShort(ImportSchedule.ERROR);
            }

            @Override
            public void analyzeScheduleSuccess() {
                ToastUtils.showShort("导入完成");
                ImportScheduleActivity.this.finish();
            }

            @Override
            public void save2DB(int start, int step, int day, String txtClsName, String txtClsSite) {
                import2Database.save(start, step, day, txtClsName, txtClsSite);
            }

            @Override
            public int getIID() {
                return import2Database.getIId();
            }

            @Override
            public void insertData(String key, ContentValues values) {
                import2Database.insertData(Import2Database.DIY_WEEK, values);
            }

            @Override
            public void setIID(int iid) {
                import2Database.setIId(iid);
            }
        });
    }

    private void initView() {
        editAccount = findViewById(R.id.edit_account);
        editPassword = findViewById(R.id.edit_password);
        secretCode = findViewById(R.id.txtsecretcode);

        secretImage = findViewById(R.id.secretImage);
        secretImage.setOnClickListener(v -> {
            LogUtils.i("点击了图片");
            importSchedule.initImage();
        });

        findViewById(R.id.demo_sure).setOnClickListener(v -> {
            //清空数据
            DataSupport.deleteAll(DIYCourses.class);
            DataSupport.deleteAll(DIYWeek.class);
            if (isAnyOneNull()) {
                ToastUtils.showShort(ImportSchedule.ERROR);
            } else {
                importSchedule.setStrId(editAccount.getText().toString());
                importSchedule.setStrPassword(editPassword.getText().toString());
                importSchedule.setTxtSecretCode(secretCode.getText().toString());
                importSchedule.getScheduleFromWeb();
            }
        });
    }

    private boolean isAnyOneNull() {
        return importSchedule.isNull(editAccount) ||
                importSchedule.isNull(editPassword) ||
                importSchedule.isNull(secretCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
