package com.example.customschedule.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.customschedule.MyApplication;
import com.example.customschedule.util.DateUtil;
import com.example.customschedule.view.widget.WeekSchedule;

public class RefreshWidget extends Service {
    /**
     * 初始没3个小时刷新一次
     */
    int second = 3 * 60 * 60 * 1000;

    public RefreshWidget() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent refreshWidget = new Intent(MyApplication.getContext(), WeekSchedule.class);
        refreshWidget.setAction("refresh");
        sendBroadcast(refreshWidget);


        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //识别当前是周一则将刷新时间改为每周刷新一次，不然每天刷新一次
        if (DateUtil.getDayIndexOnWeek() == 1) {
            second = 7 * 24 * 60 * 60 * 1000;
        }
        long triggerAtTime = SystemClock.elapsedRealtime() + second;
        Intent i = new Intent(this, RefreshWidget.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        assert manager != null;
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
