package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;


/**
 * Created by Resmic on 2016/9/6.
 */

public class AlertWindow extends BasePopupWindow {

    public AlertWindow(Context mContext, boolean isNeedTitle) {
        super(mContext, false, true);
        initView();
        if (isNeedTitle) {
            setWidthHeight(260, 152);
            getView().findViewById(R.id.alert_title).setVisibility(View.VISIBLE);
            ((TextView) getView().findViewById(R.id.alert_message)).setTextColor(Color.parseColor("#FF63677F"));
            ((TextView) getView().findViewById(R.id.alert_message)).setTextSize(15);
        } else {
            setWidthHeight(260, 142);
            getView().findViewById(R.id.alert_title).setVisibility(View.GONE);
            ((TextView) getView().findViewById(R.id.alert_message)).setTextColor(Color.parseColor("#373C5A"));
            ((TextView) getView().findViewById(R.id.alert_message)).setTextSize(17);
        }
    }


    public AlertWindow(Context mContext, boolean isNeedTitle, int width, int height) {
        super(mContext, false, true);
        initView();
        if (isNeedTitle) {
            setWidthHeight(width, height);
            getView().findViewById(R.id.alert_title).setVisibility(View.VISIBLE);
            ((TextView) getView().findViewById(R.id.alert_message)).setTextColor(Color.parseColor("#FF63677F"));
            ((TextView) getView().findViewById(R.id.alert_message)).setTextSize(15);
        } else {
            setWidthHeight(width, height);
            getView().findViewById(R.id.alert_title).setVisibility(View.GONE);
            ((TextView) getView().findViewById(R.id.alert_message)).setTextColor(Color.parseColor("#373C5A"));
            ((TextView) getView().findViewById(R.id.alert_message)).setTextSize(17);
        }
    }

    private void initView() {
        getView().findViewById(R.id.alert_btn_cancel).setOnClickListener(this);
        getView().findViewById(R.id.alert_btn_submit).setOnClickListener(this);
    }


    public void show(String title, String message, String leftName, String rightName) {
        ((TextView) getView().findViewById(R.id.alert_title)).setText(title);
        ((TextView) getView().findViewById(R.id.alert_message)).setText(message);
        ((TextView) getView().findViewById(R.id.alert_btn_cancel)).setText(leftName);
        ((TextView) getView().findViewById(R.id.alert_btn_submit)).setText(rightName);
        super.show();
    }

    public void showMessage(String message) {
        ((TextView) getView().findViewById(R.id.alert_message)).setText(message);
        getView().findViewById(R.id.alert_btn_submit).setVisibility(View.GONE);
        getView().findViewById(R.id.alert_btn_cancel).setBackgroundResource(R.drawable.common_circular_btn_buttom_selector);
        ((TextView) getView().findViewById(R.id.alert_btn_cancel)).setText(R.string.text_server_connect_fail);
        getView().findViewById(R.id.alert_vline).setVisibility(View.GONE);
        super.show();
    }

    public void setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
        cancel();
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_alert_layout, null);
    }

    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
