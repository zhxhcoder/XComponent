package com.zhxh.xcomponentlib.tabIndicator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by zhxh on 2018/7/2
 */

public class AnimationUtils {

    public static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    public static int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (endValue - startValue));
    }

}