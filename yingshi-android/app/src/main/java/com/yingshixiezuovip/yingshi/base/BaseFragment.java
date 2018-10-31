package com.yingshixiezuovip.yingshi.base;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.custom.LoadWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.TaskListener;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.ToastUtils;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment implements TaskListener, View.OnClickListener, UMShareListener {
    protected LoadWindow mLoadWindow;
    protected UserInfo mUserInfo;

    public BaseFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mLoadWindow = new LoadWindow(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_enter_next_anim, R.anim.activity_out_up_anim);
    }

    public void startMActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.vertical_in_anim, R.anim.activity_nomove_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.activity_enter_next_anim, R.anim.activity_out_up_anim);

    }


    protected void showMessage(int strid) {
        showMessage(getString(strid));
    }

    protected void showMessage(final String message) {
        if (getActivity() != null) {
            ToastUtils.showMessage(getActivity(), message);
        }
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

    private long mLastClickTime;
    private long mLastViewID = -1;

    /**
     * 为了防止暴力点击，单击间隔必须大于{@link Configs#SINGLE_CLICK_INTERVAL}
     * 同时，子类必须重写{@link BaseActivity#onSingleClick(View)}方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (mLastViewID == v.getId() && System.currentTimeMillis() - mLastClickTime < Configs.SINGLE_CLICK_INTERVAL) {
            L.d("已停止点击，连续两次点击事件小于" + Configs.SINGLE_CLICK_INTERVAL + "ms");
            return;
        }
        mLastViewID = v.getId();
        mLastClickTime = System.currentTimeMillis();
        onSingleClick(v);
    }

    protected void onSingleClick(View v) {
    }


    public View findViewById(int resid) {
        View v = getView().findViewById(resid);
        if (v != null) {
            v.setSoundEffectsEnabled(false);
        }
        return v;
    }

    public void onEventMainThread(BaseEvent event) {
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = SPUtils.getUserInfo(getActivity());
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
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
