package com.zhxh.xcomponent.dummy

import com.google.gson.annotations.SerializedName
import com.zhxh.xchartlib.entity.IAxisValue

/**
 * Created by zhxh on 2018/6/28
 */
class ChartData(@field:SerializedName(value = "date", alternate = ["logDay"])
                var date: String?, @field:SerializedName(value = "value", alternate = ["yield"])
                var value: String?) : IAxisValue {

    @SerializedName(value = "name", alternate = ["UserName"])
    var name: String? = null
    @SerializedName(value = "list", alternate = ["DayInitsAsstes"])
    var list: List<ChartData>? = null

    override fun xValue(): String? {
        return date
    }

    override fun yValue(): Float {
        return java.lang.Float.parseFloat(value!!)
    }

    override fun flagValue(): Int {
        return 0
    }
}
