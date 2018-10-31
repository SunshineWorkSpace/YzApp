package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class LoadWindow extends BasePopupWindow {
    private TextView tvMessage;

    public LoadWindow(Context mContext) {
        super(mContext, false, false);
        tvMessage = ((TextView) findViewById(R.id.tv_message));
        setCancelable(false);
        setWidthHeight(250, 80);
    }

    public void show(int strid) {
        tvMessage.setText(strid);
        super.show();
    }

    public void showMessage(String progress) {
        tvMessage.setText(progress);
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_loading_layout, null);
    }

    public void setCancelable(boolean isCancelable) {
        mDialog.setCancelable(isCancelable);
    }
}
