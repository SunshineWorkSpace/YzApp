package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * 类名称:VipChoosePayModel
 * 类描述:
 * 创建时间: 2018-11-13-14:01
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class VipChoosePayModel {
    public List<VipChoosePayBeanModel> data;

    public static class VipChoosePayBeanModel {
        public String id;
        public String year;
        public String rate;
        public String oprice;
        public String price;
        public boolean isClick=false;
    }
}
