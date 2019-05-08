package com.zhxh.xcomponent.dailyarticle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhxh.xcomponent.R;

/**
 * Created by zhxh on 2019/05/06
 * 每日研读
 */
public class DailyArticleGuideView extends LinearLayout {

    private TextView tv_daily_article_guide_read;
    private TextView tv_daily_article_card_title;
    private TextView tv_daily_article_card_num;
    private TextView tv_daily_article_card_day;
    private ImageView iv_daily_article_card_right;

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
        tv_daily_article_card_num = findViewById(R.id.tv_daily_article_card_num);
        tv_daily_article_card_day = findViewById(R.id.tv_daily_article_card_day);
        iv_daily_article_card_right = findViewById(R.id.iv_daily_article_card_right);

        this.setVisibility(GONE);
    }

    /**
     * 方法描述：展示
     */
    public void show(DailyArticleData data) {
        if (data != null) {
            this.setVisibility(VISIBLE);
            tv_daily_article_guide_read.setText(data.getTitle());
            tv_daily_article_card_title.setText(data.getContent());
            tv_daily_article_card_num.setText(data.getTitle());
            tv_daily_article_card_day.setText(data.getTitle());

            this.setOnClickListener(v -> {
                //TODO
            });
        } else {
            this.setVisibility(GONE);
        }
    }
}