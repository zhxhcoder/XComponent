package com.zhxh.xcomponentlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhxh on 2020/4/15
 */
public final class CxyView extends View {

    private Path mLinePath;
    private Paint mPaint;
    private DrawFilter mDrawFilter;

    public CxyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //初始化路径
        mLinePath = new Path();

        //初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setAlpha(80);

        //画布抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);

        mLinePath.reset();

        float y;

        mLinePath.moveTo(getLeft(),getBottom());

        for (float x = 0; x <= getWidth(); x += 10) {
            y = x*2;
            mLinePath.lineTo(x, y);
        }

        mLinePath.lineTo(getRight(),getBottom());
        canvas.drawPath(mLinePath, mPaint);

        //20毫秒后会重新调用onDraw()方法
        //postInvalidateDelayed(20);
    }
}
