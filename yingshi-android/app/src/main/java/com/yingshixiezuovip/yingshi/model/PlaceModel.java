package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/5/17.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PlaceModel extends BaseResp {
    public List<ProviceItem> data;

    public static class ProviceItem {
        public int proviceId;
        public String provinceName;
        public List<CityItem> list;
    }

    public static class CityItem {
        public int cityId;
        public String cityName;
    }
}
