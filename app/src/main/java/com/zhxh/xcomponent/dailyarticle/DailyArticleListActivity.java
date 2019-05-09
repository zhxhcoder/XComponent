package com.zhxh.xcomponent.dailyarticle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhxh.xcomponent.R;

import java.util.ArrayList;
import java.util.List;

public class DailyArticleListActivity extends AppCompatActivity {

    int page = 1;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DailyArticleListAdapter listAdapter;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cfuturewealth_daily_article_list);

        initView();

        initAdapter();

        loadData();

        refreshLayout.setOnRefreshListener(
            () -> {
                page++;
                loadData();
            }
        );
    }

    private void initView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.item_listview_popwin, null);

        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initAdapter() {
        listAdapter = new DailyArticleListAdapter(this, R.layout.cfuturewealth_item_daily_article_layout);
        listAdapter.setEnableLoadMore(false);
        listAdapter.setHeaderAndEmpty(true);

        listAdapter.bindToRecyclerView(recyclerView);

        TextView empty = new TextView(this);
        empty.setText("数据已经空啦");
        empty.setGravity(Gravity.CENTER);
        empty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        listAdapter.setEmptyView(empty);
        listAdapter.getEmptyView().setOnClickListener(v -> {
            Toast.makeText(DailyArticleListActivity.this, "empty", Toast.LENGTH_LONG).show();
        });
    }

    private void loadData() {

        Log.d("loadData", "loadData:" + page);

        List<DailyArticleData> tempList = new ArrayList<>();
        tempList.add(new DailyArticleData(page, "title1", "content1"));
        tempList.add(new DailyArticleData(page, "title2", "content2"));
        tempList.add(new DailyArticleData(page, "title3", "content3"));
        tempList.add(new DailyArticleData(page, "title4", "content4"));
        tempList.add(new DailyArticleData(page, "title5", "content5"));

        if (page == 1) {
            if (tempList != null && tempList.size() > 0) {
                listAdapter.setNewData(tempList);
            } else {
                listAdapter.setNewData(null);
                refreshLayout.setEnabled(false);
            }
        } else {
            if (page == 4) {
                //终止加载更多数据
                headerView.setVisibility(View.VISIBLE);
                listAdapter.addHeaderView(headerView);
                refreshLayout.setOnRefreshListener(null);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
                listAdapter.notifyDataSetChanged();
            } else {
                listAdapter.addData(0, tempList);
                refreshLayout.setRefreshing(false);
            }
        }
    }
}
