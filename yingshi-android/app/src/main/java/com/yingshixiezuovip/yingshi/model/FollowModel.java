package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/5/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class FollowModel extends BaseResp {
    public List<FollowItem> data;

    public static class FollowItem extends BaseResp.RespData{
        public int id;//	:	150
        public int userid;//	:45
        public String head;//	:http://www.cftvc.com:8888/yz_dir/user/head/20170401/thumb/981c4bb494f1551e5f6f99834f599641.jpg
        public String nickname;//	:zhang法will
        public String position;//	:导演
        public String born;// 北京市 通州
        public String crtime;//	:2017-04-14 10:45:59
        public int isfollow;//	:0

    }
}
