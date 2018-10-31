package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.base.BaseResp;

import java.util.List;

/**
 * Created by Resmic on 2017/5/3.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class HomeTypeModel extends BaseResp {
    public List<HomeType> data;

    public static class HomeType {
        public int id;
        public String name;
    }
}
