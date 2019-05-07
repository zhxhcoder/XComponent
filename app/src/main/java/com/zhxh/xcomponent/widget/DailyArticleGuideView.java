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

    private TextView tv_daily_article_guide_read;
    private TextView tv_daily_article_card_title;


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
        tv_daily_article_guide_read = findViewById(R.id.tv_daily_article_guide_read);
        tv_daily_article_card_title = findViewById(R.id.tv_daily_article_card_title);
    }

    /**
     * 方法描述：展示
     */
    public void show(DailyArticleData data) {

    }
}