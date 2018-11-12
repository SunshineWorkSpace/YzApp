package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * 类名称:BuyerDetailModel
 * 类描述:
 * 创建时间: 2018-11-13-00:06
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class BuyerDetailModel {
    public BuyerDetailBeanModel data;
    public static class BuyerDetailBeanModel extends ListDetailsModel.DetailsBaseItem {
        public String uid;
        public String revcname;
        public String telphone;
        public String city;
        public String address;

    }
}
