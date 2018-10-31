package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 2017/5/5.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CommentWindow extends BasePopupWindow {
    private EditText mCommEdit;
    private OnCommentClickListener onCommentClickListener;

    public CommentWindow(Context paramContext) {
        super(paramContext, false, true);
        setWidthHeight(240, 180);
        initView();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            findViewById(R.id.common_btn_submit).setEnabled(s.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void initView() {
        mCommEdit = (EditText) findViewById(R.id.common_et_input);
        mCommEdit.addTextChangedListener(textWatcher);
        findViewById(R.id.common_btn_submit).setOnClickListener(this);
    }

    public void cancel() {
        this.mCommEdit.setText(null);
        super.cancel();
    }

    public View createView() {
        return View.inflate(this.mContext, R.layout.window_comment_layout, null);
    }

    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if ((v.getId() == R.id.common_btn_submit) && (this.onCommentClickListener != null))
            this.onCommentClickListener.onCommentClick(this.mCommEdit.getText().toString());
    }

    public void setOnCommentClickListener(OnCommentClickListener paramOnCommentClickListener) {
        this.onCommentClickListener = paramOnCommentClickListener;
    }

    public interface OnCommentClickListener {
        void onCommentClick(String comment);
    }
}
