package com.zhxh.xcomponent.dailyarticle

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zhxh.xcomponent.R

/**
 * Created by zhxh on 2019/05/06
 * 每日研读
 */
class DailyArticleGuideView : LinearLayout {

    private lateinit var ctx: Context

    private var tv_daily_article_guide_read: TextView? = null
    private var tv_daily_article_card_title: TextView? = null
    private var tv_daily_article_card_num: TextView? = null
    private var tv_daily_article_card_day: TextView? = null
    private lateinit var iv_daily_article_card_right: ImageView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    /**
     * 方法描述：初始化
     *
     * @author renmeng
     * @time 2019/1/19
     */
    private fun init(context: Context) {
        ctx = context
        LayoutInflater.from(context).inflate(R.layout.cfuturewealth_daily_article_guide, this)
        tv_daily_article_guide_read = findViewById(R.id.tv_daily_article_guide_read)
        tv_daily_article_card_title = findViewById(R.id.tv_daily_article_card_title)
        tv_daily_article_card_num = findViewById(R.id.tv_daily_article_card_num)
        tv_daily_article_card_day = findViewById(R.id.tv_daily_article_card_day)
        iv_daily_article_card_right = findViewById(R.id.iv_daily_article_card_right)

        this.visibility = View.GONE
    }

    /**
     * 方法描述：展示
     */
    fun show(data: DailyArticleData?) {
        if (data != null) {
            this.visibility = View.VISIBLE
            tv_daily_article_guide_read!!.text = data.title
            tv_daily_article_card_title!!.text = data.content
            tv_daily_article_card_num!!.text = data.title
            tv_daily_article_card_day!!.text = data.title

            //设置图片圆角角度
            val roundedCorners = RoundedCorners(6)
            //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
            val options = RequestOptions.bitmapTransform(roundedCorners).override(120, 120)
            ctx.let { Glide.with(it).load(R.mipmap.ic_reclogo).apply(options).into(iv_daily_article_card_right) }

            this.setOnClickListener { v ->
                ctx.startActivity(Intent(ctx, DailyArticleListActivity::class.java))
                //ctx.startActivity(new Intent(ctx, DailyArticleCommonActivity.class));
            }
        } else {
            this.visibility = View.GONE
        }
    }
}