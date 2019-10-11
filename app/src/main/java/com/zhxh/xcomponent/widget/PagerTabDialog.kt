package com.zhxh.xcomponent.widget

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.zhxh.xcomponent.R
import kotlinx.android.synthetic.main.pager_tab_dialog_item.view.*
import kotlinx.android.synthetic.main.pager_tab_dialog_layout.view.*

/**
 * Created by zhxh on 2019/10/11
 */
class PagerTabDialog(ctx: Context?, attrs: AttributeSet?) : RelativeLayout(ctx, attrs) {
    private var mContext: Context? = null // 上下文
    private lateinit var mAnimationView: View// 执行动画的View
    private var curIndex: Int = 0// 当前位置
    private var pageSelectListener: PageSelectListener? = null

    init {
        mContext = ctx
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.pager_tab_dialog_layout, this)
    }

    fun show(list: List<String>) {
        if (list.isNullOrEmpty()) {
            return
        }

        if (list.size <= 1) {
            indexContainer.visibility = View.GONE
        } else {
            indexContainer.visibility = View.VISIBLE
        }

        //内容区域点击无效
        contentLayout.setOnClickListener {}
        viewpager.adapter = MViewAdapter(list)
        viewpager.addOnPageChangeListener(MPageChangeListener(list))

        setTabLayout(curIndex, list)

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

        mAnimationView = this@PagerTabDialog

        this.visibility = View.VISIBLE
        // 执行由底部出来的动画
        val animation =
            AnimationUtils.loadAnimation(mContext, R.anim.cbase_push_top_in)
        mAnimationView.startAnimation(animation)
        // 解决点击穿透的问题
        this.isClickable = true
        this.setOnClickListener { close() }
    }


    fun setTabLayout(curIndex: Int, list: List<String>) {

        viewpager.currentItem = curIndex

        indexContainer.removeAllViews()
        list.forEachIndexed { index, item ->

            val dot = TextView(mContext)
            dot.width = dip2px(7f)
            dot.height = dip2px(7f)

            if (index == curIndex) {
                dot.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.main_color))
            } else {
                dot.setBackgroundColor(
                    ContextCompat.getColor(
                        mContext!!,
                        R.color.mask_color
                    )
                )
            }

            val layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            layoutParams.rightMargin = dip2px(10f)
            layoutParams.leftMargin = dip2px(10f)
            dot.layoutParams = layoutParams

            indexContainer.addView(dot)
        }
    }

    private fun close() {
        val animation =
            AnimationUtils.loadAnimation(mContext, R.anim.cbase_push_bottom_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                ((mContext as Activity).window.decorView.findViewById<View>(android.R.id.content) as ViewGroup)
                    .removeView(this@PagerTabDialog)
                this@PagerTabDialog.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
        mAnimationView.startAnimation(animation)
    }

    interface PageSelectListener {
        //监听滑动页
        fun pageSelect(index: Int)
    }

    private inner class MPageChangeListener(val list: List<String>) :
        ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        override fun onPageSelected(index: Int) {
            // 设置当前显示的布局下标
            this@PagerTabDialog.curIndex = index
            pageSelectListener?.pageSelect(index)
            setTabLayout(index, list)
        }
    }

    private inner class MViewAdapter(
        private var dataList: List<String>
    ) : PagerAdapter() {

        override fun getCount(): Int {
            return dataList.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item = dataList[position]

            val itemView = LayoutInflater.from(context)
                .inflate(R.layout.pager_tab_dialog_item, null)

            itemView.apply {
                tvTitle.text = item
                tvContent.text = item
            }
            container.addView(itemView)

            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    private fun dip2px(dpValue: Float): Int {
        if (context == null) {
            return 0
        }
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}

