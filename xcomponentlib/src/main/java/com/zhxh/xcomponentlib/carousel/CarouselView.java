package com.zhxh.xcomponentlib.carousel;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhxh.xcomponentlib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxh on 2015/10/30.
 */
public class CarouselView extends LinearLayout {

    private LayoutInflater inflater;

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 布局轮播视图
     */
    private LoopViewPager loopViewPager = null;

    /**
     * 滚动布局视图适配器
     */
    private CycleViewAdapter mAdvAdapter;

    /**
     * 布局轮播指示器控件
     */
    private ViewGroup viewGroupContainer;

    /**
     * 布局轮播指示器-个图
     */
    private View singleView = null;
    /**
     *
     * */
    private int highLightPointer;
    private int lowLightPointer = R.drawable.dot;
    /**
     * 是否显示原始大小
     */
    private boolean showPointerAuto = false;
    /**
     * 滚动布局指示器-视图列表
     */
    private View[] viewGroups = null;

    /**
     * 布局滚动当前布局下标
     */
    private int mViewIndex = 0;
    /**
     * 是否布局自动轮播
     */
    private boolean isCarouseAutoPlay = false;

    /**
     * 是否轮播
     */
    private boolean isAuto = true;
    /**
     * 布局自动轮播Task
     */
    private Runnable mViewTimerTask = new Runnable() {

        @Override
        public void run() {
            if (viewGroups != null && isAuto) {
                // 下标等于布局列表长度说明已滚动到最后一张布局,重置下标
                if ((++mViewIndex) == viewGroups.length) {
                    mViewIndex = 0;
                }
                loopViewPager.setCurrentItem(mViewIndex);
            }
        }
    };
    /**
     * 手机密度
     */
    private float mScale;
    private Handler mHandler = new Handler();
    /**
     * 是否是图片
     */
    private boolean isImage = true;

    private PageSelecetListener pageSelecetListener;

    /**
     * @param context
     */
    public CarouselView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);

        mContext = context;
        mScale = getResources().getDisplayMetrics().density;
        inflater.inflate(R.layout.carousel_view, this);
        loopViewPager = (LoopViewPager) findViewById(R.id.carousel_pager);
        loopViewPager.setBoundaryCaching(true);
        loopViewPager.setOnPageChangeListener(new GuidePageChangeListener());
        loopViewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始布局滚动
                        startViewTimerTask();
                        break;
                    default:
                        // 停止布局滚动
                        stopViewTimerTask();
                        break;
                }
                PointF downP = new PointF();
                PointF curP = new PointF();
                int act = event.getAction();
                if (act == MotionEvent.ACTION_DOWN || act == MotionEvent.ACTION_MOVE || act == MotionEvent.ACTION_UP) {
                    ((ViewGroup) v).requestDisallowInterceptTouchEvent(true);
                    if (downP.x == curP.x && downP.y == curP.y) {
                        return false;
                    }
                }
                return false;
            }
        });
        // 滚动布局右下指示器视图
        viewGroupContainer = (ViewGroup) findViewById(R.id.viewGroupContainer);
    }

    /**
     * 装填布局数据
     *
     * @param viewResID         布局url数组
     * @param cycleViewListener
     */
    public void setViewResources(List<Integer> viewResID, List<? extends ICarouselData> datas, CycleViewListener cycleViewListener) {
        isImage = false;

        // 清除所有子视图
        viewGroupContainer.removeAllViews();
        // 布局广告数量
        final int viewCount = viewResID.size();

        //if (datas == null || datas.size() == 0) {
        //    datas = new ArrayList<>();
        //    for (int i = 0; i < viewCount; i++) {
        //        datas.add(new ? extends ICarouselData());
        //    }
        //}

        viewGroups = new ImageView[viewCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (10 * mScale), (int) (10 * mScale));

        layoutParams.rightMargin = (int) (mScale * 2);
        layoutParams.leftMargin = (int) (mScale * 2);

        for (int i = 0; i < viewCount; i++) {

            singleView = new ImageView(mContext);
            singleView.setLayoutParams(layoutParams);
            viewGroups[i] = singleView;
            if (i == 0) {
                viewGroups[i].setBackgroundResource(highLightPointer);
            } else {
                viewGroups[i].setBackgroundResource(lowLightPointer);
            }
            viewGroupContainer.addView(viewGroups[i]);
        }
        if (viewCount > 0) {

            mAdvAdapter = new CycleViewAdapter(mContext, viewResID, datas, cycleViewListener);
            loopViewPager.setAdapter(mAdvAdapter);

            /** 如果只有一张布局那么禁止LoopViewPager的滑动并且禁止自动轮播 */
            if (viewCount == 1) {
                loopViewPager.setScrollable(false);
                viewGroupContainer.setVisibility(GONE);
                stopViewTimerTask();
            } else {/** 大于一张布局 */
                loopViewPager.setScrollable(true);
                startViewTimerTask();
            }
        }
    }

    /**
     * 装填布局数据 只有图片时
     *
     * @param datas             布局url数组
     * @param cycleViewListener listener
     */
    public void setViewResources(List<? extends ICarouselData> datas, CycleViewListener cycleViewListener) {

        setViewResources(datas, cycleViewListener, false);

    }

    /**
     * 装填布局数据 只有图片时
     *
     * @param datas             布局url数组
     * @param cycleViewListener listener
     * @param isDotCenter       指示圆点位置
     */
    public void setViewResources(List<? extends ICarouselData> datas, CycleViewListener cycleViewListener, boolean isDotCenter) {

        if (isDotCenter) {
            ((LinearLayout) viewGroupContainer).setGravity(Gravity.CENTER);
        }

        isImage = true;
        // 清除所有子视图
        viewGroupContainer.removeAllViews();
        // 布局广告数量
        final int viewCount = datas.size();

        //if (datas == null || datas.size() == 0) {
        //    datas = new ArrayList<>();
        //    for (int i = 0; i < viewCount; i++) {
        //        datas.add(new ? extends ICarouselData());
        //    }
        //}

        viewGroups = new ImageView[viewCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (10 * mScale), (int) (10 * mScale));
        layoutParams.rightMargin = (int) (mScale * 2);
        layoutParams.leftMargin = (int) (mScale * 2);
        if (showPointerAuto) {
            layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;

            layoutParams.rightMargin = (int) (mScale * 5);
            layoutParams.leftMargin = (int) (mScale * 5);
        }

        for (int i = 0; i < viewCount; i++) {

            singleView = new ImageView(mContext);

            singleView.setLayoutParams(layoutParams);
            viewGroups[i] = singleView;
            if (i == 0) {
                viewGroups[i].setBackgroundResource(highLightPointer);
            } else {
                viewGroups[i].setBackgroundResource(lowLightPointer);
            }
            viewGroupContainer.addView(viewGroups[i]);
        }
        if (viewCount > 0) {

            mAdvAdapter = new CycleViewAdapter(mContext, null, datas, cycleViewListener);
            loopViewPager.setAdapter(mAdvAdapter);

            /** 如果只有一张布局那么禁止LoopViewPager的滑动并且禁止自动轮播 */
            if (viewCount == 1) {
                loopViewPager.setScrollable(false);
                viewGroupContainer.setVisibility(GONE);
                stopViewTimerTask();
            } else {/** 大于一张布局 */
                loopViewPager.setScrollable(true);
                startViewTimerTask();
            }
        }
    }

    /**
     * 开始轮播(手动控制自动轮播与否，便于资源控制)
     */
    public void startViewCycle() {
        startViewTimerTask();
    }

    /**
     * 暂停轮播——用于节省资源
     */
    public void pushImageCycle() {
        stopViewTimerTask();
    }

    /**
     * 设定轮播图是否能自动轮播
     */
    public void setIsCarouseAutoPlay(boolean isCarouseAutoPlay) {
        this.isCarouseAutoPlay = isCarouseAutoPlay;
    }

    /**
     * 开始布局滚动任务
     */
    private void startViewTimerTask() {
        //如果不是自动, 则什么都不做
        if (!isCarouseAutoPlay) return;
        stopViewTimerTask();
        // 布局每3秒滚动一次
        mHandler.postDelayed(mViewTimerTask, 5000);
    }

    /**
     * 停止布局滚动任务
     */
    private void stopViewTimerTask() {
        mHandler.removeCallbacks(mViewTimerTask);
    }

    /**
     * 轮播控件的监听事件
     */
    public static interface CycleViewListener {

        /**
         * 加载布局资源
         *
         * @param holder
         * @param data
         */
        public void displayLayout(ICarouselData data, CarouselHolder holder, ImageView imageView);

        /**
         * 单击布局事件
         *
         * @param position
         * @param view
         */
        public void onViewClick(int position, View view);
    }

    /**
     * 轮播布局状态监听器
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {
            // 设置当前显示的布局下标
            mViewIndex = index;
            if (viewGroups != null) {
                // 设置布局滚动指示器背景
                viewGroups[index].setBackgroundResource(highLightPointer);
                for (int i = 0; i < viewGroups.length; i++) {
                    if (index != i) {
                        viewGroups[i].setBackgroundResource(lowLightPointer);
                    }
                }
            }
            if (null != pageSelecetListener)
                pageSelecetListener.pageSelect(index);

            startViewTimerTask(); // 开始下次计时
        }
    }

    private class CycleViewAdapter extends PagerAdapter {

        /**
         * 布局视图缓存列表
         */
        private ArrayList<ImageView> viewArrayList;

        /**
         * 布局资源列表
         */
        private List<Integer> viewResID = new ArrayList<Integer>();

        /**
         * 布局数据
         */
        private List<? extends ICarouselData> datas = new ArrayList<>();

        /**
         * 广告布局点击监听器
         */
        private CycleViewListener mCycleViewListener;

        private Context mContext;

        public CycleViewAdapter(Context context, List<Integer> viewResID, List<? extends ICarouselData> datas, CycleViewListener cycleViewListener) {
            mContext = context;
            this.viewResID = viewResID;
            this.datas = datas;
            mCycleViewListener = cycleViewListener;
            viewArrayList = new ArrayList<ImageView>();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ICarouselData data = datas.get(position);

            String resource = datas.get(position).getDisplayUrl();
            ImageView view = null;

            if (viewArrayList.isEmpty()) {

                view = new ImageView(mContext);
                view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                view.setScaleType(ImageView.ScaleType.FIT_XY);

            } else {
                view = viewArrayList.remove(0);
            }

            // 设置布局点击监听
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCycleViewListener.onViewClick(position, v);
                }
            });
            view.setTag(resource);
            container.addView(view);
            mCycleViewListener.displayLayout(data, null, view);
            return view;


        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);
            viewArrayList.add(view);
        }

    }

    public void setHighLightPointer(int highLightPointer) {
        this.highLightPointer = highLightPointer;
    }

    public void setLowLightPointer(int lowLightPointer) {
        this.lowLightPointer = lowLightPointer;
    }

    public class CarouselHolder {

        public ImageView iv_data;
        public TextView tv_data;

    }


    /**
     * 设置pointer的位置
     *
     * @param gravity
     */
    public void setPointerGravity(int gravity) {
        if (viewGroupContainer == null)
            return;

        ((LinearLayout) viewGroupContainer).setGravity(gravity);
    }

    /**
     * 隐藏自带指针
     */
    public void hidePointerView() {
        if (viewGroupContainer == null)
            return;
        viewGroupContainer.setVisibility(View.GONE);

    }

    public void setShowPointerAuto(boolean showPointerAuto) {
        this.showPointerAuto = showPointerAuto;
    }

    public void setPageSelecetListener(PageSelecetListener listener) {

        this.pageSelecetListener = listener;
    }

    public interface PageSelecetListener {
        //监听滑动页
        void pageSelect(int index);
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }
}
