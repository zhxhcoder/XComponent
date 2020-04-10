package com.zhxh.xcomponentlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zhxh on 2020/4/10
 * 扇形
 */
public class CArcProgress extends View {
    private Paint paint;
    private int radious;
    private String total = "666.00M";
    private String title = "可用流量";
    private String alerady = "已用流量66.99M";
    private Path mPath;
    private float[] pos;
    private float[] tan;
    private PathMeasure mMeasure;

    private ValueAnimator valueAnimator;
    private float mAnimatorValue;
    private float percent = 50;

    private int sweepAngle;

    public CArcProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);

        mPath = new Path();
        mMeasure = new PathMeasure();
        pos = new float[2];
        tan = new float[2];

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatorValue = (float) valueAnimator.getAnimatedValue();

                float degree = percent / 100 * 180;

                Log.e("valueAnimator", mAnimatorValue * degree + "");
                sweepAngle = (int) (mAnimatorValue * degree);

                invalidate();
            }
        });
        valueAnimator.setDuration(3000);
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int htightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int defaultSize = 600;

        if (widthMode == MeasureSpec.AT_MOST && htightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultSize, defaultSize);
        } else if (widthMode == MeasureSpec.AT_MOST) {

            setMeasuredDimension(defaultSize, height);
        } else if (htightMode == MeasureSpec.AT_MOST) {

            setMeasuredDimension(width, defaultSize);
        } else {
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rectF = new RectF();
        radious = (int) ((Math.min(getWidth(), getHeight()) / 2 - paint.getStrokeWidth()) - 100);
        rectF.left = getWidth() / 2 - radious;
        rectF.top = getHeight() / 2 - radious;
        rectF.right = getWidth() / 2 + radious;
        rectF.bottom = getHeight() / 2 + radious;

        paint.setColor(Color.parseColor("#44000000"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawArc(rectF, 180, 180, false, paint);

        mPath.addArc(rectF, 180, 180);
        mMeasure.setPath(mPath, false);
        mMeasure.getPosTan(mMeasure.getLength(), pos, tan);
        //canvas.drawCircle(pos[0], pos[1], 10, paint);

        paint.setColor(Color.GREEN);
        canvas.drawArc(rectF, 180, sweepAngle, false, paint);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setTextSize(40);
        canvas.drawText(total, getWidth() / 2 - paint.measureText(total) / 2, getHeight() / 2, paint);
        paint.setTextSize(30);
        canvas.drawText(title, getWidth() / 2 - paint.measureText(total) / 2, getHeight() / 2 - paint.measureText(total) / 2, paint);

        paint.setTextSize(35);
        canvas.drawText(alerady, getWidth() / 2 - paint.measureText(alerady) / 2, pos[1] + paint.measureText(alerady) / 4, paint);
    }
}
