package com.zhxh.xcomponentlib.stickyscroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ScrollView;

import com.zhxh.xcomponentlib.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxh on 2018/7/3
 */
public class StickyScrollView extends ScrollView {

    /**
     * Tag for views that should stick and have constant drawing. e.g. TextViews, ImageViews etc
     */
    public static final String STICKY_TAG = "sticky";

    /**
     * Default height of the shadow peeking out below the stuck view.
     */
    private static final int DEFAULT_SHADOW_HEIGHT = 10; // dp;

    /**
     * Flag for views that should stick and have non-constant drawing. e.g. Buttons, ProgressBars etc
     */
    public static final String FLAG_NONCONSTANT = "-nonconstant";

    /**
     * Flag for views that have aren't fully opaque
     */
    public static final String FLAG_HASTRANSPARANCY = "-hastransparancy";

    /**
     * 正常排列模式
     */
    public static final int NORMAL_MODE = 0;

    /**
     * 队列排列模式
     */
    public static final int ARRAY_MODE = 1;

    /**
     * StickyScrollView的默认模式
     */
    private int MODE = NORMAL_MODE;

    private ArrayList<View> stickyViews;
    private View currentlyStickingView;
    private int stickyViewTopOffset;
    private int stickyViewLeftOffset;
    private boolean redirectTouchesToStickyView;
    private boolean clippingToPadding;
    private boolean clipToPaddingHasBeenSet;

    private int mShadowHeight;
    private Drawable mShadowDrawable;

    private int currentIndex;
    private List<Integer> sumList;

    public StickyScrollView(Context context) {
        this(context, null);
    }

    public StickyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NORMAL_MODE, ARRAY_MODE})
    public @interface Type {
    }

    public void setMODE(@Type int MODE) {
        this.MODE = MODE;
    }

    /**
     * 当点击Sticky的时候，实现某些背景的渐变
     */
    private Runnable invalidataRunnable = new Runnable() {

        @Override
        public void run() {
            if (currentlyStickingView != null) {
                int left = currentlyStickingView.getLeft();
                int top = currentlyStickingView.getTop();
                int right = currentlyStickingView.getRight();
                int bottom = getScrollY() + (currentlyStickingView.getHeight() + stickyViewTopOffset);

                invalidate(left, top, right, bottom);
            }

            postDelayed(this, 16);

        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        upDateView();
        return super.onInterceptTouchEvent(ev);
    }


    public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.StickyScrollView, defStyle, 0);
        try {
            setup();
            final float density = context.getResources().getDisplayMetrics().density;
            int defaultShadowHeightInPix = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);

            mShadowHeight = a.getDimensionPixelSize(
                    R.styleable.StickyScrollView_stuckShadowHeight,
                    defaultShadowHeightInPix);

            int shadowDrawableRes = a.getResourceId(
                    R.styleable.StickyScrollView_stuckShadowDrawable, -1);

            if (shadowDrawableRes != -1) {
                mShadowDrawable = context.getResources().getDrawable(
                        shadowDrawableRes);
            }

            MODE = a.getInteger(R.styleable.StickyScrollView_mode, NORMAL_MODE);
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets the height of the shadow drawable in pixels.
     *
     * @param height
     */
    public void setShadowHeight(int height) {
        mShadowHeight = height;
    }


    public void setup() {
        stickyViews = new ArrayList<View>();
        sumList = new ArrayList<>();
    }

    private int getLeftForViewRelativeOnlyChild(View v) {
        int left = v.getLeft();
        while (v.getParent() != getChildAt(0)) {
            v = (View) v.getParent();
            left += v.getLeft();
        }
        return left;
    }

    private int getTopForViewRelativeOnlyChild(View v) {
        int top = v.getTop();
        while (v.getParent() != getChildAt(0)) {
            v = (View) v.getParent();
            top += v.getTop();
        }
        return top;
    }

    private int getRightForViewRelativeOnlyChild(View v) {
        int right = v.getRight();
        while (v.getParent() != getChildAt(0)) {
            v = (View) v.getParent();
            right += v.getRight();
        }
        return right;
    }

    private int getBottomForViewRelativeOnlyChild(View v) {
        int bottom = v.getBottom();
        while (v.getParent() != getChildAt(0)) {
            v = (View) v.getParent();
            bottom += v.getBottom();
        }
        return bottom;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!clipToPaddingHasBeenSet) {
            clippingToPadding = true;
        }
        notifyHierarchyChanged();
    }

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
        clippingToPadding = clipToPadding;
        clipToPaddingHasBeenSet = true;
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        findStickyViews(child);
    }

    StickyRunnable runnable;
    int mRecycleTime = 500;

    private class StickyRunnable implements Runnable {
        private int time = 0;

        @Override
        public void run() {
            // 防止一直循环浪费内存
            if (time < mRecycleTime) {
                if (getHandler() != null) {
                    getHandler().removeCallbacks(this);
                    getHandler().postDelayed(this, 16);
                    postInvalidate();
                }
                time = time + 16;
            } else {
                return;
            }
        }

        public void start() {
            time = 0;
            run();
        }

    }

    /**
     * 为了防止现某些动画特效时间过长导致刷新失败的情况
     *
     * @param time
     */
    public void setRunnableRecycleTime(int time) {
        mRecycleTime = time;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        upDateView();
    }

    /**
     * 这里是为了保证一些Sticky的View里包含动画或者某些特效的情况下,特效和动画还能一定程度上保证正常
     */
    private void upDateView() {
//        if (runnable != null) {
//            removeCallbacks(runnable);
//            runnable = null;
//        }
//        runnable = new StickyRunnable();
//        postDelayed(runnable, 16);

        if (runnable == null) {
            runnable = new StickyRunnable();
        }
        runnable.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        switch (MODE) {
            case ARRAY_MODE:
                drawArrayLayout(canvas);
                break;
            case NORMAL_MODE:
            default:
                drawNormalMode(canvas);
                break;

        }

    }

    private void drawNormalMode(Canvas canvas) {
        if (currentlyStickingView != null) {
//            canvas.save();
            RectF bounds = new RectF(canvas.getClipBounds());
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, (int) (Math.min(255, Math.max(0, alphaList.get(stickyViews.indexOf(currentlyStickingView)) * 255f))), Canvas.ALL_SAVE_FLAG);
            canvas.translate(getPaddingLeft() + stickyViewLeftOffset, getScrollY() + stickyViewTopOffset + (clippingToPadding ? getPaddingTop() : 0));
            canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth() - stickyViewLeftOffset, currentlyStickingView.getHeight() + mShadowHeight + 1);

            if (mShadowDrawable != null) {
                int left = 0;
                int right = currentlyStickingView.getWidth();
                int top = currentlyStickingView.getHeight();
                int bottom = currentlyStickingView.getHeight() + mShadowHeight;
                mShadowDrawable.setBounds(left, top, right, bottom);
                mShadowDrawable.draw(canvas);
            }

            canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth(), currentlyStickingView.getHeight());
            canvas.translate(-currentlyStickingView.getScrollX(), -currentlyStickingView.getScrollY());
            Drawable background = currentlyStickingView.getBackground();
            background.draw(canvas);
            if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)) {
                showView(currentlyStickingView);
                currentlyStickingView.draw(canvas);
                hideView(currentlyStickingView);
            } else {
                currentlyStickingView.draw(canvas);
            }
            canvas.restore();
        }
    }

    public void drawArrayLayout(Canvas canvas) {
        final int index = stickyViews.indexOf(currentlyStickingView);
        RectF bounds = null;
        for (int i = 0; i <= index; i++) {
            View currentItem = stickyViews.get(i);
            if (currentItem != null) {
//                canvas.save();
                if (bounds == null) {
                    bounds = new RectF(canvas.getClipBounds());
                }
                canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, (int) (Math.min(255, Math.max(0, alphaList.get(i) * 255f))), Canvas.ALL_SAVE_FLAG);
                canvas.translate(getPaddingLeft() + stickyViewLeftOffset, getScrollY() + stickyViewTopOffset + (clippingToPadding ? getPaddingTop() : 0) + sumList.get(i));

                canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth(), currentItem.getHeight() + mShadowHeight + 1);
                if (mShadowDrawable != null) {
                    int left = 0;
                    int right = currentItem.getWidth();
                    int top = currentItem.getHeight();
                    int bottom = currentItem.getHeight() + mShadowHeight;
                    mShadowDrawable.setBounds(left, top, right, bottom);
                    mShadowDrawable.draw(canvas);
                }

                canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth(), currentItem.getHeight());
                canvas.translate(-currentItem.getScrollX(), -currentItem.getScrollY());
                Drawable background = currentItem.getBackground();
                if (background == null) {
                    background = new ColorDrawable(Color.TRANSPARENT);
                }
                background.draw(canvas);
                currentItem.draw(canvas);
                canvas.restore();

            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (MODE) {
            case ARRAY_MODE:
                diapatchArrayTouchEvent(ev);
                break;
            case NORMAL_MODE:
            default:
                diapatchNormalTouchEvent(ev);
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void diapatchNormalTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            redirectTouchesToStickyView = true;
        }

        if (redirectTouchesToStickyView) {
            redirectTouchesToStickyView = currentlyStickingView != null;
            if (redirectTouchesToStickyView) {
                redirectTouchesToStickyView =
                        ev.getY() <= (currentlyStickingView.getHeight() + stickyViewTopOffset) &&
                                ev.getX() >= getLeftForViewRelativeOnlyChild(currentlyStickingView) &&
                                ev.getX() <= getRightForViewRelativeOnlyChild(currentlyStickingView);
            }
        } else if (currentlyStickingView == null) {
            redirectTouchesToStickyView = false;
        }
        if (redirectTouchesToStickyView) {
            ev.offsetLocation(0, -1 * ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
        }
    }

    private void diapatchArrayTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            redirectTouchesToStickyView = true;
        }

        if (redirectTouchesToStickyView) {
            redirectTouchesToStickyView = currentlyStickingView != null;
            if (redirectTouchesToStickyView) {
                int index = stickyViews.indexOf(currentlyStickingView);
                redirectTouchesToStickyView =
                        ev.getY() <= (sumList.get(index + 1) + stickyViewTopOffset) &&
                                ev.getX() >= getLeftForViewRelativeOnlyChild(currentlyStickingView) &&
                                ev.getX() <= getRightForViewRelativeOnlyChild(currentlyStickingView);
            }
        } else if (currentlyStickingView == null) {
            redirectTouchesToStickyView = false;
        }
        if (redirectTouchesToStickyView) {
            currentIndex = 0;
            for (int i = 0; i < sumList.size() - 1; i++) {
                if (i == 0) {
                    if (ev.getY() <= sumList.get(1)) {
                        currentIndex = 0;
                        break;
                    }
                } else {
                    if (sumList.get(i + 1) >= ev.getY() && sumList.get(i) < ev.getY()) {
                        currentIndex = i;
                        break;
                    }
                }
            }

            ev.offsetLocation(0, -1 * ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(stickyViews.get(currentIndex)) + sumList.get(currentIndex)));
        }
    }

    private boolean hasNotDoneActionDown = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (redirectTouchesToStickyView) {
            switch (MODE) {
                case ARRAY_MODE:
                    ev.offsetLocation(0, ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(stickyViews.get(currentIndex)) + sumList.get(currentIndex)));
                    break;
                case NORMAL_MODE:
                default:
                    ev.offsetLocation(0, ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
                    break;
            }

        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            hasNotDoneActionDown = false;
        }

        if (hasNotDoneActionDown) {
            MotionEvent down = MotionEvent.obtain(ev);
            down.setAction(MotionEvent.ACTION_DOWN);
            super.onTouchEvent(down);
            hasNotDoneActionDown = false;
        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            hasNotDoneActionDown = true;
        }

        return super.onTouchEvent(ev);
    }

    private OnScrollChangedListener onScrollChangedListener;

    /**
     * @param onScrollChangedListener
     */
    public void setOnScrollListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(x, y, oldx, oldy);
        }
        switch (MODE) {
            case ARRAY_MODE:
                doArrayStickyThing();
                break;
            case NORMAL_MODE:
            default:
                doNormalStickyThing();
                break;
        }

    }

    private void doNormalStickyThing() {
        View viewThatShouldStick = null;
        View approachingView = null;
        for (View v : stickyViews) {
            int viewTop = getTopForViewRelativeOnlyChild(v) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop());
            if (viewTop <= 0) {
                if (viewThatShouldStick == null || viewTop > (getTopForViewRelativeOnlyChild(viewThatShouldStick) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))) {
                    viewThatShouldStick = v;
                }
            } else {
                if (approachingView == null || viewTop < (getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))) {
                    approachingView = v;
                }
            }
        }
        if (viewThatShouldStick != null) {
            stickyViewTopOffset = approachingView == null ? 0 : Math.min(0, getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()) - viewThatShouldStick.getHeight());
            if (viewThatShouldStick != currentlyStickingView) {
                if (currentlyStickingView != null) {
                    stopStickingCurrentlyStickingView();
                }
                // only compute the left offset when we start sticking.
                stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(viewThatShouldStick);
                startStickingView(viewThatShouldStick);
            }
        } else if (currentlyStickingView != null) {
            stopStickingCurrentlyStickingView();
        }
    }

    private void doArrayStickyThing() {
        View viewThatShouldStick = null;
        View approachingView = null;
        sumList.clear();
        int sum = 0;
        sumList.add(sum);
        for (int i = 0; i < stickyViews.size(); i++) {
            View v = stickyViews.get(i);
            int viewTop = getTopForViewRelativeOnlyChild(v) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop());
            if (viewTop <= sum) {
                if (viewThatShouldStick == null || viewTop > (getTopForViewRelativeOnlyChild(viewThatShouldStick) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))) {
                    viewThatShouldStick = v;
                }
            } else {
                if (approachingView == null || viewTop < (getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))) {
                    approachingView = v;
                }
            }
            sum = sum + v.getHeight();
            sumList.add(sum);
        }


        if (viewThatShouldStick != null) {
            stickyViewTopOffset = approachingView == null ? 0 : Math.min(0, getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()) - viewThatShouldStick.getHeight());
            if (viewThatShouldStick != currentlyStickingView) {
                if (currentlyStickingView != null) {
                    stopStickingCurrentlyStickingView();
                }
                // only compute the left offset when we start sticking.
                stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(viewThatShouldStick);
                startStickingView(viewThatShouldStick);
            }
        } else if (currentlyStickingView != null) {
            stopStickingCurrentlyStickingView();
        }
    }

    private boolean hideStickyView = true;

    public void setHideStickyView(boolean hideStickyView) {
        this.hideStickyView = hideStickyView;
    }

    private List<Float> alphaList = new ArrayList<>();

    private void startStickingView(View viewThatShouldStick) {
        // 当一个View显示sticky的时候，让它不被看到，防止和一些alpha值比较低的时候引起的界面显示重叠问题
        final int stickyViewsSize = stickyViews.size();
        for (int i = 0; i < stickyViewsSize; i++) {
            if (i <= stickyViews.indexOf(viewThatShouldStick)) {
                if (hideStickyView) {
                    stickyViews.get(i).setAlpha(0);
                } else {
                    stickyViews.get(i).setAlpha(alphaList.get(i));
                }
            } else {
                stickyViews.get(i).setAlpha(alphaList.get(i));
            }
        }
        currentlyStickingView = viewThatShouldStick;
        if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)) {
            hideView(currentlyStickingView);
        }
        if (((String) currentlyStickingView.getTag()).contains(FLAG_NONCONSTANT)) {
            post(invalidataRunnable);
        }
    }


    private void stopStickingCurrentlyStickingView() {
        final int stickyViewsSize = stickyViews.size();
        for (int i = 0; i < stickyViewsSize; i++) {
            stickyViews.get(i).setAlpha(alphaList.get(i));
        }
        if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)) {
            showView(currentlyStickingView);
        }
        currentlyStickingView = null;
        removeCallbacks(invalidataRunnable);
    }

    /**
     * Notify that the sticky attribute has been added or removed from one or more views in the View hierarchy
     */
    public void notifyStickyAttributeChanged() {
        notifyHierarchyChanged();
    }

    private void notifyHierarchyChanged() {
        if (currentlyStickingView != null) {
            stopStickingCurrentlyStickingView();
        }
        stickyViews.clear();
        findStickyViews(getChildAt(0));
        doArrayStickyThing();
        invalidate();
    }

    private void findStickyViews(View v) {
        if (v == null) return;
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                String tag = getStringTagForView(vg.getChildAt(i));
                if (tag != null && tag.contains(STICKY_TAG)) {
                    stickyViews.add(vg.getChildAt(i));
                    alphaList.add(vg.getChildAt(i).getAlpha());
                } else if (vg.getChildAt(i) instanceof ViewGroup) {
                    findStickyViews(vg.getChildAt(i));
                }
            }
        } else {
            String tag = (String) v.getTag();
            if (tag != null && tag.contains(STICKY_TAG)) {
                stickyViews.add(v);
                alphaList.add(v.getAlpha());
            }
        }
    }

    private void hideView(View v) {
        if (Build.VERSION.SDK_INT >= 11) {
            v.setAlpha(0);
        } else {
            AlphaAnimation anim = new AlphaAnimation(1, 0);
            anim.setDuration(0);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }
    }

    private void showView(View v) {
        if (Build.VERSION.SDK_INT >= 11) {
            v.setAlpha(currentlyStickingView.getAlpha());
        } else {
            AlphaAnimation anim = new AlphaAnimation(0, 1);
            anim.setDuration(0);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }
    }

    private String getStringTagForView(View v) {
        Object tagObject = v.getTag();
        return String.valueOf(tagObject);
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int x, int y, int oldxX, int oldY);
    }
}
