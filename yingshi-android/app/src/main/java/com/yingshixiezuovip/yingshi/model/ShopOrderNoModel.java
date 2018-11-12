package com.yingshixiezuovip.yingshi.model;

/**
 * 订单信息
 * Created by yuhua.gou on 2018/11/12.
 */

public class ShopOrderNoModel {
    public ShopOrderNo data;

    public final static class ShopOrderNo  extends ListDetailsModel.DetailsBaseItem {
        public String flow_trade_no;
    }
}
