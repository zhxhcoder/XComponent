package com.zhxh.xcomponent.dailyarticle

import java.util.ArrayList

/**
 * Created by zhxh on 2019/05/06
 */
class DailyArticleData(var type: Int, var title: String?, var content: String?) {
    var innerList: MutableList<DailyArticleData>? = null
        get() {
            this.innerList = ArrayList()
            field!!.add(DailyArticleData(0, "title1", "content1"))
            field!!.add(DailyArticleData(0, "title2", "content2"))
            field!!.add(DailyArticleData(0, "title3", "content3"))
            field!!.add(DailyArticleData(0, "title4", "content4"))
            field!!.add(DailyArticleData(0, "title5", "content5"))
            return field
        }
}
