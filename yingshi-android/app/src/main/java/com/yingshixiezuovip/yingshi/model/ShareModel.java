package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

/**
 * Created by Resmic on 2017/5/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class ShareModel extends BaseResp {
    public ShareItem data;

    public static class ShareItem extends BaseResp.RespData {
        public String photo;
        public String title;
        public String url;
        public String content;
    }
}
