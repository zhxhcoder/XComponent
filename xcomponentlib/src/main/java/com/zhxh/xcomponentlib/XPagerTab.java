package com.zhxh.xcomponentlib;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.Locale;

public class XPagerTab extends HorizontalScrollView {

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[] {
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;
    /**选中的底线颜色*/
    private int indicatorColor = 0xFFef3c3c;
    private int underlineColor = 0xFFa1acbf;
    private int dividerColor = 0x00000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;
    /**移动切换位移*/
    private int scrollOffset = 32;
    private int indicatorHeight = 2;
    private int underlineHeight = 4;
    private int dividerPadding = 6;
    private int tabPadding = 14;
    private int dividerWidth = 1;

    private int tabTextSize;
    /**选中字体大小*/
    private int tabSelectedTextSize;
    //	private int tabTextColor = 0xFF999999;
//	/**选中文字颜色*/
//	private int tabTextColorSelected = 0xFF000000;
    private int tabTextColor = 0xFF333333;
    public int tabTextColorSelected = 0xFFef3c3c;

    private int tabTextNightColor = 0xFF8997a5;
    private int tabTextNightColorSelected = 0xFFFFFFFF;

    DisplayMetrics dm = getResources().getDisplayMetrics();

    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;
    /**点击选中背景*/
    private int tabBackgroundResId = R.drawable.background_tab;

    private Locale locale;
    /**是否显示新样式*/
    public boolean newStyle = false;
    /**控件所占宽度 默认屏幕宽*/
    public int controlWidth = CommonDataManager. screenWight;
    /**是否支持换肤*/
    public boolean skinMode = false;
    private int indicatorImgResId = R.drawable.market_tab_selected;

    public XPagerTab(Context context) {
        this(context, null);
    }

    public XPagerTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XPagerTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);


        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
//    underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        underlineHeight = 1;
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
//    dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);

        //tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
        //tabSelectedTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabSelectedTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        //tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        //tabSelectedTextSize = a.getDimensionPixelSize(0, tabSelectedTextSize);
        tabTextColor = a.getColor(R.styleable.XPagerTab_pstsUnderlineColor, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.XPagerTab);

        indicatorColor = a.getColor(R.styleable.XPagerTab_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.XPagerTab_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.XPagerTab_pstsDividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.XPagerTab_pstsIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.XPagerTab_pstsUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.XPagerTab_pstsDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.XPagerTab_pstsTabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.XPagerTab_pstsTabBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.XPagerTab_pstsShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.XPagerTab_pstsScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.XPagerTab_pstsTextAllCaps, textAllCaps);

        newStyle = a.getBoolean(R.styleable.XPagerTab_pstsNewStyle, newStyle);
        skinMode = a.getBoolean(R.styleable.XPagerTab_pstsSkinMode, skinMode);
        tabTextColor = a.getColor(R.styleable.XPagerTab_pstsTabTextColor, tabTextColor);
        tabTextNightColor = a.getColor(R.styleable.XPagerTab_pstsTabTextNightColor, tabTextNightColor);
        tabTextColorSelected = a.getColor(R.styleable.XPagerTab_pstsTabTextColorSelected, tabTextColorSelected);
        tabTextNightColorSelected = a.getColor(R.styleable.XPagerTab_pstsTabTextNightColorSelected, tabTextNightColorSelected);
        tabTextSize = a.getInteger(R.styleable.XPagerTab_pstsTabTextSize, tabTextSize);
        tabSelectedTextSize = a.getInteger(R.styleable.XPagerTab_pstsTabSelectedTextSize, tabSelectedTextSize);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }

        if (newStyle) {
            //TODO
            ViewTreeObserver vto2 = getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    controlWidth = getWidth();

                    if (skinMode && MyApplication.SKIN_MODE == MyApplication.SKIN_MODE_NIGHT) {
                        XPagerTab.this.setBackgroundColor(context.getResources().getColor(R.color.colorWindowBackground));
                        tabTextColor = tabTextNightColor;
                        tabTextColorSelected = tabTextNightColorSelected;
                    }

                    notifyDataSetChanged(0);
                }
            });

        } else {
            if (skinMode && MyApplication.SKIN_MODE == MyApplication.SKIN_MODE_NIGHT) {
                XPagerTab.this.setBackgroundColor(context.getResources().getColor(R.color.colorWindowBackground));
                tabTextColor = tabTextNightColor;
                tabTextColorSelected = tabTextNightColorSelected;
            } else {
                tabTextColor = 0xFF666666;
                tabTextColorSelected = 0xFF000000;
            }

            tabTextSize = 14;
            tabSelectedTextSize = 14;
        }


    }

    /***
     * 设置有分隔线
     */
    public void setDivider() {

        dividerColor = 0xFFd2d2d2;
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged(0);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void setOnPageClickNotifyListener(NotifyListener listener) {

        this.notifyListener = listener;
    }

    /**
     * 根据按钮数量计算实际距离 为了铺满屏幕
     */
    private int caculatePadding() {
        //平均每个按钮的距离
        int avgWidth = 0;

        if(tabCount>=5)
            avgWidth = (int) ((controlWidth)/5.3);
        else
            avgWidth = controlWidth/tabCount;

        return avgWidth;
    }

    public void notifyDataSetChanged(int selectItem) {

        tabsContainer.removeAllViews();

        if(null == pager)
            return;

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles(selectItem);

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    private void addTextTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setTextColor(tabTextColorSelected);

        if(newStyle)
            tab.setTextSize(tabSelectedTextSize);

        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        int avgWidth = caculatePadding();
        TextPaint paint = new TextPaint();
        Rect rect = new Rect();

        paint.setTextSize(tabTextSize * dm.scaledDensity);
        paint.getTextBounds(title, 0, title.length(), rect);
        int w = rect.width();
        tabPadding = (avgWidth - w)/2;

        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }

    private void addTab(final int position, final View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);

                if(null !=notifyListener)
                    notifyListener.onNotify(position);

                v = tabsContainer.getChildAt(position);
                if (v instanceof TextView) {

                    TextView tab = (TextView) v;

                    for (int i = 0; i <=tabCount-1; i++) {
                        TextView tabView = (TextView) tabsContainer.getChildAt(i);
                        tabView.setTextSize(tabTextSize);
                    }
                    tab.setTextColor(tabTextColorSelected);

                    if(newStyle)
                        tab.setTextSize(tabSelectedTextSize);
                }
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
//		tab.setPadding(tabPadding*2/3, 0, tabPadding*2/3, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }

    }

    private void updateTabStyles(int selectItem) {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);

                if(i== selectItem) {
                    tab.setTextColor(tabTextColorSelected);

                    if(newStyle)
                        tab.setTextSize(tabSelectedTextSize);
                }

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }

    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            Message msg = new Message();
            msg.what = newScrollX;
            msg.arg1 = position;
            handler.sendMessageDelayed(msg, 300);
        }

    }

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            int position = msg.arg1;
            int newScrollX =  msg.what;

            if(pager.getCurrentItem()>=4) {

                smoothScrollTo(newScrollX*3, 0);
            }else {

                smoothScrollTo(0, 0);
            }
        };
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);
        Bitmap bitmapBg = BitmapFactory.decodeResource(getResources(), indicatorImgResId);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if(!newStyle) {

            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);
            // draw underline
            rectPaint.setColor(underlineColor);
            canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);
        } else {

            canvas.drawBitmap(bitmapBg, (lineLeft + lineRight) / 2 - bitmapBg.getWidth() / 2, height - indicatorHeight, rectPaint);
        }
//		canvas.drawRect(lineLeft + 3 * (lineRight - lineLeft) / 8, height - indicatorHeight, lineRight - 3 * (lineRight - lineLeft) / 8, height, rectPaint);

        // draw divider

        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }

            currentPosition = position;
            currentPositionOffset = 0;

            setTextColor();
            View v = tabsContainer.getChildAt(position);
            if (v instanceof TextView) {

                TextView tab = (TextView) v;

                for (int i = 0; i <=tabCount-1; i++) {
                    TextView tabView = (TextView) tabsContainer.getChildAt(i);
                    tabView.setTextSize(tabTextSize);
                }
                tab.setTextColor(tabTextColorSelected);
                if(newStyle)
                    tab.setTextSize(tabSelectedTextSize);
            }
        }

    }

    private void setTextColor() {

        if(null != tabsContainer) {

            for(int i = 0;i<tabsContainer.getChildCount();i++) {

                View v = tabsContainer.getChildAt(i);
                if (v instanceof TextView) {

                    TextView tab = (TextView) v;
                    tab.setTextColor(tabTextColor);
                }
            }

        }
    }

    /***
     * 设置文字选中颜色
     * @param indicatorColor
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        this.tabTextColorSelected = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    public void setIndicatorImgResId(int indicatorImgResId) {
        this.indicatorImgResId = indicatorImgResId;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}