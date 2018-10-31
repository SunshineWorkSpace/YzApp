package com.yingshixiezuovip.yingshi.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.yingshixiezuovip.yingshi.NoticeMangerActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.model.PushObject;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.ActivityManager;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * Created by Resmic on 2017/8/14.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe* 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReciver extends BroadcastReceiver {
    private static final String TAG = "YingZhePush";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                L.d(TAG, "JPushInterface.ACTION_MESSAGE_RECEIVED");
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                String params = bundle.getString(JPushInterface.EXTRA_EXTRA);
                String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
                L.d(TAG, "收到推送消息：message = " + alert + " , params = " + params);
                PushObject pushObject = new PushObject(alert, params);
                dealParams(context, pushObject);
                if (!ActivityManager.getInstance().isBackground()) {
                    JPushInterface.clearAllNotifications(context);
                    EventBus.getDefault().post(new BaseEvent(EventType.EVENT_TYPE_RECIVER_NOTICE, pushObject));
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                String params = bundle.getString(JPushInterface.EXTRA_EXTRA);
                String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
                PushObject object = new PushObject(alert, params);
                intent = new Intent(context, NoticeMangerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bundle = new Bundle();
                bundle.putSerializable("push_object", object);
                intent.putExtras(bundle);
                context.startActivity(intent);
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            } else {
            }
        } catch (Exception e) {

        }
    }

    private void dealParams(Context context, PushObject object) {
        UserInfo mUserInfo = SPUtils.getUserInfo(context);
        if (object.data.type == 2) {
            mUserInfo.isrenzhen = 1;
            mUserInfo.isbzj = 1;
            mUserInfo.iswanshan = 1;
            if (mUserInfo.type == 3) {
                mUserInfo.type = 4;
            } else if (mUserInfo.type == 1) {
                mUserInfo.type = 2;
            }
            SPUtils.saveUserInfo(mUserInfo);
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    L.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    L.d(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (!ActivityManager.getInstance().isBackground()) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        }
    }
}
