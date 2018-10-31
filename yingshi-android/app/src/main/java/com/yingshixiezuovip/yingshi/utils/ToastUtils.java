package com.yingshixiezuovip.yingshi.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Resmic on 2017/1/6.
 * Email:xiangyx@wenwen-tech.com
 */

public class ToastUtils {
    private static long mlastShowTime;

    public static void showMessage(final Activity activity, final String message) {
        if (activity == null || System.currentTimeMillis() - mlastShowTime < 1000) {
            return;
        }
        mlastShowTime = System.currentTimeMillis();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
