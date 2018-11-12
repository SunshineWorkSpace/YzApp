package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * 商品详情
 * Created by yuhua.gou on 2018/11/9.
 */

public class ShopDetailTypeModel extends BaseResp {
    public ShopDetailType data;
    public static class ShopDetailType extends ListDetailsModel.DetailsBaseItem {
    	public String id;
        public String uid;
        public String title;
        public String price;
        public String isnew;
        public String content;
        public String constans;
        public String city;
        public String nickname;
        public String head;
        public String video;
        public String videofm;
        public String num;
        public String ispay;
        public String phone;
        public String vipMoney;
        public String lookphonemoney;
        public String isbuy;
        public int isfollow;
        public List<PhotoImageItem> photoList;
        public String sharetitle;
        public String sharecontent;
        public String sharephoto;
        public String shareurl;

    }

    public static class PhotoImageItem extends ListDetailsModel.DetailsBaseItem {
        public String photo;
        public String width;
        public String height;
    }

}
