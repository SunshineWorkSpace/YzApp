package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.HashMap;

public class MainAuthenticationInfoActivity extends BaseActivity implements PictureManager.OnPictureCallbackListener {
    private boolean isDay = true;
    private int mImageId;
    private PictureManager mPictureManager;
    private String mIDCardStr1, mIDCardStr2, mIDCardStr3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_authentication_info, R.string.title_activity_main_authentication_info);

        initView();
    }

    private void initView() {
        mPictureManager = new PictureManager(this, this);
        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.authinfo_btn_day).setOnClickListener(this);
        findViewById(R.id.authinfo_btn_project).setOnClickListener(this);
        findViewById(R.id.authinfo_iv_idcard1).setOnClickListener(this);
        findViewById(R.id.authinfo_iv_idcard2).setOnClickListener(this);
        findViewById(R.id.authinfo_iv_idcard3).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.authinfo_btn_day:
                onClickCheckBox(1);
                break;
            case R.id.authinfo_btn_project:
                onClickCheckBox(2);
                break;
            case R.id.authinfo_iv_idcard1:
                mImageId = 1;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
            case R.id.authinfo_iv_idcard2:
                mImageId = 2;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
            case R.id.authinfo_iv_idcard3:
                mImageId = 3;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
            case R.id.right_btn_submit:
                doSubmit();
                break;
        }
    }

    private void doSubmit() {
        final String email = ((EditText) findViewById(R.id.authinfo_et_email)).getText().toString();
        if (TextUtils.isEmpty(email)) {
            showMessage("请填写你的邮箱");
            return;
        }
        final String priceStr = ((EditText) findViewById(R.id.authinfo_et_price)).getText().toString();
        if (TextUtils.isEmpty(priceStr)) {
            showMessage("请填写你的薪酬");
            return;
        }
        if (priceStr.startsWith(".")) {
            showMessage("请输入正确的薪酬");
            return;
        }
        double xinzi;
        try {
            xinzi = Double.parseDouble(priceStr);
        } catch (Exception e) {
            xinzi = 0;
        }
        if (xinzi == 0) {
            showMessage("薪资必须大于0，请重新输入");
            return;
        }
        if (TextUtils.isEmpty(mIDCardStr1) || TextUtils.isEmpty(mIDCardStr2)) {
            showMessage("请点击上传身份证正反面");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.show(R.string.text_request);
                        }
                    });
                    HashMap<String, Object> infoParams = new HashMap<>();
                    infoParams.put("token", mUserInfo.token);
                    infoParams.put("email", email);
                    infoParams.put("price", priceStr);
                    infoParams.put("unit", isDay ? "天" : "项目");
                    infoParams.put("identity_card", PictureManager.getBase64(mIDCardStr1));
                    infoParams.put("side_card", PictureManager.getBase64(mIDCardStr2));
                    if (!TextUtils.isEmpty(mIDCardStr3)) {
                        infoParams.put("diploma", PictureManager.getBase64(mIDCardStr1));
                    } else {
                        infoParams.put("diploma", "");
                    }
                    HttpUtils.doPost(TaskType.TASK_TYPE_SUBMIT_AUTHENTICATION, infoParams, MainAuthenticationInfoActivity.this);
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.cancel();
                        }
                    });

                    L.d("Exception::" + ThrowableUtils.getThrowableDetailsMessage(e));
                    showMessage("头像解析失败，请稍后重试");
                }
            }
        }).start();
    }

    private void onClickCheckBox(int index) {
        ((CheckBox) findViewById(R.id.authinfo_cb_day)).setChecked(index == 1);
        ((CheckBox) findViewById(R.id.authinfo_cb_project)).setChecked(index != 1);
        isDay = index == 1;
    }

    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        switch (mImageId) {
            case 1:
                mIDCardStr1 = uri.getPath();
                ((ImageView) findViewById(R.id.authinfo_iv_idcard1)).setImageURI(uri);
                break;
            case 2:
                mIDCardStr2 = uri.getPath();
                ((ImageView) findViewById(R.id.authinfo_iv_idcard2)).setImageURI(uri);
                break;
            case 3:
                mIDCardStr3 = uri.getPath();
                ((ImageView) findViewById(R.id.authinfo_iv_idcard3)).setImageURI(uri);
                break;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_SUBMIT_AUTHENTICATION:
                showMessage("认证信息提交成功，请缴纳保证金");
                mUserInfo.isrenzhen = 1;
                SPUtils.saveUserInfo(mUserInfo);
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_USER_PRICE);
                startActivity(new Intent(this, MainAuthenticationMoneyActivity.class));
                break;
        }
    }

    @Override
    public void onPictureUpload(int status, String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mPictureManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
