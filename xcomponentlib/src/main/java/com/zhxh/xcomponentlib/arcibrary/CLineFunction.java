package com.zhxh.xcomponentlib.arcibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhxh on 2020/4/12
 */
public class CLineFunction extends View {

    float x, y;


    public CLineFunction(Context context) {
        super(context);
    }

    public CLineFunction(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    private float getCalcY() {

        return 0f;
    }


    private float getAxisY() {

        return 0f;
    }


    private float getAxisX() {

        return 0f;
    }


}
