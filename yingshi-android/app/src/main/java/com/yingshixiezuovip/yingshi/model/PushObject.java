package com.yingshixiezuovip.yingshi.model;

import android.text.TextUtils;

import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.io.Serializable;

/**
 * Created by Resmic on 2017/8/15.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PushObject implements Serializable {
    public int type;
    public String message;
    public PubshObjectItem data;

    public PushObject(String message, String dataStr) {
        this.message = message;
        if (TextUtils.isEmpty(dataStr)) {
            this.data = new PubshObjectItem();
        } else {
            try {
                this.data = GsonUtil.fromJson(dataStr, PubshObjectItem.class);
            } catch (Exception e) {
                this.data = new PubshObjectItem();
            }
        }
    }

    public static class PubshObjectItem implements Serializable {
        public int type;
        public int id;
    }
}
