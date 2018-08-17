package com.zhxh.xcomponentlib;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhxh on 2018/8/17
 */
public final class XPagerAdapter extends PagerAdapter {

    private List<View> views;

    public XPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public static class GalleryTransformer implements ViewPager.PageTransformer {

        private float alpha = 0.5f;
        private float scale = 0.9f;

        public GalleryTransformer(float alpha, float scale) {
            this.alpha = alpha;
            this.scale = scale;
        }

        public GalleryTransformer(float scale) {
            this.scale = scale;
        }

        public GalleryTransformer() {
        }

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
                //不可见区域
                page.setAlpha(alpha);
                page.setScaleX(scale);
                page.setScaleY(scale);
            } else {
                //透明度效果
                if (position <= 0) {
                    //pos区域[-1,0)
                    page.setAlpha(alpha + alpha * (1 + position));
                } else {
                    //pos区域[0,1]
                    page.setAlpha(alpha + alpha * (1 - position));
                }
                //缩放效果
                float scale = Math.max(this.scale, 1 - Math.abs(position));
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
        }
    }

    public static class CardTransformer implements ViewPager.PageTransformer {

        private int mOffset = 60;

        public CardTransformer(int mOffset) {
            this.mOffset = mOffset;
        }

        public CardTransformer() {
        }

        @Override
        public void transformPage(View page, float position) {

            if (position <= 0) {
                page.setRotation(45 * position);
                page.setTranslationX((page.getWidth() / 2 * position));
            } else {
                //移动X轴坐标，使得卡片在同一坐标
                page.setTranslationX(-position * page.getWidth());
                //缩放卡片并调整位置
                float scale = (page.getWidth() - mOffset * position) / page.getWidth();
                page.setScaleX(scale);
                page.setScaleY(scale);
                //移动Y轴坐标
                page.setTranslationY(position * mOffset);
            }


        }
    }

}
