package com.example.customschedule.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;

import com.example.customschedule.R;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.http.bean.DIYDaySchedule;
import com.example.customschedule.http.bean.DIYWeek;
import com.example.customschedule.http.message.RefreshEvent;
import com.example.customschedule.ui.fragment.ScheduleDayFragment;
import com.example.customschedule.ui.fragment.ScheduleWeekFragment;
import com.example.customschedule.ui.fragment.ScheduleWeekRefresh;
import com.example.customschedule.util.DateUtil;
import com.example.customschedule.view.ColorFlipPagerTitleView;
import com.example.customschedule.view.widget.WeekSchedule;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyu
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();
        setDrawerLayout();
        setSP();
        setViewPager();
    }

    private void setSP() {
        ScheduleWeekRefresh.refreshWidget(DateUtil.getWeekNow());
    }

    private void setViewPager() {
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(new ScheduleDayFragment());
        fragments.add(new ScheduleWeekFragment());
        String[] titles = new String[]{"今日", "本周"};

        ViewPager mViewPager = findViewById(R.id.viewpager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), titles, fragments);
        mViewPager.setAdapter(adapter);

        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(20);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.hui));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.black));
                simplePagerTitleView.setOnClickListener(v -> mViewPager.setCurrentItem(index));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 6));
                indicator.setLineWidth(UIUtil.dip2px(context, 10));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(getResources().getColor(R.color.black));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    private void setToolBar() {
        // 设置toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_main:
                    popUpMyOverflow();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void setDrawerLayout() {
        //设置滑动菜单
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //将原有的home隐藏并将menu图片设置为home图标
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.nav_menu);
        }
        mNavigationView.setCheckedItem(R.id.nav_DIYSchedule);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_DIYSchedule:
                    break;
                case R.id.nav_setting:
                    startActivity(new Intent(MainActivity.this, NavSetting.class));
                    break;
                case R.id.nav_importSchedule:
                    startActivity(new Intent(MainActivity.this, ImportScheduleActivity.class));
                default:
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        });
        // 侧边栏沉浸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            //状态栏字体黑色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetAll:
                // 重置课表
                DataSupport.deleteAll(DIYCourses.class);
                DataSupport.deleteAll(DIYWeek.class);
                DataSupport.deleteAll(DIYDaySchedule.class);
                EventBus.getDefault().post(new RefreshEvent());
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void popUpMyOverflow() {
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        // 初始化
        @SuppressLint("InflateParams")
        View popView = getLayoutInflater().inflate(R.layout.popwindow_overflow, null);
        mPopupWindow = new PopupWindow(popView);
        mPopupWindow.setWidth(500);
        mPopupWindow.setHeight(190);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(toolbar, Gravity.END | Gravity.TOP, 20, 0);
        popView.findViewById(R.id.resetAll).setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //刷新桌面插件
        Intent intent = new Intent(MainActivity.this, WeekSchedule.class);
        intent.setAction("refresh");
        sendBroadcast(intent);
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {

        private String[] tabNames;
        private List<Fragment> fragments;

        MainPagerAdapter(FragmentManager fm, String[] tabNames, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
            this.tabNames = tabNames;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
