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
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;

/**
 * 类名称:DialogCleanPosition
 * 类描述:
 * 创建时间: 2019-01-22-17:38
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class DialogCleanPosition extends Dialog {


    public DialogCleanPosition(@NonNull Context context) {
        super(context);
    }

    public DialogCleanPosition(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogCleanPosition(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements View.OnClickListener {
        private Context context;
        private TextView tv_clean, tv_yes,tv_change,tv_close;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        DialogCleanPosition dialog;
        public Builder(Context context) {
            this.context = context;
        }

        public DialogCleanPosition.Builder setPositiveButton(DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public DialogCleanPosition create(String isClean) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            dialog = new DialogCleanPosition(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_clean_position, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 500));
            tv_clean = (TextView) layout.findViewById(R.id.tv_clean);
            tv_yes = (TextView) layout.findViewById(R.id.tv_yes);
            tv_change=(TextView)layout.findViewById(R.id.tv_change);
            tv_close=(TextView)layout.findViewById(R.id.tv_close);
            if(isClean.equals("0")){
                tv_clean.setText("修改作品");
            }else {
                tv_clean.setText("取消认领");
            }
            tv_close.setOnClickListener(this);
            tv_change.setOnClickListener(this);
            tv_clean.setOnClickListener(this);
            tv_yes.setOnClickListener(this);
            dialog.setContentView(layout);
            return dialog;
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_clean:
                    if (positiveButtonClickListener != null) {
                        positiveButtonClickListener.onClick(dialog,
                                0);
                    }
                    break;
                case R.id.tv_yes:
                    if (positiveButtonClickListener != null) {
                        positiveButtonClickListener.onClick(dialog,
                                1);
                    }
                    break;
                case R.id.tv_change:
                    if (positiveButtonClickListener != null) {
                        positiveButtonClickListener.onClick(dialog,
                                2);
                    }
                    break;
                case R.id.tv_close:
                    if (positiveButtonClickListener != null) {
                        positiveButtonClickListener.onClick(dialog,
                                3);
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
