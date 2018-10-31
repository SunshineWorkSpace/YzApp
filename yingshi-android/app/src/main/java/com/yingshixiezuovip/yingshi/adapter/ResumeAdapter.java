package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.custom.CustomImageView;
import com.yingshixiezuovip.yingshi.model.ResumeModel;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayerStandard;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.util.List;

/**
 * Created by Resmic on 2017/9/14.
 */

public class ResumeAdapter extends MBaseAdapter<ResumeModel> {

    public ResumeAdapter(Context mContext, List<ResumeModel> mData, OnImageClickListener onImageClickListener) {
        super(mContext, mData);
        this.onImageClickListener = onImageClickListener;
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        ResumeModel resumeModel = getItem(position);
        ((TextView) viewHolder.findViewById(R.id.resume_tv_desc)).setText(resumeModel.zwcontent);
        viewHolder.findViewById(R.id.resume_tv_desc).setVisibility(TextUtils.isEmpty(resumeModel.zwcontent) ? View.GONE : View.VISIBLE);
        viewHolder.findViewById(R.id.videoplayer_layout).setVisibility(View.GONE);
        viewHolder.findViewById(R.id.customImageView).setVisibility(View.GONE);
        ImageView imageView;
        if (resumeModel.type == 1) {
            imageView = (CustomImageView) viewHolder.findViewById(R.id.customImageView);
            imageView.setVisibility(View.VISIBLE);
            ((CustomImageView) imageView).setSize(resumeModel.width, resumeModel.height);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageClickListener != null) {
                        onImageClickListener.onImageClick(position);
                    }
                }
            });
        } else {
            viewHolder.findViewById(R.id.videoplayer_layout).setVisibility(View.VISIBLE);
            JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) viewHolder.findViewById(R.id.videoplayer);
            imageView = jcVideoPlayerStandard.thumbImageView;
            jcVideoPlayerStandard.setVisibility(View.VISIBLE);
            jcVideoPlayerStandard.setUp(resumeModel.zwfile, JCVideoPlayerStandard.SCREEN_LAYOUT_LIST, "");
            ((TextView) viewHolder.findViewById(R.id.video_tv_counttime)).setText(resumeModel.zwtime);
        }
        PictureManager.displayImage(resumeModel.zwphoto, imageView);

    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.view_resume_item_layout;
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    private OnImageClickListener onImageClickListener;
}
