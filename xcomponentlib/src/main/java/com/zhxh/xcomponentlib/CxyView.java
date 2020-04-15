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
    private Paint mBelowWavePaint;
    private DrawFilter mDrawFilter;
    private float φ;

    public CxyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //初始化路径
        mLinePath = new Path();

        //初始化画笔
        mBelowWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBelowWavePaint.setAntiAlias(true);
        mBelowWavePaint.setStyle(Paint.Style.FILL);
        mBelowWavePaint.setColor(Color.DKGRAY);
        mBelowWavePaint.setAlpha(80);

        //画布抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);

        mLinePath.reset();

        φ-=0.1f;
        float y,y2;
        double ω = 2*Math.PI / getWidth();//角速度

        mLinePath.moveTo(getLeft(),getBottom());

        for (float x = 0; x <= getWidth(); x += 20) {
            /**
             *  y=Asin(ωx+φ)+k
             *  A—振幅越大，波形在y轴上最大与最小值的差值越大
             *  ω—角速度， 控制正弦周期(单位角度内震动的次数)
             *  φ—初相，反映在坐标系上则为图像的左右移动。这里通过不断改变φ,达到波浪移动效果
             *  k—偏距，反映在坐标系上则为图像的上移或下移。
             */
            y2 = (float) (15 * Math.sin(ω * x + φ) + 15);
            mLinePath.lineTo(x, y2);
        }

        mLinePath.lineTo(getRight(),getBottom());
        canvas.drawPath(mLinePath,mBelowWavePaint);

        //20毫秒后会重新调用onDraw()方法
        //postInvalidateDelayed(20);
    }
}
