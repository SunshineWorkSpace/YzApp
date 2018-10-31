package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.LoginUtils;
import com.yingshixiezuovip.yingshi.utils.Md5Utils;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/8/6.
 */

public class UserBindPhoneActivity extends BaseActivity {
    private AlertWindow mExitBindWindow;
    private int mDefaultSMSInterval = 60;
    private int mCurrentSMSInterval = mDefaultSMSInterval;
    private UserInfo mTempUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bind_phone, R.string.activity_user_bind_phone_title);

        mTempUserInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
        initView();
        initWindow();
    }

    private void initView() {
        findViewById(R.id.userbind_btn_sendcode).setOnClickListener(this);
        findViewById(R.id.userbind_btn_bind).setOnClickListener(this);
        findViewById(R.id.userbind_btn_call).setOnClickListener(this);
        findViewById(R.id.userbind_btn_call).setOnClickListener(this);
    }

    private void initWindow() {
        mExitBindWindow = new AlertWindow(this, true);
        mExitBindWindow.setOnClickListener(this);
    }


    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.userbind_btn_sendcode:
                doSendCode();
                break;
            case R.id.userbind_btn_bind:
                doBindPhone();
                break;
            case R.id.alert_btn_submit:
                SPUtils.saveUserInfo(null);
                LoginUtils.doLogout();
                finish();
                break;
            case R.id.userbind_btn_call:
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Configs.SERVICE_PHONE)));
                break;
            default:
                break;

        }
    }

    private void doSendCode() {
        String phoneStr = getPhone();
        if (TextUtils.isEmpty(phoneStr)) {
            showMessage("请先输入需要绑定的手机号码");
            return;
        }
        mTempUserInfo.phone = phoneStr;
        HashMap<String, Object> sendCodeParams = new HashMap<>();
        sendCodeParams.put("token", mTempUserInfo.token);
        sendCodeParams.put("phone", phoneStr);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(TaskType.TASK_TYPE_SendSMS, sendCodeParams, this);
    }

    private void doBindPhone() {
        String phoneStr = getPhone();
        if (TextUtils.isEmpty(phoneStr)) {
            showMessage("请输入需要绑定的手机号码");
            return;
        }
        String verifiCode = (((EditText) findViewById(R.id.userbind_et_verificode)).getText().toString() + "").trim();
        if (TextUtils.isEmpty(verifiCode)) {
            showMessage("请获取验证码");
            return;
        }
        String password = (((EditText) findViewById(R.id.userbind_et_password)).getText().toString() + "").trim();
        if (TextUtils.isEmpty(password)) {
            showMessage("请输入密码");
            return;
        }

        if (password.length() < 6) {
            showMessage("登录密码不能少于6位");
            return;
        }

        String repassword = (((EditText) findViewById(R.id.userbind_et_repassword)).getText().toString() + "").trim();
        if (TextUtils.isEmpty(repassword)) {
            showMessage("请确认密码");
            return;
        }

        if (!password.equals(repassword)) {
            showMessage("两次密码不一致，请重新输入");
            return;
        }

        mLoadWindow.show(R.string.waiting);

        HashMap<String, Object> bindParams = new HashMap<>();
        bindParams.put("token", mTempUserInfo.token);
        bindParams.put("phone", phoneStr);
        bindParams.put("code", verifiCode);
        bindParams.put("pwd", Md5Utils.md5Encode(password));
        HttpUtils.doPost(TaskType.TASK_TYPE_BIND_PHONE, bindParams, this);
    }

    private Handler mCountHandler = new Handler();
    private Runnable mCountRunnable = new Runnable() {
        @Override
        public void run() {
            mCurrentSMSInterval--;
            if (mCurrentSMSInterval > 0) {
                ((TextView) findViewById(R.id.userbind_btn_sendcode)).setText(mCurrentSMSInterval + "s倒计时");
                mCountHandler.postDelayed(this, 1000);
            } else {
                ((TextView) findViewById(R.id.userbind_btn_sendcode)).setText("获取验证码");
                findViewById(R.id.userbind_btn_sendcode).setEnabled(true);
            }
        }
    };

    private void enterCountTime() {
        mCurrentSMSInterval = mDefaultSMSInterval;
        mCountHandler.postDelayed(mCountRunnable, 1000);
        findViewById(R.id.userbind_btn_sendcode).setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        mExitBindWindow.show("账号绑定手机号？", "放弃并回到登录页？", "取消", "回到登录页");
    }

    private String getPhone() {
        return ((EditText) findViewById(R.id.userbind_et_phone)).getText().toString();
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            if (result instanceof BaseThrowable) {
                if (((BaseThrowable) result).getErrorCode() == 406) {
                    showMessage("验证码不正确，请重新输入");
                } else if (((BaseThrowable) result).getErrorCode() == 403) {
                    showMessage("该手机已被绑定，请重新输入");
                } else {
                    showMessage(((BaseThrowable) result).getMessage());
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_SendSMS:
                enterCountTime();
                break;
            case TASK_TYPE_BIND_PHONE:
                SPUtils.saveUserInfo(mTempUserInfo);
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                YingApplication.getInstance().startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountHandler.removeCallbacks(mCountRunnable);
    }
}
