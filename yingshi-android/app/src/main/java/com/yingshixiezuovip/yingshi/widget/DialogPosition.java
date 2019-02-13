package com.yingshixiezuovip.yingshi.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;

/**
 * 类名称:DialogPosition
 * 类描述:
 * 创建时间: 2019-01-22-16:27
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class DialogPosition extends Dialog {


    public DialogPosition(@NonNull Context context) {
        super(context);
    }

    public DialogPosition(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogPosition(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements View.OnClickListener {
        private Context context;
        private TextView tv_clean, tv_yes;
        private EditText et_position;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        DialogPosition dialog;
        public Builder(Context context) {
            this.context = context;
        }
        public interface OnClickListener {
            void onClick(DialogPosition dialog, int position,String content);
        }
        private OnClickListener mOnItemClickListener;

        public void setmOnItemClickListener(OnClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        public DialogPosition.Builder setPositiveButton(DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public DialogPosition create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            dialog = new DialogPosition(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_position, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 500));
            et_position=(EditText)layout.findViewById(R.id.et_position);
            tv_clean = (TextView) layout.findViewById(R.id.tv_clean);
            tv_yes = (TextView) layout.findViewById(R.id.tv_yes);
            tv_clean.setOnClickListener(this);
            tv_yes.setOnClickListener(this);
            dialog.setContentView(layout);
            return dialog;
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_clean:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(dialog,
                                0,"");
                    }
                    break;
                case R.id.tv_yes:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(dialog,
                                1,et_position.getText().toString());
                    }
                    break;

            }
        }



    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(30, 0, 30, 0);

        getWindow().setAttributes(layoutParams);

    }
}
