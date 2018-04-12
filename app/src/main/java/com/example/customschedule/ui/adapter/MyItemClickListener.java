package com.example.customschedule.ui.adapter;

import android.view.View;

/**
 * @author hyt
 * @date 2018/2/24
 */
public interface MyItemClickListener {
    /**
     * 课表Item的点击事件
     *
     * @param view    被点击的View
     * @param postion 点击View的位置
     */
    void onItemClick(View view, int postion);
}
