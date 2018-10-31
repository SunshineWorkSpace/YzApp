package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/9/13.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CoverWindow extends BasePopupWindow {
    public CoverWindow(Context mContext) {
        super(mContext);
        findViewById(R.id.dialog_item1).setOnClickListener(this);
        findViewById(R.id.dialog_item2).setOnClickListener(this);
        findViewById(R.id.dialog_item3).setOnClickListener(this);
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_cover_window_layout, null);
    }
}
