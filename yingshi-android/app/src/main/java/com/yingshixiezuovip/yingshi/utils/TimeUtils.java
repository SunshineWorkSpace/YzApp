package com.yingshixiezuovip.yingshi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/6/14.
 */
public class TimeUtils {

    private SimpleDateFormat simpleDateFormat;

    public TimeUtils() {
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public TimeUtils(String formatStr) {
        simpleDateFormat = new SimpleDateFormat(formatStr);
    }

    public String get_time() {
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    public String get_time(long time) {
        return simpleDateFormat.format(new Date(time));
    }

    public static String foramtDate(long time, String formatStr) {
        return new SimpleDateFormat(formatStr).format(new java.sql.Date(time));
    }

    public static String formatRefreshDate(long createTime) {
        try {
            String ret = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar now = Calendar.getInstance();
            long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();
            if (ms_now - createTime < ms) {
                ret = "今天" + " " + foramtDate(createTime, "HH:mm");
            } else if (ms_now - createTime < (ms + 24 * 3600 * 1000)) {
                ret = "昨天" + " " + foramtDate(createTime, "HH:mm");
            } else if (ms_now - createTime < (ms + 24 * 3600 * 1000 * 2)) {//yyyyMMddHHmmss
                ret = "前天" + " " + foramtDate(createTime, "HH:mm");
            } else {
                ret = foramtDate(createTime, "yyyy-MM-dd HH:mm");
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
