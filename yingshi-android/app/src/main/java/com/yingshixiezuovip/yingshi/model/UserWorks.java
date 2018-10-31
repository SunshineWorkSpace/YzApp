package com.yingshixiezuovip.yingshi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Resmic on 2017/9/14.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class UserWorks implements Serializable {
    public int id;
    public String nickname;
    public String position;
    public String city;
    public String samplereels_fm;
    public int productionType;
    public int bycertificate_is;
    public int usertype;
    public String head;
    public int samplereels_fm_isSys;
    public String school;
    public String invite;
    public int inviteUserid;
    public int isfollow;
    public String phone;
    public int ispay;
    public int userid;
    public List<ResumeModel> resumeList;
    public String lookphonemoney;
    public String resume_content;
    public String share_samplereels_url;
    public String share_samplereels_title;
    public String share_samplereels_content;
    public String share_samplereels_photo;
    public String resume_url;
}
