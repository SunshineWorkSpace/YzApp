package com.yingshixiezuovip.yingshi.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

public class ChoiceVideoWindow extends PopupWindow {
    public ChoiceVideoWindow(Context context) {
        super(context);

        initView(context);
    }

    private void initView(final Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.window_choice_video_layout, null);
        setContentView(contentView);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(CommUtils.dip2px(130));
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());


        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity) context).getWindow().setAttributes(lp);

            }
        });
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        WindowManager.LayoutParams lp = ((Activity) getContentView().getContext()).getWindow().getAttributes();
        lp.alpha = 0.7f;
        ((Activity) getContentView().getContext()).getWindow().setAttributes(lp);

        super.showAsDropDown(anchor, xoff, yoff);
    }
}
