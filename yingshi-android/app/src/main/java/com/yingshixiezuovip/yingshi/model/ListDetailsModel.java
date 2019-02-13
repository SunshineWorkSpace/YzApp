package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Resmic on 2017/5/6.
 */

public class ListDetailsModel extends BaseResp {
    public DetailsModel data;

    public static class DetailsModel {
        public String detailsUrl;
        public int id;
        public int usertype;
        public String easemob_token;
        public String nickname;
        public String head;
        public int productionType;
        public String typename;
        public String title;
        public String phone;
        public String lookphonemoney;
        public String userposition;
        public String price;
        public String content;
        public String fmphoto;
        public int width;
        public int height;
        public int ispay;
        public int readnum;
        public String crtime;
        public int iszan;
        public String position;
        public String unit;
        public String city;
        public int isfollow;
        public int inviteUserid;
        public String invite;
        public int userid;
        public List<DetailsImageItem> bodyList;
        public List<DetailsVideoItem> videoList;
        public List<HomeListModel.HomeListItem> otherList;
        public List<ReviewModel.ReviewItem> reviewList;
        public String share_photo;
        public String share_title;
        public String share_url;
        public String share_content;
        public String iscalim;
        public List<Calims> calims;
    }

    public static class Calims extends DetailsBaseItem {
        public String userid;
        public String position;
        public String head;
        public String nickname;
    }

    public static class DetailsImageItem extends DetailsBaseItem {
        public int bodyId;
        public String photo;
        public int width;
        public int height;
        public String content;
    }

    public static class DetailsVideoItem extends DetailsBaseItem {
        public int videId;
        public String url;
        public String content;
        public String photo;
    }

    public static class DetailsBaseItem implements Serializable {

    }
}
