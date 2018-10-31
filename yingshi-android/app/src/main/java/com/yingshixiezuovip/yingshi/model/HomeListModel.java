package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/5/3.
 */

public class HomeListModel extends BaseResp {
    public List<HomeListItem> data;

    public static class HomeListItem extends ListDetailsModel.DetailsBaseItem {
        public int recommend;  //0-非推荐1-推荐
        public int id;  //列表ID
        public int iszan;  //1-已点赞0-未点赞
        public String title;  //标题
        public String fmphoto;  //封面图片
        public int width;  //封面宽度
        public int height;  //封面高度
        public String invite;  //邀请人
        public int inviteUserid;  //邀请人用户ID
        public int zan;  //点赞次数
        public int userid;  //用户ID
        public String nickname;  //用户昵称
        public String head;  //用户头像
        public String price;  //价格
        public String unit;   //价格单位
        public int usertype;  //当前登陆用户类型1-普通用户2-会员非登陆状态获取列表数据默认返回1
        public int isfollow;  //是否关注0-未关注1-关注
        public String typename;//type名称
        public String photo;
        public String crtime;
        public String city;
        public String position;
        public int productionType;

    }
}
