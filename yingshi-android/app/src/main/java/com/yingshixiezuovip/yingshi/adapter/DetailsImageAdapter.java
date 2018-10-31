package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.custom.CustomImageView;
import com.yingshixiezuovip.yingshi.model.ListDetailsModel;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayer;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayerStandard;
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

public class DetailsImageAdapter extends MBaseAdapter<ListDetailsModel.DetailsBaseItem> implements ImageLoadingListener {
    private List<Bitmap> mBitmaps;

    public DetailsImageAdapter(Context mContext, List<ListDetailsModel.DetailsBaseItem> mData) {
        super(mContext, mData);
        mBitmaps = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(int position, ViewHolder viewHolder) {
        ListDetailsModel.DetailsBaseItem baseItem = getItem(position);
        JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) viewHolder.findViewById(R.id.videoplayer);
        CustomImageView customImageView = (CustomImageView) viewHolder.findViewById(R.id.customImageView);
        JCVideoPlayer.releaseAllVideos();
        if (baseItem instanceof ListDetailsModel.DetailsImageItem) {
            jcVideoPlayerStandard.setVisibility(View.GONE);
            customImageView.setVisibility(View.VISIBLE);
            ListDetailsModel.DetailsImageItem imageItem = (ListDetailsModel.DetailsImageItem) baseItem;
            customImageView.setSize(imageItem.width, imageItem.height);
            PictureManager.displayImage(imageItem.photo, customImageView, this);
        } else if (baseItem instanceof ListDetailsModel.DetailsVideoItem) {
            jcVideoPlayerStandard.setVisibility(View.VISIBLE);
            customImageView.setVisibility(View.GONE);
            ListDetailsModel.DetailsVideoItem videoItem = (ListDetailsModel.DetailsVideoItem) baseItem;
            jcVideoPlayerStandard.setUp(videoItem.url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            PictureManager.displayImage(videoItem.photo, jcVideoPlayerStandard.thumbImageView);
            viewHolder.getCovertView().setFocusable(false);
        }
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.view_videoview_layout;
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