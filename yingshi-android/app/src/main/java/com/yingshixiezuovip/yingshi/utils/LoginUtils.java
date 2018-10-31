package com.yingshixiezuovip.yingshi.utils;

import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.SimpleTaskListener;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BaseEaseUser;
import com.yingshixiezuovip.yingshi.model.UserInfo;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Resmic on 2017/5/20.
 */

public class LoginUtils {
    private static UserInfo mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());

    public static void doLogin(String token) {
        L.d("EMClient::doLogin::onStart ==> { userName:" + token + ",password:" + Md5Utils.md5Encode(token));
        EMClient.getInstance().login(token, Md5Utils.md5Encode(token), new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                initEaseUI();
                L.d("EMClient::login::onSuccess");
            }

            @Override
            public void onProgress(int progress, String status) {
                L.d("EMClient::login::onProgress");
            }

            @Override
            public void onError(int code, String message) {
                L.d("EMClient::login::onError::" + message);
            }
        });
    }

    public static void doLogout() {
        JPushInterface.cleanTags(YingApplication.getInstance(), 0);
        EMClient.getInstance().logout(true);
    }

    public static void getUserInfo(String token) {
        HashMap<String, Object> msgParams = new HashMap<>();
        msgParams.put("token", token);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_USERINFO_BY_TOKEN, msgParams, taskListener);
    }

    private static SimpleTaskListener taskListener = new SimpleTaskListener() {
        @Override
        public void taskFinished(TaskType type, Object result, boolean isHistory) {
        }
    };

    public static void initEaseUI() {
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                L.d("setUserProfileProvider::" + username);
                EaseUser easeUser = null;
                if (username.equalsIgnoreCase(mUserInfo.token)) {
                    easeUser = new EaseUser(mUserInfo.nickname);
                    easeUser.setAvatar(mUserInfo.head);
                } else {
                    BaseEaseUser tempuser = SPUtils.getBaseEaseUser(YingApplication.getInstance(), username);
                    if (tempuser != null) {
                        easeUser = new EaseUser(TextUtils.isEmpty(tempuser.nickname) ? tempuser.token : tempuser.nickname);
                        easeUser.setAvatar(tempuser.head);
                    } else {
                        getUserInfo(username);
                    }
                }
                return easeUser;
            }
        });
    }
}
