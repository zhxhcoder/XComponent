package com.zhxh.xcomponent.dailyarticle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.zhxh.xcomponent.R

import java.util.ArrayList

class DailyArticleListActivity : AppCompatActivity() {

    internal var page = 1
    internal var recyclerView: RecyclerView
    internal var listAdapter: DailyArticleListAdapter
    private var headerView: View? = null

    internal var onScrollListener: RecyclerView.OnScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cfuturewealth_daily_article_list_new)

        initView()

        initAdapter()

        loadData()


        onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //获得recyclerView的线性布局管理器
                val manager = recyclerView.layoutManager as LinearLayoutManager?
                //获取到第一个item的显示的下标  不等于0表示第一个item处于不可见状态 说明列表没有滑动到顶部 显示回到顶部按钮
                val firstVisibleItemPosition = manager!!.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition == 0) {
                    //TODO 滑动头部
                    loadMoreData()
                }
            }
        }
        recyclerView.addOnScrollListener(onScrollListener)
    }

    private fun initView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.item_listview_popwin, null)

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
    }

    private fun initAdapter() {
        listAdapter = DailyArticleListAdapter(this, R.layout.cfuturewealth_item_daily_article_layout)
        listAdapter.setEnableLoadMore(false)
        listAdapter.setHeaderAndEmpty(true)

        listAdapter.bindToRecyclerView(recyclerView)

        val empty = TextView(this)
        empty.text = "数据已经空啦"
        empty.gravity = Gravity.CENTER
        empty.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        listAdapter.emptyView = empty
        listAdapter.emptyView.setOnClickListener { v -> Toast.makeText(this@DailyArticleListActivity, "empty", Toast.LENGTH_LONG).show() }
    }

    private fun loadMoreData() {
        page++
        loadData()
    }

    private fun loadData() {

        Log.d("xxxxxxx", "loadData：$page")

        val tempList = ArrayList<DailyArticleData>()
        tempList.add(DailyArticleData(page, "title1", "content1"))
        tempList.add(DailyArticleData(page, "title2", "content2"))
        tempList.add(DailyArticleData(page, "title3", "content3"))
        tempList.add(DailyArticleData(page, "title4", "content4"))
        tempList.add(DailyArticleData(page, "title5", "content5"))
        tempList.add(DailyArticleData(page, "title6", "content6"))
        tempList.add(DailyArticleData(page, "title7", "content7"))

        if (page == 1) {
            if (tempList != null && tempList.size > 0) {
                listAdapter.setNewData(tempList)
            } else {
                listAdapter.setNewData(null)
                //TODO 加载完成
                recyclerView.removeOnScrollListener(onScrollListener)
            }
        } else {
            if (page == 9) {
                //TODO 加载完成
                recyclerView.removeOnScrollListener(onScrollListener)

                headerView!!.visibility = View.VISIBLE
                listAdapter.addHeaderView(headerView)
            } else {
                listAdapter.addData(0, tempList)
            }
        }
    }
}
