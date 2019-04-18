package com.zhxh.xcomponentlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by zhxh on 2019/4/18
 * 倾斜字体 实现银行精选列表中，开抢时间的倾斜展示
 */
public class TiltTextView extends android.support.v7.widget.AppCompatTextView {
    private int degrees;

    public TiltTextView(Context context) {
        super(context);
    }

    public TiltTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TiltTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TiltTextView);
        degrees = typedArray.getInt(R.styleable.TiltTextView_Xdegree, 0);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (degrees != 0) {
            canvas.rotate(-degrees, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        }
        super.onDraw(canvas);
    }
}
