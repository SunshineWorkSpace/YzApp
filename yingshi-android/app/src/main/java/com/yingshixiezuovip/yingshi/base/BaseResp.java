package com.yingshixiezuovip.yingshi.base;

import java.io.Serializable;

/**
 * Created by Resmic on 2017/5/3.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class BaseResp {
    public RespStatus result;


    public static class RespStatus {
        public int code;
        public String detail;
    }

    public static class RespData implements Serializable{
    }
}
