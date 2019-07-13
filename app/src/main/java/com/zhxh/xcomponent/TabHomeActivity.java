package com.zhxh.xcomponent;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhxh.xcomponent.dummy.ChartData;
import com.zhxh.xcomponentlib.XPagerTabStrip;
import com.zhxh.xcomponentlib.xtablayout.XTabLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabHomeActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout1;
    private TabLayout tabLayout2;

    private XPagerTabStrip xPagerTabStrip1;
    private XTabLayout xTabLayout1;
    private XTabLayout xTabLayout2;

    private List<String> titleList = new ArrayList<>();
    private List<String> titleList2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.mViewPager);

        tabLayout1 = findViewById(R.id.tabLayout1);
        tabLayout2 = findViewById(R.id.tabLayout2);

        xPagerTabStrip1 = findViewById(R.id.xPagerTabStrip1);
        xTabLayout1 = findViewById(R.id.xTabLayout1);
        xTabLayout2 = findViewById(R.id.xTabLayout2);

        titleList.add("总收益榜");
        titleList.add("本周收益榜");
        titleList.add("榜单");
        titleList.add("收益榜");
        titleList.add("热门收益榜");
        titleList.add("热门榜");

        titleList2.add("热门");
        titleList2.add("热门");
        titleList2.add("热门");
        titleList2.add("热门");
        titleList2.add("热门");

        /*for (int i = 0; i < titleList.size(); i++) {
            //从源码上看这个不起作用 起作用的是getPageTitle
            tabLayout1.addTab(tabLayout1.newTab().setText("tab" + i));
        }*/

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(0);

        tabLayout1.setupWithViewPager(mViewPager);
        tabLayout2.setupWithViewPager(mViewPager);
        xPagerTabStrip1.setViewPager(mViewPager);
        xTabLayout1.setupWithViewPager(mViewPager);
        xTabLayout2.setupWithViewPager(mViewPager);


        tabLayout2.post(() -> {
            //通过反射方法改变
            //setIndicator(tabLayout2, 10, 10);
        });

    }

    @Override
    public void onListFragmentInteraction(ChartData item) {

        Toast.makeText(this,item.getName(),Toast.LENGTH_LONG).show();

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return ItemFragment.newInstance(position % 2 + 1);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }
    }

    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }


    }
}
