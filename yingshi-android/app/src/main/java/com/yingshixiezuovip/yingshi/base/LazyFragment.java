package com.yingshixiezuovip.yingshi.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by yuhua.gou on 2018/11/8.
 */

public abstract class LazyFragment extends Fragment implements TaskListener,UMShareListener,
        View.OnClickListener{
    protected Dialog mLoadingDialog = null;
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    public abstract int getLayoutId();

    protected View mRootView;
    protected LoadWindow mLoadWindow;
    protected UserInfo mUserInfo;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    protected BaseActivity mBaseActivity;

    public YingApplication getApplication() {
        return (YingApplication) getActivity().getApplication();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
            this.initViews();
            this.initData(savedInstanceState);
            //获得索引值
            isPrepared = true;
            initLoad();
        }

        //因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }


    public void showDefalutLoadingDialog(){
        showLoadingDialog(mBaseActivity,"加载中...");
    }

    /**
     * 加载框
     *
     * @param title 提示
     */
    protected void showLoadingDialog(Context activity, String title) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new Dialog(activity);
            mLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View v = LayoutInflater.from(activity).inflate(R.layout.loading_dialog, null);
            mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mLoadingDialog.setContentView(v);
            TextView loadingtitle = (TextView) v.findViewById(R.id.tv_loadingtitle);
            loadingtitle.setText(title);
        }
        mLoadingDialog.show();
    }

    /**
     * 关闭dialog
     */
    protected void dimissLoadingDialog() {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
        }catch (Exception e){

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadWindow = new LoadWindow(getActivity());
    }

    public void startActivityWithNewTask(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    protected void showMessage(int strid) {
        showMessage(getString(strid));
    }

    protected void showMessage(final String message) {
        if (getActivity() != null) {
            ToastUtils.showMessage(getActivity(), message);
        }
    }

    private void initLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        lazyLoad();
    }


    /**
     * 可见
     */
    protected void onVisible() {
        initLoad();
    }


    /**
     * 不可见
     */
    protected void onInvisible() {


    }

    /**
     * 初始化view
     * initViews
     *
     * @since 1.0
     */
    protected abstract void initViews();

    /**
     * 初始化数据
     * initData
     *
     * @since 1.0
     */
    protected abstract void initData(Bundle savedInstanceState);

    protected View findViewById(int id) {
        return mRootView.findViewById(id);
    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();

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
