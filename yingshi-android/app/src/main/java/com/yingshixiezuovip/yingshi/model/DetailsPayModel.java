package com.yingshixiezuovip.yingshi.model;

/**
 * Created by Resmic on 2017/8/10.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class DetailsPayModel extends PayModel {
    public int userID;

    public DetailsPayModel(String payPrice, int userID) {
        super(payPrice, 2);
        this.userID = userID;
    }
}
