package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * 类名称:MyBalanceModel
 * 类描述:
 * 创建时间: 2018-11-13-10:32
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class MyBalanceModel extends BaseResp {
    public MyBalanceBeanModel data;
    public static class MyBalanceBeanModel extends ListDetailsModel.DetailsBaseItem {
        public String balance;
        public String txz_balance;
        public String xbj_balance;

    }
}
