package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * 类名称:BuyerOrderModel
 * 类描述:
 * 创建时间: 2018-11-12-00:25
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class BuyerOrderModel extends BaseResp {
    public List<BuyerOderDetailModel> data;
    public static class BuyerOderDetailModel extends ListDetailsModel.DetailsBaseItem {
        public String id;
        public String title;
        public String photo;
        public String uid;
        public String nickname;
        public String head;
        public String proid;
        public String total;
        public String state;
        public String phone;

    }
}
