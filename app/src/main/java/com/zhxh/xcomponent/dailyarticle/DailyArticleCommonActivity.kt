package com.zhxh.xcomponent.dailyarticle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import com.zhxh.xcomponent.R

import java.util.ArrayList

/**
 * Created by zhxh on 2019/05/09
 */
class DailyArticleCommonActivity : AppCompatActivity() {

    private var page = 1
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: DailyArticleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cfuturewealth_daily_article_list)

        initView()

        initAdapter()

        loadData()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
    }

    private fun initAdapter() {
        listAdapter = DailyArticleListAdapter(this, R.layout.cfuturewealth_item_daily_article_layout)
        listAdapter.setEnableLoadMore(true)
        listAdapter.setHeaderAndEmpty(false)
        listAdapter.setOnLoadMoreListener({
            page++
            loadData()
        }, recyclerView)

        recyclerView.adapter = listAdapter
    }

    private fun loadData() {

        val tempList = ArrayList<DailyArticleData>()
        tempList.add(DailyArticleData(page, "title1", "content1"))
        tempList.add(DailyArticleData(page, "title2", "content2"))
        tempList.add(DailyArticleData(page, "title3", "content3"))
        tempList.add(DailyArticleData(page, "title4", "content4"))
        tempList.add(DailyArticleData(page, "title5", "content5"))

        if (page == 1) {
            if (tempList != null && tempList.size > 0) {
                listAdapter.setNewData(tempList)
            } else {
                listAdapter.loadMoreComplete()
            }
        } else {
            if (page == 4) {
                listAdapter.loadMoreEnd()
            } else {
                listAdapter.loadMoreComplete()
                listAdapter.addData(tempList)
            }
        }
    }
}
