package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * 类名称:LogisticsDetailModel
 * 类描述:
 * 创建时间: 2018-11-12-17:48
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class LogisticsDetailModel {
    public List<LogisticsDetailBeanModel> data;

    public static class LogisticsDetailBeanModel {
        public String time;
        public String ftime;
        public String context;
    }
}
