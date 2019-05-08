package com.zhxh.xcomponent.dailyarticle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhxh.xcomponent.R;

import java.util.List;

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

        LinearLayout ll_daily_article_inner_container = helper.getView(R.id.ll_daily_article_inner_container);
        //setInnerList(ll_daily_article_inner_container, item.getInnerList());
    }

    private void setInnerList(LinearLayout container, List<DailyArticleData> dataList) {
        if (dataList != null && dataList.size() > 0) {
            container.setVisibility(View.VISIBLE);
            container.removeAllViews();

            for (int i = 0; i < dataList.size(); i++) {

                final DailyArticleData data = dataList.get(i);

                View itemView = LayoutInflater.from(ctx).inflate(R.layout.cfuturewealth_item_daily_article_inner, null);

                View line_top = itemView.findViewById(R.id.line_top);
                TextView tv_inner_title = itemView.findViewById(R.id.tv_inner_title);
                tv_inner_title.setText(data.getTitle() + ":" + data.getType());

                if (i == 0) {
                    line_top.setVisibility(View.GONE);
                } else {
                    line_top.setVisibility(View.VISIBLE);
                }
                container.addView(itemView);
                itemView.setOnClickListener(v -> {
                    Toast.makeText(ctx, data.getTitle() + ":" + data.getType(), Toast.LENGTH_LONG).show();
                });
            }

        } else {
            container.setVisibility(View.GONE);
        }
    }
}
