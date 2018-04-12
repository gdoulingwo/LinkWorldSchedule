package com.example.customschedule.view.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.customschedule.MyApplication;
import com.example.customschedule.R;
import com.example.customschedule.http.bean.DIYDaySchedule;
import com.example.customschedule.ui.fragment.ScheduleWeekRefresh;
import com.example.customschedule.util.DateUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyt
 * @date 2018/3/1
 */

public class WeekScheduleFactory implements RemoteViewsService.RemoteViewsFactory {

    private static List<DIYDaySchedule> List_day1 = new ArrayList<>();
    private static List<DIYDaySchedule> List_day2 = new ArrayList<>();
    private static List<DIYDaySchedule> List_day3 = new ArrayList<>();
    private static List<DIYDaySchedule> List_day4 = new ArrayList<>();
    private static List<DIYDaySchedule> List_day5 = new ArrayList<>();
    private static List<DIYDaySchedule> List_day6 = new ArrayList<>();
    private static List<DIYDaySchedule> List_day7 = new ArrayList<>();
    private Context context;

    WeekScheduleFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    private List<DIYDaySchedule> getData(String dayId) {
        return DataSupport.where("day = ?", dayId).order("startWeek").find(DIYDaySchedule.class);
    }

    private void clearData() {
        List_day1.clear();
        List_day2.clear();
        List_day3.clear();
        List_day4.clear();
        List_day5.clear();
        List_day6.clear();
        List_day7.clear();
    }

    private void initData() {
        clearData();

        List_day1 = getData("1");
        List_day2 = getData("2");
        List_day3 = getData("3");
        List_day4 = getData("4");
        List_day5 = getData("5");
        List_day6 = getData("6");
        List_day7 = getData("7");
    }

    /**
     * 当调用notifyAppWidgetViewDataChanged方法时，触发这个方法
     * 例如：MyRemoteViewsFactory.notifyAppWidgetViewDataChanged();
     */
    @Override
    public void onDataSetChanged() {
        ScheduleWeekRefresh.refreshWidget(DateUtil.getWeekNow());
        initData();
    }

    @Override
    public void onDestroy() {
        clearData();
    }

    @Override
    public int getCount() {
        return 1;
    }


    /**
     * 创建并且填充，在指定索引位置显示的View，这个和BaseAdapter的getView类似
     */
    @Override
    public RemoteViews getViewAt(int position) {
        //最终remoteView
        RemoteViews rvFinal = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_list);
        rvFinal.addView(R.id.widget_week_ll_period, setPeriod());

        rvFinal.addView(R.id.widget_week_ll_day1, setListItemView(List_day1));
        rvFinal.addView(R.id.widget_week_ll_day2, setListItemView(List_day2));
        rvFinal.addView(R.id.widget_week_ll_day3, setListItemView(List_day3));
        rvFinal.addView(R.id.widget_week_ll_day4, setListItemView(List_day4));
        rvFinal.addView(R.id.widget_week_ll_day5, setListItemView(List_day5));
        rvFinal.addView(R.id.widget_week_ll_day6, setListItemView(List_day6));
        rvFinal.addView(R.id.widget_week_ll_day7, setListItemView(List_day7));

        return rvFinal;
    }

    /**
     * 显示一个"加载"View。返回null的时候将使用默认的View
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * 不同View定义的数量。默认为1（本人一直在使用默认值）
     * ps:此处定义的是item的总量，也就是你的子布局有多少种
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 如果每个项提供的ID是稳定的，即她们不会在运行时改变，就返回true（没用过。。。）
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    private RemoteViews setListItemView(List<DIYDaySchedule> listDayTemp) {
        int pastNumber = 0;

        RemoteViews result = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_day);
        for (int i = 0; i < listDayTemp.size(); i++) {
            int clsStartNumber = listDayTemp.get(i).getStartWeek();
            int clsCountNumber = listDayTemp.get(i).getStep();
            String clsName = listDayTemp.get(i).getName();
            String clsSite = listDayTemp.get(i).getRoom();
            // 填充与上个TextView之间的空白
            int isNull = clsStartNumber - pastNumber;
            if (isNull != 0) {
                result.addView(R.id.widget_week_ll_day, isNull(isNull));
            }
            pastNumber = clsStartNumber + clsCountNumber;

            String content = clsName + "@" + clsSite;
            RemoteViews itemTv = tvType(clsCountNumber, content);
            result.addView(R.id.widget_week_ll_day, itemTv);
        }

        return result;
    }

    private RemoteViews tvType(int count, String content) {
        // 默认为2
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_item2);
        rv.setTextViewText(R.id.widget_week_tv_itme_1, "出错");
        if (count == 1) {
            rv = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_item1);
            rv.setTextViewText(R.id.widget_week_tv_itme_1, content);
            return rv;
        }
        if (count == 2) {
            rv = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_item2);
            rv.setTextViewText(R.id.widget_week_tv_itme_2, content);
            return rv;
        }
        if (count == 3) {
            rv = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_item3);
            rv.setTextViewText(R.id.widget_week_tv_itme_3, content);
            return rv;
        }
        if (count == 4) {
            rv = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_item4);
            rv.setTextViewText(R.id.widget_week_tv_itme_4, content);
            return rv;
        }
        return rv;
    }

    private RemoteViews isNull(int count) {

        RemoteViews result = new RemoteViews(context.getPackageName(), R.layout.widget_group_itemnull);
        RemoteViews nullItem = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_itemnull);

        for (int i = 0; i < count; i++) {
            result.addView(R.id.widget_group_itemnull, nullItem);
        }
        return result;
    }

    private RemoteViews setPeriod() {
        RemoteViews result = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_day);
        for (int i = 0; i < 12; i++) {
            RemoteViews rvPeriodItem = new RemoteViews(context.getPackageName(), R.layout.widget_weekschedule_period_item);
            rvPeriodItem.setTextViewText(R.id.widget_period, String.valueOf(i + 1));
            rvPeriodItem.setTextColor(R.id.widget_period, MyApplication.getContext().getResources().getColor(R.color.widget_table_textColor));
            result.addView(R.id.widget_week_ll_day, rvPeriodItem);
        }
        return result;
    }
}
