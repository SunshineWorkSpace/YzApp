package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.WebViewActivity;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskInfo;
import com.yingshixiezuovip.yingshi.datautils.TaskListener;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.Md5Utils;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class RegistWindow extends BasePopupWindow implements TaskListener, Runnable {
    private boolean isAgree = true;
    private LoadWindow mLoadWindow;
    private Handler mHanler;
    private int mWaitTime = 0;

    public RegistWindow(Context mContext) {
        super(mContext, false, false);
        setWidthHeight(300, 300);
        findViewById(R.id.regist_btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAgree = !isAgree;
                ((ImageView) findViewById(R.id.regist_iv_check)).setImageResource(isAgree ? R.mipmap.icon_check : R.mipmap.icon_uncheck);
            }
        });
        findViewById(R.id.regist_btn_close).setOnClickListener(this);
        findViewById(R.id.regist_btn_submit).setOnClickListener(this);
        findViewById(R.id.regist_btn_getcode).setOnClickListener(this);
        findViewById(R.id.regist_btn_agreement).setOnClickListener(this);

        mLoadWindow = new LoadWindow(mContext);
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

    private void setButtonStatus(boolean enable, String info) {
        findViewById(R.id.regist_btn_getcode).setEnabled(enable);
        ((TextView) findViewById(R.id.regist_btn_getcode)).setText(info);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.regist_btn_close:
                cancel();
                break;
            case R.id.regist_btn_agreement:
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.CURL, Configs.AgreementURL);
                mContext.startActivity(intent);
                break;
            case R.id.regist_btn_submit:
                if (!checkRegistParams()) {
                    return;
                }
                if (!isAgree) {
                    showMessage(mContext.getString(R.string.please_read_agreement_before));
                    return;
                }
                doRegist();
                break;
            case R.id.regist_btn_getcode:
                doSendVerificationCode();
                break;
        }
    }

    private void doRegist() {
        if (onRegistClickListener != null) {
            String phone = ((EditText) findViewById(R.id.regist_et_phone)).getText().toString();
            String vitifCode = ((EditText) findViewById(R.id.regist_et_vitifcode)).getText().toString();
            String password = ((EditText) findViewById(R.id.regist_et_password)).getText().toString();
            onRegistClickListener.onRegistClick(phone, Md5Utils.md5Encode(password), vitifCode);
        }
    }

    private void doSendVerificationCode() {
        String phone = ((EditText) findViewById(R.id.regist_et_phone)).getText().toString();
        if (!CommUtils.isMobileNo(phone)) {
            showMessage(mContext.getString(R.string.phone_format_error));
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(new TaskInfo(TaskType.TASK_TYPE_SendSMS, params, this));
    }

    private boolean checkRegistParams() {
        String phone = ((EditText) findViewById(R.id.regist_et_phone)).getText().toString();
        String vitifCode = ((EditText) findViewById(R.id.regist_et_vitifcode)).getText().toString();
        String password = ((EditText) findViewById(R.id.regist_et_password)).getText().toString();
        if (!CommUtils.isMobileNo(phone)) {
            showMessage(mContext.getString(R.string.phone_format_error));
            return false;
        } else if (TextUtils.isEmpty(vitifCode)) {
            showMessage(mContext.getString(R.string.please_get_verification_code));
            return false;
        } else if (!CommUtils.validatePassword(password)) {
            showMessage(mContext.getString(R.string.password_format_error));
            return false;
        }
        return true;
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_regist_layout, null);
    }

    @Override
    public void taskStarted(TaskType type) {
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_SendSMS:
                showMessage("验证码发送成功，请注意查收");
                mWaitTime = Configs.SMS_INTERFAL_TIME;
                mHanler.removeCallbacks(this);
                mHanler.post(this);
                break;
        }
    }

    @Override
    public void taskIsCanceled(TaskType type) {

    }

    @Override
    public void run() {
        mHanler.sendEmptyMessage(0);
        mHanler.postDelayed(RegistWindow.this, 1000);
    }

    public interface OnRegistClickListener {
        void onRegistClick(String phone, String password, String verifiCode);
    }

    public OnRegistClickListener onRegistClickListener;

    public void setOnRegistClickListener(OnRegistClickListener onRegistClickListener) {
        this.onRegistClickListener = onRegistClickListener;
    }
}
