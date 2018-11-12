package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

/**
 * 商铺信息
 * Created by yuhua.gou on 2018/11/12.
 */

public class ShopUserModel extends BaseResp {

    public ShopUser data;
    public static class ShopUser extends ListDetailsModel.DetailsBaseItem {
        public String uid;
        public String nickname;
        public String shopName;
        public String head;
        public String city;
        public String type;
        public int isguanzhu;
        public String shoptype;
        public String vipMoney;
    }
}
