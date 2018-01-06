package com.morening.hello.piechart;

import android.graphics.Color;

/**
 * Created by morening on 2018/1/1.
 */

public class DataBean implements Cloneable{

    private float data = 0f;
    private float sweep = 0f;
    private int color = Color.TRANSPARENT;
    private String tag = null;
    private Object object = null;

    public float getData() {
        return data;
    }

    public DataBean setData(float data) {
        this.data = data;
        return this;
    }

    public float getSweep() {
        return sweep;
    }

    public DataBean setSweep(float sweep) {
        this.sweep = sweep;
        return this;
    }

    public int getColor() {
        return color;
    }

    public DataBean setColor(int color) {
        this.color = color;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public DataBean setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public Object getObject() {
        return object;
    }

    public DataBean setObject(Object object) {
        this.object = object;
        return this;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DataBean data = new DataBean();
        data.setData(this.getData())
                .setTag(this.getTag())
                .setSweep(this.getSweep())
                .setObject(this.getObject());
        return data;
    }
}
