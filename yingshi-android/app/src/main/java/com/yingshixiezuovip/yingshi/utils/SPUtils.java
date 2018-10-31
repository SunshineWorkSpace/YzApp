package com.yingshixiezuovip.yingshi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.model.AuthModel;
import com.yingshixiezuovip.yingshi.model.BaseEaseUser;
import com.yingshixiezuovip.yingshi.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 2017/5/3.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class SPUtils {
    private static final String TAG = "SPUtilsTAG";
    private static final String SPName = "YingCache";

    public static String getString(Context context, String key, String defaultStr) {
        return getString(true, context, key, defaultStr);
    }

    public static String getString(boolean isUser, Context context, String key, String defaultStr) {
        String localKey = "yingcache_";
        if (isUser) {
            UserInfo userInfo = getUserInfo(context);
            if (userInfo != null) {
                localKey += userInfo.userid + "_";
            }
        }
        localKey += key;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPName, 0);
        String result = sharedPreferences.getString(localKey, defaultStr);
        L.d("SPUtilsTAG", "Get->(" + localKey + ":" + result + ")");
        return result;
    }


    public static void putString(Context context, String key, String value) {
        if (context == null) {
            return;
        }
        putString(true, context, key, value);
    }

    public static void putString(boolean isUser, Context context, String key, String value) {
        L.d(TAG, "Save->(" + key + ":" + value + ")");
        if (context == null) {
            return;
        }
        String localKey = "yingcache_";
        if (isUser) {
            UserInfo userInfo = getUserInfo(context);
            if (userInfo != null) {
                localKey += userInfo.userid + "_";
            }
        }
        localKey += key;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPName, 0);
        sharedPreferences.edit().putString(localKey, value).commit();
    }

    public static UserInfo getUserInfo(Context context) {
        String userinfoStr = getString(false, context, "yingcache_userinfo", null);
        if (!TextUtils.isEmpty(userinfoStr)) {
            try {
                return GsonUtil.fromJson(userinfoStr, UserInfo.class);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void saveUserInfo(UserInfo userinfo) {
        putString(false, YingApplication.getInstance(), "yingcache_userinfo", GsonUtil.toJson(userinfo));
    }

    public static void saveAuthInfo(AuthModel authInfo) {
        putString(false, YingApplication.getInstance(), "yingcache_authinfo", GsonUtil.toJson(authInfo));
    }

    public static AuthModel getAuthInfo(Context context) {
        String userinfoStr = getString(false, context, "yingcache_authinfo", null);
        if (!TextUtils.isEmpty(userinfoStr)) {
            try {
                return GsonUtil.fromJson(userinfoStr, AuthModel.class);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void saveRefreshTime(Context context, String position, long time) {
        putString(true, context, position, String.valueOf(time));
    }

    public static long getRefreshTime(Context context, String position) {
        return Long.parseLong(getString(true, context, position, String.valueOf(System.currentTimeMillis())));
    }

    public static BaseEaseUser getBaseEaseUser(Context context, String token) {
        String easeuserStr = getString(false, context, "base_ease_userinfo", null);
        if (TextUtils.isEmpty(easeuserStr)) {
            return null;
        }
        List<BaseEaseUser> easeUsers;
        try {
            easeUsers = GsonUtil.fromJson(easeuserStr, new TypeToken<List<BaseEaseUser>>() {
            }.getType());
        } catch (Exception e) {
            easeUsers = new ArrayList<>();
        }
        for (BaseEaseUser tempUser : easeUsers) {
            if (tempUser.token.equalsIgnoreCase(token) || tempUser.nickname.equalsIgnoreCase(token)) {
                return tempUser;
            }
        }
        return null;
    }

    public static void saveBaseEaseUser(Context context, BaseEaseUser baseEaseUser) {
        String easeuserStr = getString(false, context, "base_ease_userinfo", null);
        List<BaseEaseUser> easeUsers;
        if (TextUtils.isEmpty(easeuserStr)) {
            easeUsers = new ArrayList<>();
        } else {
            try {
                easeUsers = GsonUtil.fromJson(easeuserStr, new TypeToken<List<BaseEaseUser>>() {
                }.getType());
            } catch (Exception e) {
                easeUsers = new ArrayList<>();
            }
        }
        boolean isExit = false;
        for (BaseEaseUser tempUser : easeUsers) {
            if (tempUser.token.equalsIgnoreCase(baseEaseUser.token)) {
                isExit = true;
                tempUser.head = baseEaseUser.head;
                tempUser.nickname = baseEaseUser.nickname;
                break;
            }
        }
        if (!isExit) {
            easeUsers.add(baseEaseUser);
        }
        putString(false, context, "base_ease_userinfo", GsonUtil.toJson(easeUsers));
    }

}
