package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Resmic on 2017/5/30.
 */

public class MainImageView extends ImageView{
    public MainImageView(Context context) {
        super(context);
    }

    public MainImageView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);//高度根据使得图片的宽度充满屏幕计算而得
            int height = (int) Math.ceil((float) width * (9 * 1.0 / 16));
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
