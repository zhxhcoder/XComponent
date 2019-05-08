package com.zhxh.xcomponent.dailyarticle;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhxh.xcomponent.R;

/**
 * Created by zhxh on 2019/05/08
 */
public class DailyArticleListAdapter extends BaseQuickAdapter<DailyArticleData, BaseViewHolder> {
    private Context ctx;

    public DailyArticleListAdapter(Context ctx, int layoutResId) {
        super(layoutResId);
        this.ctx = ctx;
    }

    @Override
    protected void convert(BaseViewHolder helper, DailyArticleData item) {
        helper.setText(R.id.tv_daily_article_date, item.getTitle());
        helper.setText(R.id.tv_daily_article_read, item.getType() + "");
    }
}
