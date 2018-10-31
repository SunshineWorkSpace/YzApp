package com.yingshixiezuovip.yingshi.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.utils.CommUtils;

/**
 * Created by Resmic on 18/1/19.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class VersionModel {

    /**
     * version : 2.0.0
     * uptime : 2017-04-28 09:39:06
     * content : 33333
     * isclose : 1
     */

    private String version;
    private String uptime;
    private String content;
    private String isclose;
    private String downUrl = "http://www.baidu.com";

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsclose() {
        return isclose;
    }

    public void setIsclose(String isclose) {
        this.isclose = isclose;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public boolean needUpdate() {
        return !CommUtils.getVersionName(YingApplication.getInstance()).equalsIgnoreCase(getVersion());
    }

    public void toUpdate(Activity activity) {
        if (!TextUtils.isEmpty(getDownUrl())) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(getDownUrl());
            intent.setData(content_url);
            activity.startActivity(intent);
        }
    }
}
