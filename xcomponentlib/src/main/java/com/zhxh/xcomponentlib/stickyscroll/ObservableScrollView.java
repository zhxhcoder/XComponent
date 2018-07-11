package com.zhxh.xcomponentlib.stickyscroll;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by zhxh on 2018/7/3
 */
public class ObservableScrollView extends StickyScrollView {

    private ScrollViewListener scrollViewListener = null;
    private ScrollViewFullScrollListener fullScrollListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public void setFullScrollListener(ScrollViewFullScrollListener fullScrollListener) {
        this.fullScrollListener = fullScrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }

        if (getScrollY() + getHeight() == computeVerticalScrollRange()) {
            if (fullScrollListener != null) {
                fullScrollListener.onScrollFullDown(y);
            }
        }
    }

    public interface ScrollViewFullScrollListener {
        void onScrollFullDown(int scrollY);
    }

    public interface ScrollViewListener {

        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

    }

}