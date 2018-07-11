package com.zhxh.xcomponent.dummy;

import com.google.gson.annotations.SerializedName;
import com.zhxh.xchartlib.entity.IAxisValue;

import java.util.List;

/**
 * Created by zhxh on 2018/6/28
 */
public class ChartData implements IAxisValue {

    @SerializedName(value = "date", alternate = {"logDay"})
    private String date;
    @SerializedName(value = "value", alternate = {"yield"})
    private String value;

    @SerializedName(value = "name", alternate = {"UserName"})
    private String name;
    @SerializedName(value = "list", alternate = {"DayInitsAsstes"})
    private List<ChartData> list;


    public ChartData(String date, String value) {
        this.date = date;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChartData> getList() {
        return list;
    }

    public void setList(List<ChartData> list) {
        this.list = list;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String xValue() {
        return getDate();
    }

    @Override
    public float yValue() {
        return Float.parseFloat(getValue());
    }

    @Override
    public int flagValue() {
        return 0;
    }
}
