package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.recyclerview.base.ItemViewDelegate;
import com.yingshixiezuovip.yingshi.quote.recyclerview.base.ViewHolder;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.io.FileNotFoundException;

/**
 * Created by Resmic on 18/1/22.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class MediaLayout extends FrameLayout {
    private MediaItem mediaItem;

    public MediaLayout(Context context) {
        super(context);
        initView();
    }

    public MediaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_medialayout_layout, this);
    }

    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;

        ((TextView) findViewById(R.id.media_tv_type)).setText(mediaItem.getType() == PublishModel.TYPE_PICTYRE ?
                "图片" : (mediaItem.getType() == PublishModel.TYPE_LINK ? "链接" : "视频"));

        if (mediaItem.getType() == PublishModel.TYPE_LINK) {
            ((ImageView) findViewById(R.id.media_iv_icon)).setImageResource(R.mipmap.publish_icon_video2);
        } else {
            findViewById(R.id.media_iv_icon).setVisibility(VISIBLE);

            if (mediaItem.getType() == MediaItem.VIDEO) {
                if (mediaItem.getListBean() != null) {
                    PictureManager.displayImage(mediaItem.getListBean().getList_videofm(), (ImageView) findViewById(R.id.media_iv_icon));
                } else {
                    PictureManager.displayImage(mediaItem.getPathOrigin(getContext()), (ImageView) findViewById(R.id.media_iv_icon));
                }
            } else {
                findViewById(R.id.media_iv_icon_gif).setVisibility(GONE);
                findViewById(R.id.media_iv_icon).setVisibility(GONE);

                String path = mediaItem.getPathOrigin(getContext());

                if (path.toLowerCase().endsWith(".gif")) {
                    if (path.startsWith("http://") || path.startsWith("https://")) {
                        findViewById(R.id.media_iv_icon).setVisibility(VISIBLE);
                        Glide.with(getContext()).load(path).placeholder(android.R.color.transparent)
                                .into((ImageView) findViewById(R.id.media_iv_icon));
                    } else {
                        try {
                            findViewById(R.id.media_iv_icon_gif).setVisibility(VISIBLE);
                            ((GifView) findViewById(R.id.media_iv_icon_gif)).setMovieResource(path);
                        } catch (FileNotFoundException e) {
                            findViewById(R.id.media_iv_icon_gif).setVisibility(GONE);
                            findViewById(R.id.media_iv_icon).setVisibility(VISIBLE);
                            PictureManager.displayImage(path, (ImageView) findViewById(R.id.media_iv_icon));
                        }
                    }
                } else {
                    findViewById(R.id.media_iv_icon).setVisibility(VISIBLE);
                    PictureManager.displayImage(path, (ImageView) findViewById(R.id.media_iv_icon));
                }
            }
        }
    }

}
