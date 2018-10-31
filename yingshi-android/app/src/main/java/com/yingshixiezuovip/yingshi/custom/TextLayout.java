package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.FontModel;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.recyclerview.base.ItemViewDelegate;
import com.yingshixiezuovip.yingshi.quote.recyclerview.base.ViewHolder;
import com.yingshixiezuovip.yingshi.utils.L;


/**
 * Created by Resmic on 18/1/22.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class TextLayout extends LinearLayout {
    private FontModel fontModel;
    private TextView editText;

    public TextLayout(Context context) {
        super(context);

        initView();
    }

    public TextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_textlayout_layout, this);

        editText = (TextView) findViewById(R.id.text_et_input);

        enable(false);

        setFontModel(new FontModel());
    }

    public void enable(boolean edit) {
        if (edit) {
            setFocusableInTouchMode(true);
            setFocusable(true);
        } else {
            editText.clearFocus();
            requestFocus();
        }
    }

    public void setFontModel(FontModel fontModel) {
        this.fontModel = fontModel;

        editText.setGravity(fontModel.isCenter() ? Gravity.CENTER : Gravity.LEFT);
        editText.setTextSize(fontModel.getFont());
        editText.setTextColor(Color.parseColor(fontModel.getColor()));
        editText.getPaint().setFakeBoldText(fontModel.isBlod());
        editText.setText(editText.getText());

        postInvalidate();
    }

    public FontModel getFontModel() {
        return fontModel;
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public String getText() {
        return (editText.getText().toString() + "").trim();
    }

    public interface OnEditClickListener {
        void onEditClick(View view);
    }

    private OnEditClickListener onEditClickListener;

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }
}
