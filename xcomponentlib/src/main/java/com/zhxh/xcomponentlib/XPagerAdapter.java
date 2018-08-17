package com.zhxh.xcomponentlib;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhxh.xcomponentlib.utils.DensityUtil;

/**
 * Created by zhxh on 2018/8/17
 */
public final class XPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mImages;

    public XPagerAdapter(Context context, int[] images) {
        this.mContext = context;
        this.mImages = images;
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(DensityUtil.dip2px(mContext, 200), DensityUtil.dip2px(mContext, 400)));
        imageView.setImageResource(mImages[position]);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public static class GalleryTransformer implements ViewPager.PageTransformer {

        private static final float MAX_ALPHA = 0.5f;
        private static final float MAX_SCALE = 0.9f;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
                //不可见区域
                page.setAlpha(MAX_ALPHA);
                page.setScaleX(MAX_SCALE);
                page.setScaleY(MAX_SCALE);
            } else {
                //透明度效果
                if (position <= 0) {
                    //pos区域[-1,0)
                    page.setAlpha(MAX_ALPHA + MAX_ALPHA * (1 + position));
                } else {
                    //pos区域[0,1]
                    page.setAlpha(MAX_ALPHA + MAX_ALPHA * (1 - position));
                }
                //缩放效果
                float scale = Math.max(MAX_SCALE, 1 - Math.abs(position));
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
        }
    }

    public static class CardTransformer implements ViewPager.PageTransformer {

        private int mOffset = 60;

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
