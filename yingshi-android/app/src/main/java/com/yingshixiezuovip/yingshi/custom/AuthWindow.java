package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.MainAuthenticationInfoActivity;
import com.yingshixiezuovip.yingshi.MainAuthenticationMoneyActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.UserAuthenticationSelectActivity;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/5/17.
 */

public class AuthWindow extends BasePopupWindow {
    private int mType = 1;

    public AuthWindow(Context mContext) {
        super(mContext, false, false);
        setWidthHeight(250, 160);
        findViewById(R.id.auth_btn_cancel).setOnClickListener(this);
        findViewById(R.id.auth_btn_submit).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.auth_btn_submit:
                if (mType == 1) {//
                    mContext.startActivity(new Intent(mContext, UserAuthenticationSelectActivity.class));
                } else if (mType == 2) {
                    mContext.startActivity(new Intent(mContext, MainAuthenticationInfoActivity.class));
                } else if (mType == 3) {
                    mContext.startActivity(new Intent(mContext, MainAuthenticationMoneyActivity.class));
                } else if (mType == 4) {
                    Intent intent = new Intent(mContext, MainAuthenticationMoneyActivity.class);
                    intent.putExtra("company_auth", true);
                    mContext.startActivity(intent);
                }
                break;
        }
        cancel();
    }

    public int getType() {
        return mType;
    }

    public void show(String message, String leftName, int type) {
        show(message, leftName, "完善资料", type);
    }

    public void show(String message, String leftName, String rightName, int type) {
        ((TextView) findViewById(R.id.auth_tv_title)).setText(message);
        ((TextView) findViewById(R.id.auth_btn_cancel)).setText(leftName);
        ((TextView) findViewById(R.id.auth_btn_submit)).setText(rightName);
        this.mType = type;
        super.show();
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_auth_layout, null);
    }
}
