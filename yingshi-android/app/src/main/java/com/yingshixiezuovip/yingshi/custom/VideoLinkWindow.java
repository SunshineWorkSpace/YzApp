package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;

/**
 * Created by Resmic on 18/1/22.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class VideoLinkWindow extends BasePopupWindow {
    public VideoLinkWindow(Context mContext) {
        super(mContext, false, false);
        setWidthHeight(270, 252);

        findViewById(R.id.video_btn_cancel).setOnClickListener(this);
        findViewById(R.id.video_btn_submit).setOnClickListener(this);
    }

    @Override
    public View createView() {
        return LayoutInflater.from(mContext).inflate(R.layout.window_video_link_layout, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.video_btn_submit) {
            String link = (((EditText) findViewById(R.id.video_et_input)).getText().toString() + "").trim();

            if (!TextUtils.isEmpty(link) && (link.startsWith("http://") || link.startsWith("https://"))) {
                if (onVideoLinkFinishCallback != null) {
                    onVideoLinkFinishCallback.onVideoLink(link);
                }
            } else {
                showMessage("请输入正确的视频地址");
                return;
            }
        }

        cancel();
    }

    public void show(String mediaPath) {
        ((EditText) findViewById(R.id.video_et_input)).setText(mediaPath);
        super.show();
    }

    public interface OnVideoLinkFinishCallback {
        void onVideoLink(String linkStr);
    }

    private OnVideoLinkFinishCallback onVideoLinkFinishCallback;

    public void setOnVideoLinkFinishCallback(OnVideoLinkFinishCallback onVideoLinkFinishCallback) {
        this.onVideoLinkFinishCallback = onVideoLinkFinishCallback;
    }
}
