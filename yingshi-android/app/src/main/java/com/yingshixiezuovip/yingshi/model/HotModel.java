package com.yingshixiezuovip.yingshi.model;

import java.io.Serializable;

/**
 * Created by Resmic on 18/1/19.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：热门搜索model
 */

public class HotModel implements Serializable{
    private String keywords;
    private int type;

    public HotModel() {
    }

    public HotModel(int type, String keywords) {
        this.type = type;
        this.keywords = keywords;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
