package com.zhxh.xcomponent.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import com.zhxh.xcomponent.R

/**
 * Created by zhxh on 2019/09/20
 */
class PagerSelectView(ctx: Context?, attrs: AttributeSet?) : RelativeLayout(ctx, attrs) {
    private var mContext: Context? = null // 上下文
    private lateinit var mAnimationView: View// 执行动画的View

    init {
        mContext = ctx
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.cyiri_bottom_select_guide_layout, this)
    }


    //总是从底部弹出
    fun show(list: List<String>, listener: OnSelectListener) {


        list.forEach {
            val itemView = LayoutInflater.from(context)
                .inflate(R.layout.cyiri_bottom_select_guide_layout, null)


        }

        // 解决崩溃问题
        if (this.parent != null) {
            (this.parent as ViewGroup).removeView(this)
        }
        // 在当前Activity加入本View
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        (mContext as Activity).addContentView(this, layoutParams)

        mAnimationView = this@PagerSelectView


        this.visibility = View.VISIBLE
        // 执行由底部出来的动画
        val animation =
            AnimationUtils.loadAnimation(mContext, R.anim.cbase_push_top_in)
        mAnimationView.startAnimation(animation)
        // 解决点击穿透的问题
        this.isClickable = true
        this.setOnClickListener { close() }
    }


    fun close() {
        val animation =
            AnimationUtils.loadAnimation(mContext, R.anim.cbase_push_bottom_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                ((mContext as Activity).window.decorView.findViewById<View>(android.R.id.content) as ViewGroup)
                    .removeView(this@PagerSelectView)
                this@PagerSelectView.setVisibility(View.GONE)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        mAnimationView.startAnimation(animation)
    }


    interface OnSelectListener {
        fun onSelect(answerType: String)
    }
}

