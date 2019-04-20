package com.zhxh.xcomponent.dummy;

public class SelectItem {
    private int index;
    private String value;

    public SelectItem(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
