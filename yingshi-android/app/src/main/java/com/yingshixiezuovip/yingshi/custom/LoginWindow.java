package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class LoginWindow extends BasePopupWindow {

    public LoginWindow(Context mContext) {
        super(mContext);
        findViewById(R.id.loginwindow_btn_wechat).setOnClickListener(this);
        findViewById(R.id.loginwindow_btn_sina).setOnClickListener(this);
        findViewById(R.id.loginwindow_btn_cancel).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.loginwindow_btn_cancel:
                cancel();
                break;
        }
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_login_layout, null);
    }
}
