package com.zhxh.xcomponentlib.utils;

/**
 * Created by zhxh on 2018/8/17
 */
public final class BtnUtils {

    private static long lastClickTime;

    public synchronized static boolean isQuickClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
