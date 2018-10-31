package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/9/14.
 */

public class UrlWindow extends BasePopupWindow {
    public UrlWindow(Context mContext) {
        super(mContext);
        findViewById(R.id.dialog_item2).setOnClickListener(this);
        findViewById(R.id.dialog_item3).setOnClickListener(this);

    }

    public void show(String message) {
        ((TextView) findViewById(R.id.dialog_item1)).setText(message);
        super.show();
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_url_window_layout, null);
    }
}
