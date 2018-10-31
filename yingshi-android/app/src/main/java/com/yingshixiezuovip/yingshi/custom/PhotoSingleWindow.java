package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.datautils.download.DownloadListener;
import com.yingshixiezuovip.yingshi.datautils.download.DownloadTask;
import com.yingshixiezuovip.yingshi.quote.photoview.PhotoView;
import com.yingshixiezuovip.yingshi.utils.FileUtils;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PhotoSingleWindow extends BasePopupWindow {
    private AlertWindow downWindow;
    private String mImageUrl;
    private DownloadTask mDownloadTask;

    public PhotoSingleWindow(Context mContext) {
        super(mContext, false, true);

        downWindow = new AlertWindow(mContext, false);
        downWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    if (FileUtils.hasImageExist(mImageUrl)) {
                        showMessage("该图片已经存在");
                    } else {
                        mDownloadTask = new DownloadTask(downloadListener);
                        mDownloadTask.execute(mImageUrl);
                    }

                }
            }
        });

        setWidthHeight(true, -1, -1);

        getView().findViewById(R.id.photo_iv_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        getView().findViewById(R.id.photo_iv_image).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                downWindow.show("", "确定保存图片？", "取消", "保存");
                return false;
            }
        });
    }

    public void show(String imagePath) {
        this.mImageUrl = imagePath;
        PictureManager.displayImage(imagePath, (PhotoView) findViewById(R.id.photo_iv_image));
        super.show();
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.window_photo_single_layout, null);
    }

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onSuccess(String filePath) {
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
            showMessage("保存成功");
        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onFailed() {
            showMessage("图片保存失败");
        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onCanceled() {

        }
    };

}
