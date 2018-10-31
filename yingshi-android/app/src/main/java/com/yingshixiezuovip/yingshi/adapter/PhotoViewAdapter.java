package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.custom.CustomImageView;
import com.yingshixiezuovip.yingshi.model.ListDetailsModel;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 2017/5/26.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PhotoViewAdapter extends PagerAdapter implements ImageLoadingListener {
    private List<ListDetailsModel.DetailsImageItem> imageItems;
    private Context mContext;
    private View.OnClickListener onClickListener;
    private List<Bitmap> mBitmaps;

    public PhotoViewAdapter(Context context, List<ListDetailsModel.DetailsImageItem> imageItems, View.OnClickListener onClickListener) {
        this.mContext = context;
        this.imageItems = imageItems;
        this.onClickListener = onClickListener;
        mBitmaps = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FrameLayout layout = (FrameLayout) View.inflate(mContext, R.layout.view_photoview_item_layout, null);
        ListDetailsModel.DetailsImageItem imageItem = imageItems.get(position);
        CustomImageView customImageView = new CustomImageView(mContext, imageItem.width, imageItem.height);
        customImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        PictureManager.displayImage(imageItem.photo, customImageView, this);
        ((FrameLayout) layout.findViewById(R.id.mainFrameLayout)).addView(customImageView, layoutParams);
        container.addView(layout);
        customImageView.setOnClickListener(onClickListener);
        layout.setOnClickListener(onClickListener);
        customImageView.setOnClickListener(onClickListener);
        return layout;
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
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        mBitmaps.add(loadedImage);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
    }

    public void onDestroy() {
        for (Bitmap bitmap : mBitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }
}
