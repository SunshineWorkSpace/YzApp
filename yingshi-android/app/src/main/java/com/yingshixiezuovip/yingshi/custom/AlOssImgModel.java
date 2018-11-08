package com.yingshixiezuovip.yingshi.custom;

import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.model.HomeTypeModel;

import java.util.List;

/**
 * 类名称:AlOssImgModel
 * 类描述:
 * 创建时间: 2018-11-07-14:47
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class AlOssImgModel extends BaseResp {
    public List<AlOssImgDetailModel> data;

    public static class AlOssImgDetailModel {
        public String createDir;
    }
}
