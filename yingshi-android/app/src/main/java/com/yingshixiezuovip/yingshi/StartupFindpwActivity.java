package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskInfo;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.Md5Utils;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/5/3.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class StartupFindpwActivity extends BaseActivity implements Runnable {
    private Handler mHanler;
    private int mWaitTime = 0;
    private boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_findpw, -1, false);
        initView();
    }

    private void initView() {
        findViewById(R.id.findpw_btn_back).setOnClickListener(this);
        findViewById(R.id.findpw_btn_getcode).setOnClickListener(this);
        findViewById(R.id.findpw_btn_visible).setOnClickListener(this);
        findViewById(R.id.findpw_btn_submit).setOnClickListener(this);
        mHanler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mWaitTime > 0) {
                    mWaitTime--;
                    setButtonStatus(false, mWaitTime + "s");
                } else {
                    setButtonStatus(true, "获取验证码");
                }
            }
        };
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.findpw_btn_back:
                onBackPressed();
                break;
            case R.id.findpw_btn_getcode:
                doSendVerificationCode();
                break;
            case R.id.findpw_btn_submit:
                if (!checkRegistParams()) {
                    return;
                }
                doRequest();
                break;
        }
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.findpw_btn_visible:
                isVisible = !isVisible;
                if (isVisible) {
                    ((EditText) findViewById(R.id.findpw_et_password)).setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    ((EditText) findViewById(R.id.findpw_et_password)).setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                findViewById(R.id.findpw_et_password).postInvalidate();
                CharSequence charSequence = ((EditText) findViewById(R.id.findpw_et_password)).getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
        }
    }
    private void doSendVerificationCode() {
        String phone = ((EditText) findViewById(R.id.findpw_et_phone)).getText().toString();
        if (!CommUtils.isMobileNo(phone)) {
            showMessage(getString(R.string.phone_format_error));
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(new TaskInfo(TaskType.TASK_TYPE_RESET_SendSMS, params, this));
    }

    private void doRequest() {
        String phone = ((EditText) findViewById(R.id.findpw_et_phone)).getText().toString();
        String verifiCode = ((EditText) findViewById(R.id.findpw_et_verifcode)).getText().toString();
        String password = ((EditText) findViewById(R.id.findpw_et_password)).getText().toString();
        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("pwd", Md5Utils.md5Encode(password));
        params.put("code", verifiCode);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_RESET_PASSWORD, params, this);
    }

    private void setButtonStatus(boolean enable, String info) {
        findViewById(R.id.findpw_btn_getcode).setEnabled(enable);
        ((TextView) findViewById(R.id.findpw_btn_getcode)).setText(info);
    }

    private boolean checkRegistParams() {
        String phone = ((EditText) findViewById(R.id.findpw_et_phone)).getText().toString();
        String vitifCode = ((EditText) findViewById(R.id.findpw_et_verifcode)).getText().toString();
        String password = ((EditText) findViewById(R.id.findpw_et_password)).getText().toString();
        if (!CommUtils.isMobileNo(phone)) {
            showMessage(getString(R.string.phone_format_error));
            return false;
        } else if (TextUtils.isEmpty(vitifCode)) {
            showMessage(getString(R.string.please_get_verification_code));
            return false;
        } else if (!CommUtils.validatePassword(password)) {
            showMessage(getString(R.string.password_format_error));
            return false;
        }
        return true;
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        mHanler.removeCallbacks(this);
        if (result instanceof Throwable) {
            if (result instanceof BaseThrowable) {
                switch (((BaseThrowable) result).getErrorCode()) {
                    case 204:
                        showMessage("该手机未注册");
                        break;
                    case 406:
                        showMessage("验证码错误，请重新输入");
                        break;
                    default:
                        showMessage("服务器异常，访问失败");
                        break;
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_RESET_SendSMS:
                showMessage("验证码发送成功，请注意查收");
                mWaitTime = Configs.SMS_INTERFAL_TIME;
                mHanler.post(this);
                break;
            case TASK_TYPE_RESET_PASSWORD:
                showMessage("找回成功，请重新登录");
                onBackPressed();
                break;
        }
    }

    @Override
    public void run() {
        mHanler.sendEmptyMessage(0);
        mHanler.postDelayed(this, 1000);
    }
}
