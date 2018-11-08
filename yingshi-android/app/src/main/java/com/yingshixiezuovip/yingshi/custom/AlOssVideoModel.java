package com.yingshixiezuovip.yingshi.custom;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * 类名称:AlOssVideoModel
 * 类描述:
 * 创建时间: 2018-11-07-15:54
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class AlOssVideoModel extends BaseResp {
    public List<AlOssVideoDetailModel> data;

    public static class AlOssVideoDetailModel {
        public String createDir;
    }
}
