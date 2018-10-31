package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.utils.CommUtils;

/**
 * Created by Resmic on 18/1/19.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CateWindow extends PopupWindow {
    public CateWindow(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.window_cate_layout, null);
        setContentView(contentView);
        setWidth(CommUtils.dip2px(130));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
    }
}
