package com.yingshixiezuovip.yingshi.model;

import android.view.View;

import java.io.Serializable;

/**
 * 更多
 * Created by yuhua.gou on 2018/11/13.
 */

public class ShopMoreModel implements Serializable {
    public String title;
    public String content;
    public int showDetails= View.GONE;
    public boolean isCheck=false;

    public ShopMoreModel(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ShopMoreModel(String title, boolean isCheck) {
        this.title = title;
        this.isCheck = isCheck;
    }
}
