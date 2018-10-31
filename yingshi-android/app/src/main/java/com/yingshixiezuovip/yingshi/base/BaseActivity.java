package com.yingshixiezuovip.yingshi.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yingshixiezuovip.yingshi.MainChatListActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.StartupLoginActivity;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.LoadWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.TaskListener;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BaseEaseUser;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.LoginUtils;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.ToastUtils;

import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by Resmic on 2016/7/14.
 */


public abstract class BaseActivity extends FragmentActivity implements TaskListener, View.OnClickListener, UMShareListener {
    protected boolean isActivityRun;
    protected LoadWindow mLoadWindow;
    protected UserInfo mUserInfo;
    protected AlertWindow mAlertWindow;
    protected AlertWindow mInfoWindow;
    protected RotateAnimation mLoadAnim;
    protected boolean needAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoadWindow = new LoadWindow(this);
        mUserInfo = SPUtils.getUserInfo(this);
        if (needAuth && mUserInfo == null) {
            Intent intent = new Intent(this, StartupLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            YingApplication.getInstance().startActivity(intent);
        }
        mInfoWindow = new AlertWindow(this, false);
        mInfoWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    startActivity(new Intent(BaseActivity.this, MainChatListActivity.class));
                }
            }
        });
        initLoadAnim();
    }

    private EMMessageListener listener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            dealMessage(list);
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            dealMessage(list);
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
        }

        private void dealMessage(List<EMMessage> list) {
            L.d("dealMessage");
            BaseEaseUser easeUser;
            for (EMMessage emMessage : list) {
                easeUser = SPUtils.getBaseEaseUser(BaseActivity.this, emMessage.getFrom());
                if (easeUser == null) {
                    LoginUtils.getUserInfo(emMessage.getFrom());
                }
            }

            if (isActivityRun) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInfoWindow.show("", "有信息哦", "取消", "查看");
                    }
                });
            }
        }
    };

    private void initLoadAnim() {
        mLoadAnim = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mLoadAnim.setDuration(1200);
        mLoadAnim.setInterpolator(new LinearInterpolator());

        mLoadAnim.setRepeatCount(-1);
    }

    protected void showLoadLayout() {
        findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.load_iv_icon).startAnimation(mLoadAnim);
    }

    protected void closeLoadLayout() {
        findViewById(R.id.load_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.load_iv_icon).clearAnimation();
    }

    @Override
    public void setContentView(int layoutResID) {
        View tempView = View.inflate(this, layoutResID, null);
        setContentView(tempView);
    }

    public void setContentView(int layoutResID, int titleId) {
        View tempView = View.inflate(this, layoutResID, null);
        setContentView(tempView);
        if (titleId != -1) {
            setActivityTitle(titleId);
        } else {
            setActionBarVisibility(View.GONE);
        }
    }

    public void setContentView(int layoutResID, int titleId, boolean isShowLine) {
        setContentView(layoutResID, titleId);
        findViewById(R.id.base_view_line).setVisibility(isShowLine ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.activity_base_layout);
        EventBus.getDefault().register(this);
        FrameLayout mainView = ((FrameLayout) findViewById(R.id.base_fm_layout));
        mainView.addView(view);
        initListener();
    }

    protected void initListener() {
        findViewById(R.id.lin_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.base_mainlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommUtils.closeKeyboard(BaseActivity.this);
            }
        });
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
    }


    protected void setActivityTitle(String title) {
        ((TextView) findViewById(R.id.base_tv_title)).setText(title);
    }

    protected void setActivityTitle(int strId) {
        String title;
        if (strId > 0) {
            title = getString(strId);
        } else {
            title = "";
        }
        setActivityTitle(title);
    }

    protected void setActivityTitleSize(float size) {
        ((TextView) findViewById(R.id.base_tv_title)).setTextSize(size);
    }

    protected void setActionBarVisibility(int visibility) {
        findViewById(R.id.base_fm_actionbar).setVisibility(visibility);
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_enter_next_anim, R.anim.activity_out_up_anim);
    }

    public void superStartActivity(Intent intent) {
        super.startActivity(intent);
    }

    public void startMActivity(Intent intent) {
        super.startActivityForResult(intent, 10001);
        overridePendingTransition(R.anim.vertical_in_anim, R.anim.activity_nomove_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_enter_next_anim, R.anim.activity_out_up_anim);

    }

    public void startMActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.vertical_in_anim, R.anim.activity_nomove_anim);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter_up_anim, R.anim.activity_out_next_anim);
    }

    public void onSuperBackPressed() {
        super.onBackPressed();
    }

    public void onBackPressedToBottom() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.vertical_out_anim);
    }


    public void onBackPressed(boolean isAnimation) {
        if (isAnimation) {
            onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(listener);
        isActivityRun = true;
        mLoadWindow.cancel();
    }

    protected void cleanMessageListener() {
        EMClient.getInstance().chatManager().removeMessageListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRun = false;
        closeLoadLayout();
    }

    protected void showMessage(int strid) {
        showMessage(getString(strid));
    }

    protected void showMessage(final String message) {
        ToastUtils.showMessage(BaseActivity.this, message);
    }

    @Override
    public void taskStarted(TaskType type) {
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
    }

    @Override
    public void taskIsCanceled(TaskType type) {

    }

    public void onEventMainThread(BaseEvent event) {
        switch (event.getEventType()) {
            default:
                break;
        }
    }

    protected void delayFinish(final long delay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }).start();
    }

    private long mLastClickTime;
    private int mLastClickID;

    @Override
    public void onClick(View v) {
        if (mLastClickID == v.getId() && System.currentTimeMillis() - mLastClickTime < Configs.SINGLE_CLICK_INTERVAL) {
            L.d("已停止点击，连续两次点击事件小于" + Configs.SINGLE_CLICK_INTERVAL + "ms");
            return;
        }
        /**
         * 相同按钮在{@link Configs#SINGLE_CLICK_INTERVAL} 毫秒内不能重复点击
         */
        mLastClickID = v.getId();
        mLastClickTime = System.currentTimeMillis();
        onSingleClick(v);
    }

    protected void onSingleClick(View v) {
    }

    protected int getWColor(int colorid) {
        return getResources().getColor(colorid);
    }

    /**
     * 重写该方法，取消点击声音
     *
     * @param id
     * @return
     */
    @Override
    public View findViewById(@IdRes int id) {
        View v = super.findViewById(id);
        if (v != null) {
            v.setSoundEffectsEnabled(false);
        }
        return super.findViewById(id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(listener);
//        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        mLoadWindow.show(R.string.umeng_socialize_text_waitting_share);
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        mLoadWindow.cancel();
        showMessage("分享成功");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        mLoadWindow.cancel();
        showMessage("分享失败，请稍后重试");
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        mLoadWindow.cancel();
        showMessage("取消分享");
    }
}
