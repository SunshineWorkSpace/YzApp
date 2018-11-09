package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * 类名称:ConsumerPriceModel
 * 类描述:
 * 创建时间: 2018-11-10-01:28
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class ConsumerPriceModel {
    public List<ConsumerPriceDetailModel> data;

    public static class ConsumerPriceDetailModel {
        public String vip;
        public String price;
        public boolean isClick=false;
    }
}
