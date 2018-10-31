package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/5/25.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class ReviewModel extends BaseResp {
    public List<ReviewItem> data;

    public static class ReviewItem extends ListDetailsModel.DetailsBaseItem {
        public int rid;
        public String content;
        public String crtime;
        public int zan;
        public int userid;
        public String head;
        public String nickname;
        public String city;
        public String position;
        public int usertype;
        public int iszan;
        public String share_photo;
        public int count;
        public String share_title;
        public String share_content;
        public String share_url;
        public List<ReviewChildItem> list;
    }

    public static class ReviewChildItem {
        public int reply_id;//	:	16
        public String reply_content;//	:	33333
        public String reply_crtime;//	:	2017-05-18 02:43:04
        public int reply_userid;//	:	5
        public String reply_nickname;//	:	c3
        public String reply_head;//	:	http://w
    }

}
