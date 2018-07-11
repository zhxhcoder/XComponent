package com.zhxh.xcomponent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zhxh.xcomponent.dummy.ChartData;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;


    private List<String> titleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.mViewPager);

        titleList.add("总收益榜");
        titleList.add("本周收益榜");
        titleList.add("榜单");
        titleList.add("收益榜");
        titleList.add("热门收益榜");

        for (int i = 0; i < titleList.size(); i++) {
            //从源码上看这个不起作用 起作用的是getPageTitle
            tabLayout.addTab(tabLayout.newTab().setText("tab" + i));
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(0);

        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onListFragmentInteraction(ChartData item) {

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
}
