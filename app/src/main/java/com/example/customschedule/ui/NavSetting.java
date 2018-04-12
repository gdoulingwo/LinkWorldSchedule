package com.example.customschedule.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.customschedule.R;

/**
 * @author wangyu
 */
public class NavSetting extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_setting);
        Toolbar toolbar = findViewById(R.id.nav_setting_toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("设置");
        //侧边栏沉浸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            //状态栏字体黑色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar.setNavigationIcon(R.drawable.ic_back64);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvOpenagreenment = findViewById(R.id.tv_oppenAgreement);
        tvOpenagreenment.setOnClickListener(v -> {
            Intent openAgreement = new Intent(NavSetting.this, OpenAgreement.class);
            startActivity(openAgreement);
        });

        TextView tvRefresh = findViewById(R.id.widget_refresh);
        tvRefresh.setOnClickListener(v -> {
            Intent intentAbout = new Intent(NavSetting.this, AboutActivity.class);
            startActivity(intentAbout);
        });
    }
}
