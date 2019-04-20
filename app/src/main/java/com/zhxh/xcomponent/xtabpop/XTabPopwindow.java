package com.zhxh.xcomponent.xtabpop;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhxh.xcomponent.R;
import com.zhxh.xcomponent.dummy.SelectItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现 title-list dataLinkedMap
 * 每次返回切换
 */
public class XTabPopwindow extends LinearLayout {

    Map<String, List<SelectItem>> dataLinkedMap = new LinkedHashMap<>();


    private Context mContext;
    private LayoutInflater mInflater;
    private String[] mTitles;
    private OnTabSelectListener mListener;//点击回调
    private SparseArray<View> mTitleViews;//保存一下我们的textView;
    private SparseArray<View> mLineViews;//保存一下我们的下划线

    public XTabPopwindow(Context context) {
        this(context, null);
        mContext = context;
    }

    public XTabPopwindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public XTabPopwindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mTitleViews = new SparseArray<>();
        mLineViews = new SparseArray<>();
        this.setOrientation(HORIZONTAL);
    }

    public void setTabSelectListener(OnTabSelectListener listener) {
        mListener = listener;
    }

    public void setData(String[] titles) {
        mTitles = titles;
        layoutViews();
    }

    private void layoutViews() {
        if (mTitles != null && mTitles.length != 0) {
            for (int i = 0; i < mTitles.length; i++) {
                final View childView = mInflater.inflate(R.layout.tab_layout_item, this, false);
                final TextView titleView = (TextView) childView.findViewById(R.id.tab_title);
                View indicator = childView.findViewById(R.id.tab_indicator);
                if (i == 0) {
                    titleView.setTextColor(0xFF2a4159);
                    indicator.setVisibility(VISIBLE);
                }
                titleView.setText(mTitles[i]);

                /*                indicator.setLayoutParams(new ViewGroup.LayoutParams((int) titleView.getPaint().measureText(titleView.getText().toString()), CommonDataManager.getDensityValue(2, mContext)));*/

                this.addView(childView);
                final int position = i;
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onTabSelect(position);
                            setTabTitleColor(position);
                        }
                    }
                });

                mTitleViews.put(i, titleView);
                mLineViews.put(i, indicator);
            }

        }
    }

    private void setTabTitleColor(int position) {
        if (mTitleViews != null && mTitleViews.size() != 0) {
            for (int key = 0; key < mTitleViews.size(); key++) {
                TextView titleView = (TextView) mTitleViews.get(key);
                View lineView = mLineViews.get(key);
                titleView.setTextColor(position == key ? 0xFF2a4159 : 0xFF8997A5);
                lineView.setVisibility(position == key ? VISIBLE : INVISIBLE);
            }
        }
    }

    public void setTabSelect(int position) {
        setTabTitleColor(position);
        if (mListener != null) {
            mListener.onTabSelect(position);
        }
    }

    public interface OnTabSelectListener {
        void onTabSelect(int position);
    }

}
