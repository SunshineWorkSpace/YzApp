package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.utils.CommUtils;


/**
 * Created by Resmic on 2017/1/4.
 * Email:xiangyx@wenwen-tech.com
 */

public class NavigationView extends LinearLayout {
    private int countPosition = 4;  //总个数
    private int mNoSelectSize = 8; //未选中大小
    private int mSelectedSize = 8; //已选中大小
    private int mCurrentPosition = 0; //当前选中的id
    private int margin = 10;
    private int mSelectedResid = R.drawable.nav_shape_select;
    private int mNoSelectResid = R.drawable.nav_shape_noselect;

    public NavigationView(Context context) {
        super(context);
        initView();
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setSelection(int position) {
        this.mCurrentPosition = position % countPosition;
        initView();
    }

    public void setSize(int selectSize, int noSelectSize) {
        this.mSelectedSize = selectSize;
        this.mNoSelectSize = noSelectSize;
        initView();
    }

    public void setCountPosition(int countNum) {
        if (countNum > 1) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
        this.countPosition = countNum;
        initView();
    }

    public void setMargin(int margin) {
        this.margin = margin;
        initView();
    }

    public void setResid(int selectResid, int noSelectResid) {
        this.mSelectedResid = selectResid;
        this.mNoSelectResid = noSelectResid;
        initView();
    }

    private void initView() {
        removeAllViews();
        setOrientation(LinearLayout.HORIZONTAL);
        NavigationItemView itemView;
        for (int i = 0; i < countPosition; i++) {
            itemView = new NavigationItemView(getContext(), mSelectedSize > mNoSelectSize ? mSelectedSize : mNoSelectSize, mSelectedSize > mNoSelectSize ? mSelectedSize : mNoSelectSize);
            if (i != 0) {
                itemView.setMargins(margin, 0, 0, 0);
            }
            itemView.setSizeOrResid(i == mCurrentPosition ? mSelectedSize : mNoSelectSize, i == mCurrentPosition ? mSelectedResid : mNoSelectResid);
            addView(itemView);
        }


    }

    private class NavigationItemView extends FrameLayout {
        private int width;
        private int height;

        private View icon;

        public NavigationItemView(Context context, int width, int height) {
            super(context);
            this.width = width;
            this.height = height;
            initView();
        }

        public void setSizeOrResid(int size, int resid) {
            LayoutParams params = new LayoutParams(CommUtils.dip2px(getContext(), size), CommUtils.dip2px(getContext(), size));
            params.gravity = Gravity.CENTER;
            icon.setLayoutParams(params);
            icon.setBackgroundResource(resid);
        }

        private void initView() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommUtils.dip2px(getContext(), width), CommUtils.dip2px(getContext(), height));
            setLayoutParams(params);
            icon = new View(getContext());
            params.gravity = Gravity.CENTER;
            addView(icon);
        }

        public void setMargins(int left, int top, int right, int bottom) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
            params.setMargins(CommUtils.dip2px(getContext(), left), CommUtils.dip2px(getContext(), top), CommUtils.dip2px(getContext(), right), CommUtils.dip2px(getContext(), bottom));
            setLayoutParams(params);
        }

        public View getIcon() {
            return icon;
        }

    }
}
