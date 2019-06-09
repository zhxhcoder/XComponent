package com.zhxh.xcomponent.dailyarticle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhxh.xcomponent.R

/**
 * Created by zhxh on 2019/05/08
 */
class DailyArticleListAdapter(private val ctx: Context, layoutResId: Int) : BaseQuickAdapter<DailyArticleData, BaseViewHolder>(layoutResId) {

    override fun convert(helper: BaseViewHolder, item: DailyArticleData) {
        helper.setText(R.id.tv_daily_article_date, item.title)
        helper.setText(R.id.tv_daily_article_read, item.type.toString() + "")

        val ll_daily_article_inner_container = helper.getView<LinearLayout>(R.id.ll_daily_article_inner_container)
        //setInnerList(ll_daily_article_inner_container, item.getInnerList());
    }

    private fun setInnerList(container: LinearLayout, dataList: List<DailyArticleData>?) {
        if (dataList != null && dataList.size > 0) {
            container.visibility = View.VISIBLE
            container.removeAllViews()

            for (i in dataList.indices) {

                val data = dataList[i]

                val itemView = LayoutInflater.from(ctx).inflate(R.layout.cfuturewealth_item_daily_article_inner, null)

                val line_top = itemView.findViewById<View>(R.id.line_top)
                val tv_inner_title = itemView.findViewById<TextView>(R.id.tv_inner_title)
                tv_inner_title.text = data.title + ":" + data.type

                if (i == 0) {
                    line_top.visibility = View.GONE
                } else {
                    line_top.visibility = View.VISIBLE
                }
                container.addView(itemView)
                itemView.setOnClickListener { v -> Toast.makeText(ctx, data.title + ":" + data.type, Toast.LENGTH_LONG).show() }
            }

        } else {
            container.visibility = View.GONE
        }
    }
}
