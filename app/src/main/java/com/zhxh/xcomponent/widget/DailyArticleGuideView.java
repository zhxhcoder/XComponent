package com.zhxh.xcomponent.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.zhxh.xcomponent.R;

/**
 * Created by zhxh on 2019/05/06
 * 每日研读
 */
public class DailyArticleGuideView extends ConstraintLayout {

    private TextView mAssetNameTV; // 资产名字

    private TextView mAssetAmountTV; // 总额

    public DailyArticleGuideView(Context context) {
        super(context);
        init(context);
    }

    public DailyArticleGuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DailyArticleGuideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**
     * 方法描述：初始化
     *
     * @author renmeng
     * @time 2019/1/19
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.cfuturewealth_daily_article_guide, this);
        mAssetNameTV = findViewById(R.id.tv_asset_name);
    }

    /**
     * 方法描述：展示
     */
    public void show(DailyArticleData data) {

    }
}