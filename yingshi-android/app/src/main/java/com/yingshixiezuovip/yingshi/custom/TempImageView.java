package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.utils.L;


/**
 * Created by Resmic on 2017/5/25.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class TempImageView extends ImageView {
    public TempImageView(Context context) {
        super(context);
    }

    public TempImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            L.d("TempImageView::onDraw => " + ThrowableUtils.getThrowableDetailsMessage(e));
        }
    }
}
