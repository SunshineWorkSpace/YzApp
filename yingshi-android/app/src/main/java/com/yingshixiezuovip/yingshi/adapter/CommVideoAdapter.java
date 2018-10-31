package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.custom.VideoModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayerStandard;
import com.yingshixiezuovip.yingshi.utils.PictureManager;


/**
 * Created by Resmic on 2017/5/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CommVideoAdapter extends MBaseAdapter<VideoModel.VideoItem> {
    public CommVideoAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        final VideoModel.VideoItem videoItem = getItem(position);
        PictureManager.displayHead(videoItem.head, (RoundedImageView) viewHolder.findViewById(R.id.video_iv_head));
        ((TextView) viewHolder.findViewById(R.id.video_tv_author)).setText(videoItem.nickname);
        ((TextView) viewHolder.findViewById(R.id.video_tv_invite)).setText("邀请人：" + (TextUtils.isEmpty(videoItem.invite) ? "未定" : videoItem.invite));
        JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) viewHolder.findViewById(R.id.video_videoplayer);
        jcVideoPlayerStandard.setUp(videoItem.url, JCVideoPlayerStandard.SCREEN_LAYOUT_LIST, "");
        PictureManager.displayImage(videoItem.photo, jcVideoPlayerStandard.thumbImageView);
        ((TextView) viewHolder.findViewById(R.id.video_tv_counttime)).setText(videoItem.time);
        ((TextView) viewHolder.findViewById(R.id.video_tv_playcount)).setText("点击数：" + videoItem.playCount);
        ((TextView) viewHolder.findViewById(R.id.video_tv_content)).setText(videoItem.title);
        viewHolder.findViewById(R.id.video_tv_content).setVisibility(TextUtils.isEmpty(videoItem.title) ? View.GONE : View.VISIBLE);
        viewHolder.findViewById(R.id.video_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onHeadClick(videoItem.userid, videoItem.nickname);
                }
            }
        });
        viewHolder.getCovertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.comm_video_item_layout;
    }
}
