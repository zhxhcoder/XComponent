package com.zhxh.xcomponent.dailyarticle;

import android.support.annotation.NonNull;
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
    DailyArticleListAdapter listAdapter;
    private View headerView;

    RecyclerView.OnScrollListener onScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cfuturewealth_daily_article_list_new);

        initView();

        initAdapter();

        loadData();


        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //获得recyclerView的线性布局管理器
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //获取到第一个item的显示的下标  不等于0表示第一个item处于不可见状态 说明列表没有滑动到顶部 显示回到顶部按钮
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition == 0) {
                    //TODO 滑动头部
                    loadMoreData();
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.item_listview_popwin, null);

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
        empty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listAdapter.setEmptyView(empty);
        listAdapter.getEmptyView().setOnClickListener(v -> {
            Toast.makeText(DailyArticleListActivity.this, "empty", Toast.LENGTH_LONG).show();
        });
    }

    private void loadMoreData() {
        page++;
        loadData();
    }

    private void loadData() {

        Log.d("xxxxxxx", "loadData：" + page);

        List<DailyArticleData> tempList = new ArrayList<>();
        tempList.add(new DailyArticleData(page, "title1", "content1"));
        tempList.add(new DailyArticleData(page, "title2", "content2"));
        tempList.add(new DailyArticleData(page, "title3", "content3"));
        tempList.add(new DailyArticleData(page, "title4", "content4"));
        tempList.add(new DailyArticleData(page, "title5", "content5"));
        tempList.add(new DailyArticleData(page, "title6", "content6"));
        tempList.add(new DailyArticleData(page, "title7", "content7"));

        if (page == 1) {
            if (tempList != null && tempList.size() > 0) {
                listAdapter.setNewData(tempList);
            } else {
                listAdapter.setNewData(null);
                //TODO 加载完成
                recyclerView.removeOnScrollListener(onScrollListener);
            }
        } else {
            if (page == 9) {
                //TODO 加载完成
                recyclerView.removeOnScrollListener(onScrollListener);

                headerView.setVisibility(View.VISIBLE);
                listAdapter.addHeaderView(headerView);
            } else {
                listAdapter.addData(0, tempList);
            }
        }
    }
}
