package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/6/1.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PhoneWindow extends BasePopupWindow {
    private int type = 1;

    public PhoneWindow(Context mContext, int type) {
        super(mContext);
        this.type = type;
        findViewById(R.id.dialog_item1).setOnClickListener(this);
        findViewById(R.id.dialog_item2).setOnClickListener(this);
        findViewById(R.id.dialog_item3).setOnClickListener(this);

        if (type == 2) {
            ((TextView) findViewById(R.id.dialog_item1)).setTextSize(16);
            ((TextView) findViewById(R.id.dialog_item1)).setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            ((TextView) findViewById(R.id.dialog_item2)).setTextColor(mContext.getResources().getColor(R.color.colorBlue));
        }
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_phone_layout, null);
    }

    public void show(String s) {
        ((TextView) findViewById(R.id.dialog_item1)).setText(s);
        show();
    }

    public void show(String message1, String message2) {
        ((TextView) findViewById(R.id.dialog_item1)).setText(message1);
        ((TextView) findViewById(R.id.dialog_item2)).setText(message2);
        show();

    }
}
