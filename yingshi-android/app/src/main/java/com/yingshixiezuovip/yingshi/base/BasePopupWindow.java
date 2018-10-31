package com.yingshixiezuovip.yingshi.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.ToastUtils;

/**
 * Created by Resmic on 2016/8/26.
 */

public abstract class BasePopupWindow implements View.OnClickListener, DialogInterface.OnKeyListener {
    protected Dialog mDialog;
    private View mMainLayout;
    private boolean isCancel = true;

    public BasePopupWindow(Context mContext) {
        this.mContext = mContext;
        mDialog = new Dialog(mContext, R.style.transparent_frame_style);
        mMainLayout = createView();
        mDialog.setContentView(mMainLayout);
        mDialog.setOnKeyListener(this);
        setAnimations();
    }

    public BasePopupWindow(Context mContext, boolean isAnimation, boolean isCancel) {
        this.mContext = mContext;
        this.isCancel = isCancel;
        mDialog = new Dialog(mContext, R.style.transparent_frame_style);
        mMainLayout = createView();
        mDialog.setContentView(mMainLayout);
        if (isAnimation) {
            setAnimations();
        } else {
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.gravity = Gravity.CENTER;
            window.setWindowAnimations(R.style.fade_window_style);
        }
    }


    protected void setWidthHeight(boolean isall, int width, int height) {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.gravity = Gravity.CENTER;
        if (isall) {
            wl.x = 0;
            wl.y = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            wl.width = CommUtils.dip2px(mContext, width);
            wl.height = CommUtils.dip2px(mContext, height);
        }
        mDialog.onWindowAttributesChanged(wl);
        mDialog.setCanceledOnTouchOutside(isCancel);
    }

    protected void setWidthHeight(int width, int height) {
        setWidthHeight(false, width, height);
    }

    public BasePopupWindow(Context mContext, int styleId) {
        this.mContext = mContext;
        mDialog = new Dialog(mContext, styleId);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mMainLayout = createView();
        mDialog.setContentView(mMainLayout);
        setAnimations();
    }


    private void setAnimations() {
        Window window = mDialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mDialog.onWindowAttributesChanged(wl);
        mDialog.setCanceledOnTouchOutside(isCancel);
    }

    protected Context mContext;

    public void show() {
        cancel();
        if (mDialog != null && !mDialog.isShowing() && !((Activity) mContext).isFinishing()) {
            mDialog.show();
        }
    }

    public void cancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public abstract View createView();

    public View getView() {
        return mMainLayout;
    }

    private long mLastClickTime;

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mLastClickTime < Configs.SINGLE_CLICK_INTERVAL) {
            L.d("已停止点击，连续两次点击事件小于" + Configs.SINGLE_CLICK_INTERVAL + "ms");
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        onSingleClick(v);

    }

    protected void onSingleClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        if (mDialog != null && onDismissListener != null)
            mDialog.setOnDismissListener(onDismissListener);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        if (mDialog != null && onCancelListener != null)
            mDialog.setOnCancelListener(onCancelListener);
    }

    public View findViewById(int id) {
        return getView().findViewById(id);
    }

    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void showMessage(String message) {
        ToastUtils.showMessage((Activity) mContext, message);
    }

    public String getWString(int resid) {
        return mContext.getString(resid);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }



}
