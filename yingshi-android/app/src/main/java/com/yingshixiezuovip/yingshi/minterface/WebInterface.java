package com.yingshixiezuovip.yingshi.minterface;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.yingshixiezuovip.yingshi.custom.PhotoSingleWindow;
import com.yingshixiezuovip.yingshi.utils.L;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class WebInterface {
    private Activity activity;
    private PhotoSingleWindow mPhotoSingleWindow;


    public WebInterface(Activity activity) {
        this.activity = activity;
        mPhotoSingleWindow = new PhotoSingleWindow(activity);
    }

    @JavascriptInterface
    public void onImageClick(final String imageUrl) {
        L.d("onImageClick ==== " + imageUrl);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhotoSingleWindow.show(imageUrl);
            }
        });
    }
}
