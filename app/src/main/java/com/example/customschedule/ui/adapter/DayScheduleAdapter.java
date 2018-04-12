package com.example.customschedule.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.customschedule.R;
import com.example.customschedule.util.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.HashMap;
import java.util.List;

/**
 * @author hyt
 * @date 2018/2/26
 */

public class DayScheduleAdapter extends RecyclerView.Adapter<DayScheduleViewHolder> {

    private Context mContext;
    private List<HashMap<String, Object>> listItem;

    public DayScheduleAdapter(List<HashMap<String, Object>> listItem) {
        this.listItem = listItem;
    }


    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @NonNull
    @Override
    public DayScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        //因为只要用到垂直布局，不需要判断
        View view = mLayoutInflater.inflate(R.layout.item_tab_dayschedule, parent, false);
        return new DayScheduleViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull DayScheduleViewHolder holder, int position) {
        //获取数据

        //状态直接用Activity的
        //TODO 这里可以修改TimeLine的圆点的颜色
        holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext,
                R.drawable.ic_marker_active, R.color.black));
        //显示内容
        holder.clsName.setText((String) listItem.get(position).get("name"));
        holder.clsNumber.setText((String) listItem.get(position).get("clsNumber"));
        holder.clsSite.setText((String) listItem.get(position).get("room"));
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }
}
