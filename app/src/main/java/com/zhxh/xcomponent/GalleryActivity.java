package com.zhxh.xcomponent;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.zhxh.xcomponentlib.XPagerAdapter;

/**
 * Created by zhxh on 2018/8/17
 */
public class GalleryActivity extends AppCompatActivity {
    private RelativeLayout mRelativeLayout;
    private ViewPager mViewPager;
    private int[] mImages = {R.mipmap.icon_bg_1, R.mipmap.icon_bg_2, R.mipmap.icon_bg_3, R.mipmap.icon_bg_1, R.mipmap.icon_bg_2, R.mipmap.icon_bg_3};
    private XPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mRelativeLayout = findViewById(R.id.rl_main_content);
        mViewPager = findViewById(R.id.vp_main_pager);
        mViewPagerAdapter = new XPagerAdapter(this, mImages);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageMargin(30);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setPageTransformer(false, new XPagerAdapter.GalleryTransformer());

        //事件分发，处理页面滑动问题
        mRelativeLayout.setOnTouchListener((v, event) -> mViewPager.dispatchTouchEvent(event));

    }

}
