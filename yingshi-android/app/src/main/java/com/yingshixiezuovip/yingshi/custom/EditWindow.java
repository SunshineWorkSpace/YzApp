package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.utils.CommUtils;

/**
 * Created by Resmic on 18/1/19.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class EditWindow extends PopupWindow {
    private int type;

    public EditWindow(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.window_edit_layout, null);
        setContentView(contentView);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
    }

    public void setType(int type) {
        this.type = type;
        ((ImageView) getContentView().findViewById(R.id.edit_btn_replace))
                .setImageResource((type == PublishModel.TYPE_LINK || type == 4) ? R.mipmap.post05_icon_a : R.mipmap.post09_icon_a);
        getContentView().findViewById(R.id.edit_btn_edit_line).setVisibility(type == 4 ? View.VISIBLE : View.GONE);
        getContentView().findViewById(R.id.edit_btn_edit).setVisibility(type == 4 ? View.VISIBLE : View.GONE);
    }

    public int getType() {
        return type;
    }
}
