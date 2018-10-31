package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayer;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayerStandard;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.PictureManager;


public class MainPublishPreviewActivity extends BaseActivity {
    private PublishModel mPublishModel;
    private LinearLayout mDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_publish_preview, R.string.title_activity_main_publish_preview);

        mPublishModel = (PublishModel) getIntent().getSerializableExtra("publish_model_data");
        if (mPublishModel == null) {
            showMessage("数据加载失败，请重试");
            return;
        }

        mDataLayout = (LinearLayout) findViewById(R.id.priview_media_layout);
        initData();
    }

    private void initData() {
        ((TextView) findViewById(R.id.preview_tv_position)).setText(mPublishModel.position);
        PictureManager.displayImage(mPublishModel.cover, (ImageView) findViewById(R.id.preview_iv_cover));
        ((TextView) findViewById(R.id.preview_tv_title)).setText(mPublishModel.title);
        ((TextView) findViewById(R.id.preview_tv_content)).setText(mPublishModel.content);

        if (mPublishModel.medias != null && mPublishModel.medias.size() > 0) {
            View videoViewLayout;
            JCVideoPlayerStandard videoPlayer;
            ImageView imageView;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, CommUtils.dip2px(8));
            for (PublishModel.PublishMediaItem mediaIten : mPublishModel.medias) {
                videoViewLayout = View.inflate(this, R.layout.view_videoview_layout, null);
                videoPlayer = (JCVideoPlayerStandard) videoViewLayout.findViewById(R.id.videoplayer);
                imageView = (ImageView) videoViewLayout.findViewById(R.id.imageView);
                if (mediaIten.type == PublishModel.TYPE_PICTYRE) {
                    videoPlayer.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    PictureManager.displayImage(mediaIten.mediaPath, imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                    videoPlayer.setVisibility(View.VISIBLE);
                    videoPlayer.setUp(mediaIten.mediaPath, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                    PictureManager.displayImage(mediaIten.mediaPath, videoPlayer.thumbImageView);
                }
                if (TextUtils.isEmpty(mediaIten.desc)) {
                    videoViewLayout.findViewById(R.id.textview).setVisibility(View.GONE);
                } else {
                    videoViewLayout.findViewById(R.id.textview).setVisibility(View.VISIBLE);
                    ((TextView) videoViewLayout.findViewById(R.id.textview)).setText(mediaIten.desc);
                }
                videoViewLayout.setLayoutParams(layoutParams);
                videoViewLayout.setFocusable(false);
                mDataLayout.addView(videoViewLayout);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
