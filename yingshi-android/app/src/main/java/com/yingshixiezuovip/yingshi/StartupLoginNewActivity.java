package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.LoginWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskInfo;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.AuthModel;
import com.yingshixiezuovip.yingshi.model.UserModel;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.Md5Utils;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名称:StartupLoginNewActivity
 * 类描述:登录新
 * 创建时间: 2019-01-16-16:15
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class StartupLoginNewActivity extends BaseActivity implements UMAuthListener, Runnable {
    private EditText et_phone, et_password,et_fg_phone,et_code,et_fg_password;
    private LinearLayout lin_login, lin_input,lin_input_forget;
    private RelativeLayout rel_bg;
    private TextView tv_weixin, tv_phone_login, tv_login, tv_forget_password,tv_check_password,tv_get_code;
    private LoginWindow mLoginWindow;
    private UMShareAPI mShareAPI;
    private boolean isWechat = false;
    private AuthModel mAuthInfo;
    private boolean isShowInput=false;
    private Handler mHanler;
    private int mWaitTime = 0;
    private boolean isVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new, -1, false);
        initView();
        initWindow();

        mShareAPI = UMShareAPI.get(this);
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        lin_login = (LinearLayout) findViewById(R.id.lin_login);
        lin_input = (LinearLayout) findViewById(R.id.lin_input);
        rel_bg = (RelativeLayout) findViewById(R.id.rel_bg);
        rel_bg.setOnClickListener(this);
        tv_weixin = (TextView) findViewById(R.id.tv_weixin);
        tv_weixin.setOnClickListener(this);
        tv_phone_login = (TextView) findViewById(R.id.tv_phone_login);
        tv_phone_login.setOnClickListener(this);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
        tv_forget_password.setOnClickListener(this);
        lin_input.setVisibility(View.GONE);
        et_fg_phone=(EditText)findViewById(R.id.et_fg_phone);
        et_code=(EditText)findViewById(R.id.et_code);
        et_fg_password=(EditText)findViewById(R.id.et_fg_password);
        tv_check_password=(TextView)findViewById(R.id.tv_check_password);
        tv_check_password.setOnClickListener(this);
        lin_input_forget=(LinearLayout)findViewById(R.id.lin_input_forget);
        tv_get_code=(TextView)findViewById(R.id.tv_get_code);
        tv_get_code.setOnClickListener(this);
        final VideoView videoview=(VideoView)findViewById(R.id.videoView);
        final String videoPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login_video).toString();
        videoview.setVideoPath(videoPath);
        videoview.start();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoview.setVideoPath(videoPath);
                videoview.start();
            }
        });
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
    Animation mShowAction;
    Animation mHiddenAction;
    private void initWindow() {
        mLoginWindow = new LoginWindow(this);
        mLoginWindow.setOnClickListener(this);

        mShowAction = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.alpha_out);
    }

    private void authorize(SHARE_MEDIA platform) {
        mLoadWindow.show(R.string.text_request_auth);
        mShareAPI.getPlatformInfo(this, platform, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.tv_get_code:
                doSendVerificationCode();
                break;
            case R.id.tv_check_password:
                if (!checkRegistParams()) {
                    return;
                }
                doRequest();
                break;
            case R.id.rel_bg:
                    lin_input.setVisibility(View.GONE);
                    lin_input.setAnimation(mHiddenAction);
                    lin_login.setVisibility(View.VISIBLE);
                    lin_login.setAnimation(mShowAction);
                    lin_input_forget.setVisibility(View.GONE);
                break;
            case R.id.tv_phone_login:
                lin_input.setVisibility(View.VISIBLE);
                lin_input.setAnimation(mShowAction);
                lin_login.setVisibility(View.GONE);
                lin_login.setAnimation(mHiddenAction);
                lin_input_forget.setVisibility(View.GONE);
                break;
            case R.id.tv_login:
                if (checkLoginParams()) {
                    doLogin();
                }
                break;
            case R.id.tv_forget_password:
                lin_input_forget.setVisibility(View.VISIBLE);
                lin_input.setVisibility(View.GONE);
                lin_login.setVisibility(View.GONE);
//                startActivity(new Intent(this, StartupFindpwActivity.class));
                break;
            case R.id.tv_weixin:
                mLoginWindow.show();
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
    private void doSendVerificationCode() {
        if (!CommUtils.isMobileNo(et_fg_phone.getText().toString())) {
            showMessage(getString(R.string.phone_format_error));
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", et_fg_phone.getText().toString());
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(new TaskInfo(TaskType.TASK_TYPE_RESET_SendSMS, params, this));
    }
    private void doRequest() {
        String phone = et_fg_phone.getText().toString();
        String verifiCode = et_code.getText().toString();
        String password = et_fg_password.getText().toString();
        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("pwd", Md5Utils.md5Encode(password));
        params.put("code", verifiCode);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_RESET_PASSWORD, params, this);
    }
    private boolean checkRegistParams() {
        String phone = et_fg_phone.getText().toString();
        String verifiCode = et_code.getText().toString();
        String password = et_fg_password.getText().toString();
        if (!CommUtils.isMobileNo(phone)) {
            showMessage(getString(R.string.phone_format_error));
            return false;
        } else if (TextUtils.isEmpty(verifiCode)) {
            showMessage(getString(R.string.please_get_verification_code));
            return false;
        } else if (!CommUtils.validatePassword(password)) {
            showMessage(getString(R.string.password_format_error));
            return false;
        }
        return true;
    }
    private void setButtonStatus(boolean enable, String info) {
        tv_get_code.setEnabled(enable);
        tv_get_code.setText(info);
    }
    private boolean checkLoginParams() {
        if (TextUtils.isEmpty(et_phone.getText().toString())) {
            showMessage("请输入11位正确的手机号码");
            return false;
        } else if (TextUtils.isEmpty(et_password.getText().toString())) {
            showMessage("请输入登录密码");
            return false;
        }
        return true;
    }
    private void doLogin() {

       String  password = Md5Utils.md5Encode(et_password.getText().toString());
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", et_phone.getText().toString());
        params.put("pwd", password);

        mAuthInfo = new AuthModel();
        mAuthInfo.account = et_phone.getText().toString();
        mAuthInfo.pwd = password;
        mLoadWindow.show(R.string.text_logining);
        HttpUtils.doPost(new TaskInfo(TaskType.TASK_TYPE_LOGIN, params, this));
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
    public void run() {
        mHanler.sendEmptyMessage(0);
        mHanler.postDelayed(this, 1000);
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
          /*              if (userModel.data.iswritephone == 0) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userinfo", userModel.data);
                            Intent intent = new Intent(this, UserBindPhoneActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return;
                        }*/
                        if (userModel.data.logintype == 2 && userModel.data.type != 2 && userModel.data.type != 4 && userModel.data.type != 6
                                && userModel.data.type != 7) {
                            if (userModel.data.isbangpwd != 1) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("userinfo", userModel.data);
                                Intent intent = new Intent(this, UserBindPhoneActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                return;
                            }
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
            case TASK_TYPE_LOGIN:
                UserModel userModels = GsonUtil.fromJson(result.toString(), UserModel.class);
                if (userModels != null) {
                    SPUtils.saveAuthInfo(mAuthInfo);
                    SPUtils.saveUserInfo(userModels.data);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    YingApplication.getInstance().startActivity(intent);
                } else {
                    showMessage("登录失败，用户信息获取失败");
                }
                break;
            case TASK_TYPE_RESET_SendSMS:
                showMessage("验证码发送成功，请注意查收");
                mWaitTime = Configs.SMS_INTERFAL_TIME;
                mHanler.post(this);
                break;
            case TASK_TYPE_RESET_PASSWORD:
                showMessage("找回成功，请重新登录");
//                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
