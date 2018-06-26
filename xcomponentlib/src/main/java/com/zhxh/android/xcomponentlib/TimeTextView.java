package com.zhxh.android.xcomponentlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeTextView extends android.support.v7.widget.AppCompatTextView implements Runnable {

    Paint mPaint;

    private long mday, mhour, mmin, msecond;

    private boolean isRun = false;

    private onFinishCallBack callBack;

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        mPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeTextView);
        a.recycle();
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }
        mPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeTextView);
        a.recycle();

    }

    public TimeTextView(Context context) {
        super(context);
    }

    public void setOnFinish(onFinishCallBack callBack) {
        this.callBack = callBack;
    }

    public void setTimes(List<Long> times) {
        mday = times.size() > 0 ? times.get(0) : 0;
        mhour = times.size() > 1 ? times.get(1) : 0;
        mmin = times.size() > 2 ? times.get(2) : 0;
        msecond = times.size() > 3 ? times.get(3) : 0;

        if (!isRun()) {
            run();
        }
    }

    public void setTimes(String strTimes) {
        List<Long> times = new ArrayList<>();
        for (String item : getRegEx(strTimes, "\\d+")) {
            times.add(Long.valueOf(item));
        }
        setTimes(times);
    }

    public void setTimes(long seconds) {

        String strTimes;

        long mday, mhour, mmin, msecond;

        mday = seconds / (24 * 60 * 60);
        mhour = (seconds - mday * 24 * 60 * 60) / (60 * 60);
        mmin = (seconds - mday * 24 * 60 * 60 - mhour * 60 * 60) / 60;
        msecond = (seconds - mday * 24 * 60 * 60 - mhour * 60 * 60 - mmin * 60);
        strTimes = mday + "天" + mhour + "时" + mmin + "分钟" + msecond + "秒";

        setTimes(strTimes);
    }

    private void computeTime() {
        msecond--;
        if (msecond < 0) {
            mmin--;
            msecond = 59;
            if (mmin < 0) {
                mmin = 59;
                mhour--;
                if (mhour < 0) {
                    mhour = 59;
                    mday--;
                }
            }
        }
    }

    private void finishTimer() {

        if (callBack != null) {
            callBack.onFinish();
        }
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        this.isRun = run;
    }

    @Override
    public void run() {

        setRun(true);

        computeTime();

        if (mday == 0 && mhour == 0 && mmin == 0 && msecond == 0) {
            finishTimer();
            return;
        }

        String strTime = mday + " 天 " + mhour + " 时 " + mmin + " 分 " + msecond + " 秒 ";

        this.setText(getSizeSpanReg(strTime.replaceAll("(?<=\\b|\\D)(?=\\d\\D)", "0"), "\\d+", 36));

        postDelayed(this, 1000);

    }

    public interface onFinishCallBack {

        void onFinish();
    }

    public static SpannableString getSizeSpanReg(String srcStr, String regularExpression, int size) {

        SpannableString resultSpan = new SpannableString(srcStr);

        Pattern p = Pattern.compile(regularExpression);
        Matcher m = p.matcher(srcStr);

        while (m.find() && !regularExpression.equals("")) {

            resultSpan.setSpan(new AbsoluteSizeSpan(size, true), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return resultSpan;
    }

    public static List<String> getRegEx(String input, String regex) {
        List<String> stringList = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        while (m.find())
            stringList.add(m.group());

        return stringList;
    }
}
