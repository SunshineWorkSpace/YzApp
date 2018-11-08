package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by yuhua.gou on 2018/11/8.
 */

public class ShopTypeModel extends BaseResp {
    public List<ShopType> data;
    public static class ShopType extends ListDetailsModel.DetailsBaseItem {
        public String id;
        public String title;
        public String photo;
        public String price;
        public String city;
        public String width;
        public String height;
        public String head;

    }
}
