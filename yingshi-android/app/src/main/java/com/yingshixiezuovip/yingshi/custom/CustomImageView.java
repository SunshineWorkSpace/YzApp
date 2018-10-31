package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yingshixiezuovip.yingshi.utils.CommUtils;


/**
 * Created by Resmic on 2016/7/15.
 */

public class CustomImageView extends ImageView {
    private int mWidth, mHeight;

    public CustomImageView(Context context, int width, int height) {
        super(context);
        this.mWidth = width;
        this.mHeight = height;
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context mContext) {
        super(mContext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int width = CommUtils.getScreenWidth(getContext()) - CommUtils.dip2px(8 * 2);//高度根据使得图片的宽度充满屏幕计算而得
            int height = (int) Math.ceil((float) width * (mHeight * 1.0 / mWidth));
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        requestLayout();
    }

}
