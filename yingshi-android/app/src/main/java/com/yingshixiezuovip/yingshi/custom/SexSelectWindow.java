package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/8/17.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class SexSelectWindow extends BasePopupWindow {
    public SexSelectWindow(Context mContext) {
        super(mContext);
        findViewById(R.id.sex_btn_man).setOnClickListener(this);
        findViewById(R.id.sex_btn_female).setOnClickListener(this);
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_sex_select_layout, null);
    }
}
