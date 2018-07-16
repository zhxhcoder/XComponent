package com.zhxh.xcomponentlib;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 在edittext中可以点击的drawable
 * Created by zhxh on 2018/7/16
 */
public class XEditText extends AppCompatEditText {

    private Drawable drawableRight;
    private Drawable drawableLeft;
    private Drawable drawableTop;
    private Drawable drawableBottom;
    private int positionX;
    private int positionY;

    private onDrawableClickListener onDrawableClickListener;

    public enum DrawablePosition {
        LEFT,
        RIGHT
    }

    public XEditText(Context context) {
        super(context);
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {

        if (left != null) drawableLeft = left;
        if (right != null) drawableRight = right;
        if (top != null) drawableTop = top;
        if (bottom != null) drawableBottom = bottom;

        super.setCompoundDrawables(left, top, right, bottom);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect bounds;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            positionX = (int) event.getX();
            positionY = (int) event.getY();

            if (drawableLeft != null) {
                bounds = drawableLeft.getBounds();

                int xClickPosition;
                int yClickPosition;
                /*
                 * @return pixels into dp
                 */
                int extraClickArea = (int) (13 * getResources().getDisplayMetrics().density + 0.5);

                xClickPosition = positionX;
                yClickPosition = positionY;

                if (!bounds.contains(positionX, positionY)) {
                    /** Gives some extra space for tapping.  */
                    xClickPosition = positionX - extraClickArea;
                    yClickPosition = positionY - extraClickArea;

                    if (xClickPosition <= 0) xClickPosition = positionX;
                    if (yClickPosition <= 0) yClickPosition = positionY;

                    /** Creates square from the smallest value  from x or y*/
                    if (xClickPosition < yClickPosition) yClickPosition = xClickPosition;
                }

                if (bounds.contains(xClickPosition, yClickPosition) && onDrawableClickListener != null) {
                    onDrawableClickListener.onClick(DrawablePosition.LEFT);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;

                }
            }

            if (drawableRight != null) {
                bounds = drawableRight.getBounds();
                int xClickPosition;
                int yClickPosition;
                int extraClickingArea = 13;

                xClickPosition = positionX + extraClickingArea;
                yClickPosition = positionY - extraClickingArea;

                /**
                 * It right drawable -> subtract the value of x from the width of view. so that width - tapped area                     * will result in x co-ordinate in drawable bound.
                 */
                xClickPosition = getWidth() - xClickPosition;
                if (xClickPosition <= 0) xClickPosition += extraClickingArea;

                /* If after calculating for extra clickable area is negative.
                 * assign the original value so that after subtracting
                 * extra clicking area value doesn't go into negative value.
                 */

                if (yClickPosition <= 0) yClickPosition = positionY;
                /**If drawable bounds contains the x and y points then move ahead. */
                if (bounds.contains(xClickPosition, yClickPosition) && onDrawableClickListener != null) {
                    onDrawableClickListener.onClick(DrawablePosition.RIGHT);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;
                }
                return super.onTouchEvent(event);
            }

        }
        return super.onTouchEvent(event);
    }

    public interface onDrawableClickListener {
        void onClick(DrawablePosition target);
    }

    public void setDrawableClickListener(onDrawableClickListener onDrawableClickListener) {
        this.onDrawableClickListener = onDrawableClickListener;
    }

}
