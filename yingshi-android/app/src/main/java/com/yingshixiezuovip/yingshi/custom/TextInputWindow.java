package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;


/**
 * Created by Resmic on 18/2/1.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class TextInputWindow extends BasePopupWindow {
    public TextInputWindow(Context mContext, final OnTextInputFinishListener onTextInputFinishListener) {
        super(mContext, false, false);
        setWidthHeight(300, 230);

        findViewById(R.id.alert_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        findViewById(R.id.alert_btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTextInputFinishListener != null) {
                    onTextInputFinishListener.onTextInputFinish((((EditText) findViewById(R.id.text_input)).getText().toString() + "").trim());
                }
                cancel();
            }
        });
    }

    public void show(String text) {
        ((EditText) findViewById(R.id.text_input)).setText(text);
        super.show();
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.window_textinput_layout, null);
    }

    public interface OnTextInputFinishListener {
        void onTextInputFinish(String text);
    }

}
