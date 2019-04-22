package com.zhxh.xcomponentlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhxh on 2019/4/18
 * 继X系列开源库之后，开始C(新公司前缀)系列开源库开发
 * 倾斜字体 实现银行精选列表中，开抢时间的倾斜展示
 * 开始支持 下面带边框的效果
 * 特殊字体支持 CRegText 正则匹配 187% %颜色或字体大小改变
 */
public final class CTextView extends android.support.v7.widget.AppCompatTextView {
    GradientDrawable gradientDrawable;

    //关键属性设置
    private int solidColor = Color.TRANSPARENT;
    private int strokeColor = Color.TRANSPARENT;
    private int pressedColor = Color.TRANSPARENT;
    private int pressedTextColor = Color.TRANSPARENT;
    private int clickTextColor = Color.TRANSPARENT;
    private int angleCorner = 0;
    private int strokeWidth = 0;


    private Context ctx;
    private String specialTextReg;
    private int specialTextSize = 0;
    private int specialTextColor = Color.TRANSPARENT;


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

    public CTextView(Context context) {
        this(context, null);
    }

    public CTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context, AttributeSet attrs) {
        ctx = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CTextView);
        pressedColor = a.getColor(R.styleable.CTextView_CPressedColor, pressedColor);
        pressedTextColor = a.getColor(R.styleable.CTextView_CPressedTextColor, pressedTextColor);
        clickTextColor = a.getColor(R.styleable.CTextView_CClickTextColor, clickTextColor);
        solidColor = a.getColor(R.styleable.CTextView_CSolidColor, solidColor);
        strokeColor = a.getColor(R.styleable.CTextView_CStrokeColor, strokeColor);
        angleCorner = a.getDimensionPixelSize(R.styleable.CTextView_CAngleCorner, angleCorner);
        strokeWidth = a.getDimensionPixelSize(R.styleable.CTextView_CStrokeWidth, strokeWidth);
        drawablePadding = a.getDimensionPixelSize(R.styleable.CTextView_CDrawablePadding, drawablePadding);
        degrees = a.getInteger(R.styleable.CTextView_CRotateDegree, 0);
        specialTextReg = a.getString(R.styleable.CTextView_CSpecialTextReg);
        specialTextSize = a.getDimensionPixelSize(R.styleable.CTextView_CSpecialTextSize, specialTextSize);
        specialTextColor = a.getColor(R.styleable.CTextView_CSpecialTextColor, specialTextColor);

        defaultTextColor = this.getCurrentTextColor();

        if (pressedTextColor == Color.TRANSPARENT) {
            pressedTextColor = defaultTextColor;
        }
        if (clickTextColor == Color.TRANSPARENT) {
            clickTextColor = defaultTextColor;
        }

        if (pressedColor == Color.TRANSPARENT) {
            if (solidColor != Color.TRANSPARENT) {
                pressedColor = solidColor;
            }
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
            setBackgroundDrawable(gradientDrawable);
            return setColor(event.getAction());
        });

    }

    @Override
    public boolean performClick() {
        return super.performClick();
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

    private int degrees;

    @Override
    protected void onDraw(Canvas canvas) {

        int lastDegrees = degrees % 360;//优化大于360度情况
        if (lastDegrees != 0) {
            canvas.rotate(-lastDegrees, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        }

        SpannableString spannableString = getReformSpecialText(getText().toString(), specialTextReg, specialTextColor, specialTextSize);
        this.setText(spannableString);
        super.onDraw(canvas);
    }

    public SpannableString getReformSpecialText(String srcStr, String regularExpression, int valueColor, int size) {

        if (TextUtils.isEmpty(srcStr))
            return new SpannableString("");

        if (TextUtils.isEmpty(regularExpression) || !srcStr.contains(regularExpression))
            return new SpannableString(srcStr);


        if (valueColor == 0) {
            valueColor = this.getCurrentTextColor();
        }


        SpannableString resultSpan = new SpannableString(srcStr);

        Pattern p = Pattern.compile(regularExpression);
        Matcher m = p.matcher(srcStr);

        while (m.find() && !regularExpression.equals("")) {

            resultSpan.setSpan(new ForegroundColorSpan(valueColor),
                    m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (size != 0) {
                resultSpan.setSpan(new AbsoluteSizeSpan(size, true), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return resultSpan;
    }

    //对外定义接口
    public void setPressedColor(int pressedColor) {
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);

        defaultTextColor = color;

        if (pressedTextColor == Color.TRANSPARENT) {
            pressedTextColor = defaultTextColor;
        }
        if (clickTextColor == Color.TRANSPARENT) {
            clickTextColor = defaultTextColor;
        }
    }

    public void setPressedTextColor(int pressedTextColor) {
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    public void setSolidColor(int solidColor) {

        pressedColor = solidColor;
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    public void setStrokeColor(int strokeColor) {
        pressedColor = strokeColor;

        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    public void setBtnAttr(int solidColor, int strokeColor, int pressedColor, int pressedTextColor, int angleCorner, int strokeWidth) {
        this.solidColor = solidColor;
        this.strokeColor = strokeColor;
        this.pressedColor = pressedColor;
        this.pressedTextColor = pressedTextColor;
        this.angleCorner = angleCorner;
        this.strokeWidth = strokeWidth;
        setBtnDrawable();
    }

    //实心的
    public void setSolidAttr(int solidColor, int pressedColor, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    public void setSolidAttr(int solidColor, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    public void setSolidAttr(int solidColor) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    //空心的
    public void setStrokeAttr(int strokeColor, int pressedColor, int strokeWidth, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    public void setStrokeAttr(int strokeColor, int strokeWidth, int angleCorner) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }

    public void setStrokeAttr(int strokeColor, int strokeWidth) {
        resetExAngle();
        setBtnAttr(solidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth);
    }
}
