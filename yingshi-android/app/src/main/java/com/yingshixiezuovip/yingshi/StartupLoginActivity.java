package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.LoginWindow;
import com.yingshixiezuovip.yingshi.custom.RegistWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.AuthModel;
import com.yingshixiezuovip.yingshi.model.UserModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class StartupLoginActivity extends BaseActivity implements UMAuthListener {
    private LoginWindow mLoginWindow;
    private RegistWindow mRegistWindow;
    private UMShareAPI mShareAPI;
    private boolean isWechat = false;
    private AuthModel mAuthInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_login, -1, false);

        initView();
        initWindow();

        mShareAPI = UMShareAPI.get(this);
    }

    private void initView() {
        findViewById(R.id.login_btn_main).setOnClickListener(this);
        findViewById(R.id.login_btn_submit).setOnClickListener(this);

        findViewById(R.id.login_btn_other).setOnClickListener(this);
        findViewById(R.id.login_btn_phone).setOnClickListener(this);
        findViewById(R.id.login_btn_account).setOnClickListener(this);
        findViewById(R.id.login_btn_agreement).setOnClickListener(this);
    }


    private void initWindow() {
        mLoginWindow = new LoginWindow(this);
        mLoginWindow.setOnClickListener(this);
        mRegistWindow = new RegistWindow(this);
        mRegistWindow.setOnRegistClickListener(new RegistWindow.OnRegistClickListener() {
            @Override
            public void onRegistClick(String phone, String password, String verifiCode) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("phone", phone);
                params.put("pwd", password);
                params.put("code", verifiCode);
                mLoadWindow.show(R.string.text_request);
                HttpUtils.doPost(TaskType.TASK_TYPE_REGIST, params, StartupLoginActivity.this);
            }
        });
    }

    private void authorize(SHARE_MEDIA platform) {
        mLoadWindow.show(R.string.text_request_auth);
        mShareAPI.getPlatformInfo(this, platform, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.login_btn_main:
                findViewById(R.id.lin_login_layout).setVisibility(View.GONE);
                break;
            case R.id.login_btn_submit:
                findViewById(R.id.lin_login_layout).setVisibility(View.VISIBLE);
                break;
            case R.id.login_btn_other:
                mLoginWindow.show();
                break;
            case R.id.login_btn_phone:
                mRegistWindow.show();
                break;
            case R.id.login_btn_account:
                startActivity(new Intent(this, StartupLoginAccountActivity.class));
                break;
            case R.id.login_btn_agreement:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.CURL, Configs.AgreementURL);
                startActivity(intent);
                break;
            case R.id.loginwindow_btn_wechat:
                isWechat = true;
                authorize(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.loginwindow_btn_sina:
                authorize(SHARE_MEDIA.SINA);
                break;
        }
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        mLoginWindow.cancel();
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        L.d("onComplete::" + GsonUtil.toJson(map));
        mLoadWindow.cancel();
        HashMap<String, Object> params = new HashMap<>();
        params.put("access_token", map.get(isWechat ? "unionid" : "accessToken"));
        params.put("head", map.get(isWechat ? "iconurl" : "avatar_hd"));
        params.put("nickname", map.get("screen_name"));

        mAuthInfo = new AuthModel();
        mAuthInfo.access_token = (String) params.get("access_token");
        mAuthInfo.head = (String) params.get("head");
        mAuthInfo.nickname = (String) params.get("nickname");

        if (TextUtils.isEmpty(String.valueOf(params.get("access_token")))) {
            showMessage("授权参数异常，请尝试重新登录");
        } else {
            mLoadWindow.show(R.string.text_logining);
            HttpUtils.doPost(isWechat ? TaskType.TASK_TYPE_LOGIN_WECHAT : TaskType.TASK_TYPE_LOGIN_SINA, params, this);
        }
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        mLoadWindow.cancel();
        showMessage(throwable.getMessage());
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        mLoadWindow.cancel();
        showMessage("取消授权");
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        isWechat = false;
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            if (result instanceof BaseThrowable) {
                switch (((BaseThrowable) result).getErrorCode()) {
                    case 400:
                        showMessage("参数错误");
                        break;
                    case 406:
                        showMessage("验证码错误，请重新输入");
                        break;
                    case 403:
                        showMessage("该手机号码已被注册");
                        break;
                    default:
                        showMessage("服务器异常，注册失败");
                        break;
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_REGIST:
            case TASK_TYPE_LOGIN_WECHAT:
            case TASK_TYPE_LOGIN_SINA:
                UserModel userModel = GsonUtil.fromJson(result.toString(), UserModel.class);
                if (userModel != null) {
                    if (userModel.result.code == 200) {
                        if (userModel.data.iswritephone == 0) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userinfo", userModel.data);
                            Intent intent = new Intent(this, UserBindPhoneActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return;
                        }
                        SPUtils.saveAuthInfo(mAuthInfo);
                        SPUtils.saveUserInfo(userModel.data);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        YingApplication.getInstance().startActivity(intent);
                    } else if (userModel.result.code == 400) {
                        showMessage("参数错误");
                    } else if (userModel.result.code == 500) {
                        showMessage("服务器错误");
                    }
                } else {
                    showMessage("登录失败，用户信息获取失败");
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
