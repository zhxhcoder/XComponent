package com.zhxh.xcomponent;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhxh.xcomponentlib.XPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxh on 2018/8/17
 */
public class GalleryActivity extends AppCompatActivity {

    LayoutInflater inflater;

    private RelativeLayout mRelativeLayout;
    private ViewPager mViewPager;
    private List<View> mImages = new ArrayList<>();
    private XPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        inflater = LayoutInflater.from(this);


        mImages.add(getViewData(0));
        mImages.add(getViewData(1));
        mImages.add(getViewData(2));
        mImages.add(getViewData(3));


        mRelativeLayout = findViewById(R.id.rl_main_content);
        mViewPager = findViewById(R.id.vp_main_pager);
        mViewPagerAdapter = new XPagerAdapter(mImages);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageMargin(30);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setPageTransformer(false, new XPagerAdapter.GalleryTransformer(1f));

        //事件分发，处理页面滑动问题
        mRelativeLayout.setOnTouchListener((v, event) -> mViewPager.dispatchTouchEvent(event));

    }

    private View getViewData(int index) {

        View root = inflater.inflate(R.layout.xview_item, null);

        TextView tv_title = root.findViewById(R.id.tv_title);

        tv_title.setText(index + "标题");

        return root;
    }


}
