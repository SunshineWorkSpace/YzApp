package com.yingshixiezuovip.yingshi.custom;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/5/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class VideoModel extends BaseResp {
    public List<VideoItem> data;

    public static class VideoItem {
        public int id;//	:	104
        public String url;//	:	http://www.cftvc.com:8888/yz_dir/star/video/20170509/21880235de20de9e9c072cb3f0428237.mp4
        public String content;//	:
        public String time;//	:	00:01:43
        public int playCount;//	:	0
        public String nickname;//	:	宝宝
        public String head;//	:	http://www.cftvc.com:8888/yz_dir/user/head/20170228/thumb/cenaczh.jpg
        public String invite;//	:
        public String photo;//	:	http://www.cftvc.com:8888/yz_dir/star/video/fm/20170509/cc13929a5f9e1d4d0b10cafba9647d80.jpg
        public int userid;//	:	2
        public String title;//	:	我

    }
}
