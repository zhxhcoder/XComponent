package com.zhxh.xcomponentlib.tabIndicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhxh on 2018/7/2
 */

public class XTabItem extends View {
    public final CharSequence mText;
    public final Drawable mIcon;
    public final int mCustomLayout;

    public XTabItem(Context context) {
        this(context, null);
    }

    public XTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                com.zhxh.xcomponentlib.R.styleable.XTabItem, 0, 0);
        mText = a.getText(com.zhxh.xcomponentlib.R.styleable.XTabItem_android_text);
        mIcon = a.getDrawable(com.zhxh.xcomponentlib.R.styleable.XTabItem_android_icon);
        mCustomLayout = a.getResourceId(com.zhxh.xcomponentlib.R.styleable.XTabItem_android_layout, 0);
        a.recycle();
    }
}
