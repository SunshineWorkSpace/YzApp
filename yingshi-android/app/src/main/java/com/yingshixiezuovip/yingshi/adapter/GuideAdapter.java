package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yingshixiezuovip.yingshi.R;

/**
 * Created by Resmic on 2017/5/26.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class GuideAdapter extends PagerAdapter implements View.OnClickListener {
    private int[] mImageIds;
    private Context mContext;

    public GuideAdapter(Context context, int[] imageIds) {
        this.mContext = context;
        this.mImageIds = imageIds;
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.view_guide_item_layout, null);
        if (position == getCount() - 1) {
            view.findViewById(R.id.guide_btn_start).setVisibility(View.VISIBLE);
            view.findViewById(R.id.guide_btn_start).setOnClickListener(this);
        } else {
            view.findViewById(R.id.guide_btn_start).setVisibility(View.GONE);
        }
        ((ImageView) view.findViewById(R.id.guide_iv_icon)).setImageResource(mImageIds[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
