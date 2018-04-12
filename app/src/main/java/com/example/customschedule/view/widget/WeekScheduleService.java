package com.example.customschedule.view.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * @author hyt
 * @date 2018/3/1
 */

public class WeekScheduleService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WeekScheduleFactory(this.getApplicationContext());
    }
}
