package com.zhxh.xcomponent.dailyarticle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxh on 2019/05/06
 */
public class DailyArticleData {
    private int type;
    private String title;
    private String content;
    private List<DailyArticleData> innerList;

    public DailyArticleData(int type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public List<DailyArticleData> getInnerList() {
        innerList = new ArrayList<>();
        innerList.add(new DailyArticleData(0, "title1", "content1"));
        innerList.add(new DailyArticleData(0, "title2", "content2"));
        innerList.add(new DailyArticleData(0, "title3", "content3"));
        innerList.add(new DailyArticleData(0, "title4", "content4"));
        innerList.add(new DailyArticleData(0, "title5", "content5"));
        return innerList;
    }

    public void setInnerList(List<DailyArticleData> innerList) {
        this.innerList = innerList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
