package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/9/13.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CoverModel extends BaseResp {
    public List<CoverItem> data;

    public static class CoverItem {
        public int id;
        public String photo;
        public String photononame;
        public String uploadPhoto;
        public String name;
    }
}
