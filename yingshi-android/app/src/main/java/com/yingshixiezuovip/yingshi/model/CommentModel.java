package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/5/5.
 */

public class CommentModel extends BaseResp {
    public List<CommentItem> data;

    public static class CommentItem {
        public int id;
        public String crtime;
        public String photo;
        public String title;
        public String nickname;
        public String city;
        public int starID;
        public String typename;
        public String born;
        public int startID;
        public String content;
        public String head;
        public int userid;
        public String position;
    }
}

