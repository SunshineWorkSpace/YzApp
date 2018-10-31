package com.yingshixiezuovip.yingshi.utils;

import android.content.Context;
import android.os.PowerManager;

import com.yingshixiezuovip.yingshi.base.YingApplication;

/**
 * Created by Resmic on 2016/12/13.
 */

public class WakelockUtils {
    private static PowerManager.WakeLock mWakeLock;

    public static void acquireWakeLock() {
        if (mWakeLock == null) {
            mWakeLock = ((PowerManager) YingApplication.getInstance().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "bright");
        }
        try {
            mWakeLock.acquire();
        } catch (Exception e) {
        }
    }

    public static void releaseWakeLock() {
        if (null != mWakeLock) {
            try {
                mWakeLock.release();
            } catch (Exception e) {
            }
        }
    }

    public static void acquireWakeLock(long timeout) {
        if (mWakeLock == null) {
            mWakeLock = ((PowerManager) YingApplication.getInstance().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "bright");
        }

        if (mWakeLock != null) {
            mWakeLock.acquire(timeout);
        }
    }
}
