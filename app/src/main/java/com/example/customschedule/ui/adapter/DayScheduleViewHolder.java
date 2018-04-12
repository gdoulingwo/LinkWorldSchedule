package com.example.customschedule.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.customschedule.R;
import com.github.vipulasri.timelineview.TimelineView;

/**
 * 天表的Holder
 *
 * @author hyt
 * @date 2018/2/26
 */

class DayScheduleViewHolder extends RecyclerView.ViewHolder {

    TextView clsName;
    TextView clsSite;
    TextView clsNumber;
    TimelineView mTimelineView;

    DayScheduleViewHolder(View itemView, int viewType) {
        super(itemView);
        clsName = itemView.findViewById(R.id.text_timeline_clsName);
        clsNumber = itemView.findViewById(R.id.text_timeline_clsNumber);
        clsSite = itemView.findViewById(R.id.text_timeline_clsSite);
        mTimelineView = itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}
