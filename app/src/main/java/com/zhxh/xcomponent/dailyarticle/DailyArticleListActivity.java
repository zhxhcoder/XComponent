package com.zhxh.xcomponent.dailyarticle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhxh.xcomponent.R;

import java.util.ArrayList;
import java.util.List;

public class DailyArticleListActivity extends AppCompatActivity {

    int page = 1;
    RecyclerView dailyList;
    DailyArticleListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cfuturewealth_daily_article_list);

        initView();

        initAdapter();

        loadData();
    }

    private void initView() {
        dailyList = findViewById(R.id.dailyList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dailyList.setLayoutManager(layoutManager);
    }

    private void initAdapter() {
        listAdapter = new DailyArticleListAdapter(this, R.layout.cfuturewealth_item_daily_article_layout);
        listAdapter.setEnableLoadMore(true);
        listAdapter.setHeaderAndEmpty(false);
        listAdapter.setOnLoadMoreListener(() -> {
            page++;
            loadData();
        }, dailyList);

        dailyList.setAdapter(listAdapter);
    }

    private void loadData() {

        List<DailyArticleData> tempList = new ArrayList<>();
        tempList.add(new DailyArticleData(page, "title1", "content1"));
        tempList.add(new DailyArticleData(page, "title2", "content2"));
        tempList.add(new DailyArticleData(page, "title3", "content3"));
        tempList.add(new DailyArticleData(page, "title4", "content4"));
        tempList.add(new DailyArticleData(page, "title5", "content5"));

        if (page == 1) {
            if (tempList != null || tempList.size() == 0) {
                listAdapter.setNewData(tempList);
            } else {
                listAdapter.loadMoreComplete();
            }
        } else {
            if (page == 4) {
                listAdapter.loadMoreEnd();
            } else {
                listAdapter.loadMoreComplete();
                listAdapter.addData(tempList);
            }
        }
    }
}
