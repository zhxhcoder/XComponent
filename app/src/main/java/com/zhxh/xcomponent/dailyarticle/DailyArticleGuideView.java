package com.zhxh.xcomponent.dailyarticle;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.zhxh.xcomponent.R;

/**
 * Created by zhxh on 2019/05/06
 * 每日研读
 */
public class DailyArticleGuideView extends LinearLayout {

    private Context ctx;

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
        ctx = context;
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

            //设置图片圆角角度
            RoundedCorners roundedCorners = new RoundedCorners(6);
            //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(120, 120);
            Glide.with(ctx).load(R.mipmap.ic_reclogo).apply(options).into(iv_daily_article_card_right);

            this.setOnClickListener(v -> {
                ctx.startActivity(new Intent(ctx, DailyArticleListActivity.class));
            });
        } else {
            this.setVisibility(GONE);
        }
    }
}