package com.zhxh.xcomponentlib;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import com.zhxh.xcomponentlib.utils.BtnUtils;

/**
 * Created by zhxh on 2018/05/23
 */
public final class XButton extends AppCompatButton {

    GradientDrawable gradientDrawable;

    //关键属性设置
    private int solidColor = Color.TRANSPARENT;
    private int strokeColor = Color.TRANSPARENT;
    private int pressedColor = Color.TRANSPARENT;
    private int pressedTextColor = Color.TRANSPARENT;
    private int clickTextColor = Color.TRANSPARENT;
    private int textColorAnimEnd = Color.TRANSPARENT;
    private int angleCorner = 0;
    private int strokeWidth = 0;

    private int defaultTextColor;

    private int drawableWidth;
    private DrawablePosition position;

    Rect bounds;
    int drawablePadding = 0;

    enum DrawablePosition {
        NONE,
        LEFT_AND_RIGHT,
        LEFT,
        RIGHT
    }

    boolean isTouchPass = true;

    //动画
    boolean isClickAnim;
    boolean isAnimComplete;

    private float animatedValue;
    private int drawableStart;
    private int drawableEnd;
    private float clickAnimTime = 500;
    ClickAnimAction clickAnimAction;

    public XButton(Context context) {
        this(context, null);
    }

    public XButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XButton);
        pressedColor = a.getColor(R.styleable.XButton_XpressedColor, pressedColor);
        pressedTextColor = a.getColor(R.styleable.XButton_XpressedTextColor, pressedTextColor);
        clickTextColor = a.getColor(R.styleable.XButton_XclickTextColor, clickTextColor);
        solidColor = a.getColor(R.styleable.XButton_XsolidColor, solidColor);
        strokeColor = a.getColor(R.styleable.XButton_XstrokeColor, strokeColor);
        angleCorner = a.getDimensionPixelSize(R.styleable.XButton_XangleCorner, angleCorner);
        strokeWidth = a.getDimensionPixelSize(R.styleable.XButton_XstrokeWidth, strokeWidth);
        drawablePadding = a.getDimensionPixelSize(R.styleable.XButton_XdrawablePadding, drawablePadding);

        defaultTextColor = this.getCurrentTextColor();

        if (pressedTextColor == Color.TRANSPARENT) {
            pressedTextColor = defaultTextColor;
        }
        if (clickTextColor == Color.TRANSPARENT) {
            clickTextColor = defaultTextColor;
        }
        if (textColorAnimEnd == Color.TRANSPARENT) {
            textColorAnimEnd = defaultTextColor;
        }

        if (null == bounds) {
            bounds = new Rect();
        }
        if (null == gradientDrawable) {
            gradientDrawable = new GradientDrawable();
        }

        setGravity(Gravity.CENTER);
        setDrawablePadding(drawablePadding);
        setBtnDrawable();

        //设置按钮点击之后的颜色更换
        setOnTouchListener((arg0, event) -> {
            if (isClickAnim) {
                return false;
            }
            setBackgroundDrawable(gradientDrawable);
            return setColor(event.getAction());
        });

    }

    @Override
    public boolean performClick() {
        if (BtnUtils.isQuickClick()) {
            return true;
        }
        return super.performClick();
    }

    public void startAnim() {

        if (clickAnimAction == null) {
            return;
        }


        ValueAnimator animator = ValueAnimator.ofFloat(0, clickAnimTime);
        animator.setDuration((long) clickAnimTime);
        animator.addUpdateListener(animation -> {

            animatedValue = (float) animation.getAnimatedValue();

            if (animatedValue < clickAnimTime / 2) {
                float startAlpha = (clickAnimTime / 2 - animatedValue) / (clickAnimTime / 2);
                XButton.this.setBackgroundResource(drawableStart);
                XButton.this.setAlpha(startAlpha);
            } else if (animatedValue < clickAnimTime) {
                float endAlpha = (animatedValue - clickAnimTime / 2) / (clickAnimTime / 2);
                XButton.this.setAlpha(endAlpha);
                XButton.this.setBackgroundResource(drawableEnd);
            } else if (animatedValue == clickAnimTime) {
                isAnimComplete = true;
                this.setTextColor(textColorAnimEnd);
                clickAnimAction.onAnimFinished();
                this.setClickable(false);
            }

        });

        animator.start();
        postInvalidate();
    }

    public void setClickAnimTime(float clickAnimTime) {
        this.clickAnimTime = clickAnimTime;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Paint textPaint = getPaint();
        String text = getText().toString();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        int textWidth = bounds.width();
        int factor = (position == DrawablePosition.LEFT_AND_RIGHT) ? 2 : 1;
        int contentWidth = drawableWidth + drawablePadding * factor + textWidth;
        int horizontalPadding = (int) ((getWidth() / 2.0) - (contentWidth / 2.0));

        setCompoundDrawablePadding(-horizontalPadding + drawablePadding);

        switch (position) {
            case LEFT:
                setPadding(horizontalPadding, getPaddingTop(), 0, getPaddingBottom());
                break;

            case RIGHT:
                setPadding(0, getPaddingTop(), horizontalPadding, getPaddingBottom());
                break;

            case LEFT_AND_RIGHT:
                setPadding(horizontalPadding, getPaddingTop(), horizontalPadding, getPaddingBottom());
                break;

            default:
                setPadding(0, getPaddingTop(), 0, getPaddingBottom());
        }
    }


    //重新设置位置
    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);

        if (left != null && right != null) {
            drawableWidth = left.getIntrinsicWidth() + right.getIntrinsicWidth();
            position = DrawablePosition.LEFT_AND_RIGHT;
        } else if (left != null) {
            drawableWidth = left.getIntrinsicWidth();
            position = DrawablePosition.LEFT;
        } else if (right != null) {
            drawableWidth = right.getIntrinsicWidth();
            position = DrawablePosition.RIGHT;
        } else {
            position = DrawablePosition.NONE;
        }

        requestLayout();
    }

    //还原为默认
    public void reset() {
        pressedColor = Color.TRANSPARENT;
        solidColor = Color.TRANSPARENT;
        strokeColor = Color.TRANSPARENT;

        angleCorner = 0;
        strokeWidth = 0;
    }

    public boolean isAnimComplete() {
        return isAnimComplete;
    }


    public void setIsAnimComplete(boolean isAnimComplete) {

        this.isAnimComplete = isAnimComplete;

        if (isAnimComplete) {
            this.setTextColor(textColorAnimEnd);
            this.setClickable(false);
            this.setBackgroundResource(drawableEnd);
        } else {
            this.setTextColor(defaultTextColor);
            this.setClickable(true);
            this.setBackgroundResource(drawableStart);
        }
    }

    public void setAnimDrawable(int drawableStart, int drawableEnd, ClickAnimAction clickAnimAction) {
        this.isClickAnim = true;
        this.drawableStart = drawableStart;
        this.drawableEnd = drawableEnd;
        this.clickAnimAction = clickAnimAction;
    }

    public void setTextColorAnimEnd(int textColorAnimEnd) {
        this.textColorAnimEnd = textColorAnimEnd;
    }

    //除去Angle还原为默认
    public void resetExAngle() {
        pressedColor = Color.TRANSPARENT;
        solidColor = Color.TRANSPARENT;
        strokeColor = Color.TRANSPARENT;

        strokeWidth = 0;
    }

    public void setDrawablePadding(int padding) {
        drawablePadding = padding;
        requestLayout();
    }

    private void setBtnDrawable() {
        //设置按钮颜色
        gradientDrawable.setColor(solidColor);
        //设置按钮的边框宽度
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        //设置按钮圆角大小
        gradientDrawable.setCornerRadius(angleCorner);
        setBackgroundDrawable(gradientDrawable);
    }


    //处理按钮点击事件无效
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        isTouchPass = false;
    }

    //处理按下去的颜色 区分solid和stroke模式
    public boolean setColor(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                gradientDrawable.setColor(pressedColor);
                this.setTextColor(pressedTextColor);
                break;
            case MotionEvent.ACTION_UP:
                gradientDrawable.setColor(solidColor);
                this.setTextColor(defaultTextColor);
                break;
            case MotionEvent.ACTION_CANCEL:
                gradientDrawable.setColor(solidColor);
                this.setTextColor(defaultTextColor);
                break;
        }

        return isTouchPass;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isClickAnim) {
            if (isAnimComplete) {
                this.setBackgroundResource(drawableEnd);
                this.setClickable(false);
            }
        }
    }

    //对外定义接口
    public void setPressedColor(int pressedColor) {
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    public void setSolidColor(int solidColor) {
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    public void setStrokeColor(int strokeColor) {
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    public void setBtnAttr(int solidColor, int strokeColor, int pressedColor, int angleCorner, int strokeWidth) {
        this.solidColor = solidColor;
        this.strokeColor = strokeColor;
        this.pressedColor = pressedColor;
        this.angleCorner = angleCorner;
        this.strokeWidth = strokeWidth;
        setBtnDrawable();
    }

    //实心的
    public void setSolidAttr(int solidColor, int pressedColor, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    public void setSolidAttr(int solidColor, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    public void setSolidAttr(int solidColor) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    //空心的
    public void setStrokeAttr(int strokeColor, int pressedColor, int strokeWidth, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    public void setStrokeAttr(int strokeColor, int strokeWidth, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }

    public void setStrokeAttr(int strokeColor, int strokeWidth) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, angleCorner, strokeWidth);
    }


    public interface ClickAnimAction {
        void onAnimFinished();
    }


}
