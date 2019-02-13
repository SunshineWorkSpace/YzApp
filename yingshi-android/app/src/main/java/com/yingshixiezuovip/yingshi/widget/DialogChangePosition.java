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
 * 类名称:DialogChangePosition
 * 类描述:
 * 创建时间: 2019-01-22-18:26
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class DialogChangePosition extends Dialog {


    public DialogChangePosition(@NonNull Context context) {
        super(context);
    }

    public DialogChangePosition(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogChangePosition(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements View.OnClickListener {
        private Context context;
        private TextView tv_clean, tv_yes,tv_change;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        DialogChangePosition dialog;
        public Builder(Context context) {
            this.context = context;
        }

        public DialogChangePosition.Builder setPositiveButton(DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public DialogChangePosition create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            dialog = new DialogChangePosition(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.item_change_position, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 500));
            tv_clean = (TextView) layout.findViewById(R.id.tv_clean);
            tv_yes = (TextView) layout.findViewById(R.id.tv_yes);
            tv_change=(TextView)layout.findViewById(R.id.tv_change);
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
