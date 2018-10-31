package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;


public class PhotoWindow extends BasePopupWindow {

    public PhotoWindow(Context mContext) {
        super(mContext);
        findViewById(R.id.dialog_item1).setOnClickListener(this);
        findViewById(R.id.dialog_item2).setOnClickListener(this);
        findViewById(R.id.dialog_item3).setOnClickListener(this);
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.photoalbum_dialog_buttom, null);
    }

}

