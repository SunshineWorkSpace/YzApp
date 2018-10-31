package com.yingshixiezuovip.yingshi.utils;

import android.util.Log;

import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;

/**
 * Created by Resmic on 2016/8/5.
 */

public class L {
    public static void d(Object messsage) {
        if (messsage != null) {
            Log.d("YingZheTAG", messsage.toString());
        }
    }

    public static void d(String TAG, Object messsage) {
        if (Configs.isDebug && messsage != null) {
            Log.d(TAG, messsage.toString());
        }
    }

    public static void d(String TAG, Object messsage, Throwable throwable) {
        if (Configs.isDebug && messsage != null) {
            Log.d(TAG, messsage.toString() + "\n" + ThrowableUtils.getThrowableDetailsMessage(throwable));
        }
    }

    public static void e(Object messsage) {
        if (Configs.isDebug && messsage != null) {
            Log.e("WEN_TAG", messsage.toString());
        }
    }

    public static void w(Object messsage) {
        if (Configs.isDebug && messsage != null) {
            Log.w("WEN_TAG", messsage.toString());
        }
    }

    public static void i(String tag, String messsage) {
        if (Configs.isDebug && messsage != null) {
            Log.i(tag, messsage.toString());
        }
    }
}
