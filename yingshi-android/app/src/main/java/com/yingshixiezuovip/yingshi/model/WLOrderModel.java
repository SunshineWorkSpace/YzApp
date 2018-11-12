package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * 类名称:WLOrderModel
 * 类描述:
 * 创建时间: 2018-11-13-00:48
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class WLOrderModel {
    public List<WLOrderBeanModel> data;
    public static class WLOrderBeanModel extends ListDetailsModel.DetailsBaseItem {
        public String invoice_id;
        public String invoice_name;

    }
}
