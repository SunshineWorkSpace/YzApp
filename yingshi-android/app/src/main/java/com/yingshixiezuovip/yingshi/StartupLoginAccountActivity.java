package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskInfo;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.AuthModel;
import com.yingshixiezuovip.yingshi.model.UserModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.Md5Utils;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class StartupLoginAccountActivity extends BaseActivity {
    private boolean isVisible = false;
    private AuthModel mAuthInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_login_account, -1, false);

        initView();
    }

    private void initView() {
        findViewById(R.id.account_btn_back).setOnClickListener(this);
        findViewById(R.id.account_btn_findpw).setOnClickListener(this);
        findViewById(R.id.account_btn_submit).setOnClickListener(this);
        findViewById(R.id.account_btn_visibility).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.account_btn_back:
                onBackPressed();
                break;
            case R.id.account_btn_findpw:
                startActivity(new Intent(this, StartupFindpwActivity.class));
                break;
            case R.id.account_btn_submit:
                if (checkLoginParams()) {
                    doLogin();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.account_btn_visibility:
                isVisible = !isVisible;
                if (isVisible) {
                    ((EditText) findViewById(R.id.account_et_password)).setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    ((EditText) findViewById(R.id.account_et_password)).setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                findViewById(R.id.account_et_password).postInvalidate();
                CharSequence charSequence = ((EditText) findViewById(R.id.account_et_password)).getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
        }
    }

    private boolean checkLoginParams() {
        String phone = ((EditText) findViewById(R.id.account_et_phone)).getText().toString();
        String password = ((EditText) findViewById(R.id.account_et_password)).getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showMessage("请输入11位正确的手机号码");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showMessage("请输入登录密码");
            return false;
        }
        return true;
    }

    private void doLogin() {
        String phone = ((EditText) findViewById(R.id.account_et_phone)).getText().toString();
        String password = ((EditText) findViewById(R.id.account_et_password)).getText().toString();
        password = Md5Utils.md5Encode(password);
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", phone);
        params.put("pwd", password);

        mAuthInfo = new AuthModel();
        mAuthInfo.account = phone;
        mAuthInfo.pwd = password;
        mLoadWindow.show(R.string.text_logining);
        HttpUtils.doPost(new TaskInfo(TaskType.TASK_TYPE_LOGIN, params, this));
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            if (result instanceof BaseThrowable) {
                int status = ((BaseThrowable) result).getErrorCode();
                if (status == 400) {
                    showMessage("参数错误");
                } else if (status == 404) {
                    showMessage("用户不存在或密码错误");
                } else if (status == 500) {
                    showMessage("服务器错误");
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_LOGIN:
                UserModel userModel = GsonUtil.fromJson(result.toString(), UserModel.class);
                if (userModel != null) {
                    SPUtils.saveAuthInfo(mAuthInfo);
                    SPUtils.saveUserInfo(userModel.data);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    YingApplication.getInstance().startActivity(intent);
                } else {
                    showMessage("登录失败，用户信息获取失败");
                }
                break;
        }
    }
}
