package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Resmic on 2017/5/10.
 */

public class BuyerModel extends BaseResp {
    public List<BuyerItem> data;

    public static class BuyerItem implements Serializable{
        public int id;//	:29
        public String fmphoto;//	:http://www.cftvc.com:8888/yz_dir/star/photo/20170414/thumb/6c0dee386a95ad5304e2ebedaf115fec.jpg
        public String position;//	:
        public String nickname;//	:蔬菜
        public String crtime;//	:2017 年05月09日
        public int status;//	:6
        public String statusName;//	:卖家未接单
        public int starID;//	:240
        public String title;//	:kend
        public int userid;//	:63
        public String orderno;//	:201705090958313447
        public String total;//	:0.5
        public String telphone;//	:18729909780
        public List<String> list;
    }
}
