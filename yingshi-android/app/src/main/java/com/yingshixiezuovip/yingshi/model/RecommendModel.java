package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * Created by Resmic on 2017/5/16.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class RecommendModel {
    public List<RecommendItem> data;

    public static class RecommendItem {
        public int userid;
        public String name;
        public String head;
        public String position;
        public String invite;
        public int isfollow;
        public String city;
    }
}
