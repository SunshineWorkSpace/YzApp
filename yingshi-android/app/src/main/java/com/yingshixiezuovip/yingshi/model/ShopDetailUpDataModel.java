package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.io.Serializable;
import java.util.List;

/**
 * 类名称:ShopDetailUpDataModel
 * 类描述:
 * 创建时间: 2019-01-21-14:49
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class ShopDetailUpDataModel  extends BaseResp implements Serializable {
    public ShopDetailUpDataBeanModel data;
    public static class ShopDetailUpDataBeanModel extends ListDetailsModel.DetailsBaseItem {
        public String id;
        public String num;
        public String title;
        public String price;
        public String isnew;
        public String typename;
        public String constans;
        public String city;
        public String address;
        public String content;
        public String video;
        public String videofm;
        public String videotimer;
        public String video_yuan;
        public String videofm_yuan;
        public List<PhotoImageItem> photoList;
    }
    public static class PhotoImageItem extends ListDetailsModel.DetailsBaseItem {
        public String photo;
        public String photo_yuan;
        public String width;
        public String height;
    }
}
