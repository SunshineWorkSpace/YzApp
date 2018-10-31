package com.yingshixiezuovip.yingshi.model;

import java.io.Serializable;

/**
 * Created by Resmic on 2017/8/10.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PayModel implements Serializable {
    public String payPrice;
    private int type;

    public PayModel(String payPrice, int type) {
        this.payPrice = payPrice;
        this.type = type;
    }

    public PayModel(String payPrice) {
        this.payPrice = payPrice;
        this.type = 1;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
