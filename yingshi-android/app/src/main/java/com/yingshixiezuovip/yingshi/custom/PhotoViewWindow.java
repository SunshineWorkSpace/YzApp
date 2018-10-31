package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.PhotoViewAdapter;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.model.ListDetailsModel;

import java.util.List;

/**
 * Created by Resmic on 2017/5/26.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PhotoViewWindow extends BasePopupWindow implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private TextView mPageText;
    private int maxNum;
    private PhotoViewAdapter mPhotoViewAdapter;

    public PhotoViewWindow(Context mContext, List<ListDetailsModel.DetailsImageItem> imageItems) {
        super(mContext, false, true);
        setWidthHeight(true, -1, -1);
        maxNum = imageItems.size();
        mViewPager = (ViewPager) findViewById(R.id.photoview_viewpage);
        mViewPager.addOnPageChangeListener(this);
        mPageText = (TextView) findViewById(R.id.photoview_tv_page);
        mPhotoViewAdapter = new PhotoViewAdapter(mContext, imageItems, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        mViewPager.setAdapter(mPhotoViewAdapter);
        mPageText.setText((mViewPager.getCurrentItem() + 1) + "/" + maxNum);
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_photoview_layout, null);
    }

    public void show(int position) {
        if (position >= mViewPager.getAdapter().getCount()) {
            return;
        }
        mViewPager.setCurrentItem(position);
        super.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPageText.setText((mViewPager.getCurrentItem() + 1) + "/" + maxNum);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void onDestroy() {
        if (mPhotoViewAdapter != null) {
            mPhotoViewAdapter.onDestroy();
        }
    }
}
