package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Resmic on 2017/5/3.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class UserInfo extends BaseResp.RespData {
    public int id;
    public String token;//用户唯一标示token，客户端自行保存
    public int userid;//用户ID
    public int type;//用户类型1-普通用户2-会员
    public String invite;//邀请人
    public int inviteUserid;//邀请人ID
    public int iswanshan;//0-未完善1-已完善
    public int isrenzhen;//0-未完善认证资料1-已完善认证资料
    public int isbzj;//1-未缴纳保证金2-缴纳保证金
    public int registerstatus;//注册状态 1-手机号码注册2-微信注册3-新浪注册
    public String nickname;//用户昵称
    public String head;//用户头像
    public int usertype;//1-普通用户2-认证会员
    public String sex;//性别
    public String position;//职位
    public String school;//毕业学院
    public String city;//当前城市
    public String birth;//出生日
    public String price;//价格
    public String unit;//单位
    public String telphone;//联系电话
    public String background;//个人空间背景图片
    public String phone;
    public int iswritephone;
    public int logintype;
    public int zuopincount;//作品个数
    public int forfollowcount;//被关注个数
    public int followcount;//关注个数
    public int zancount;//背书个数
    public int otherzancount;
    public List<UserPicItem> photoArr;//图片数组
    public int pid;//图片ID
    public String photo;//图片链接
    public int width;//图片宽度
    public int height;//图片高度
    public int guanzhu;//关注总数
    public int isguanzhu;//是否关注
    public int isbangpwd;//是否绑定手机号
    public String share_photo;
    public String share_title;
    public String share_url;

    public String share_samplereels_title;
    public String share_samplereels_url;
    public String share_samplereels_content;
    public String share_samplereels_photo;

    public static class UserPicItem implements Serializable {
        public int pid;
        public String photo;
        public int width;
        public int height;
    }
}
