package com.yingshixiezuovip.yingshi.utils;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Resmic on 2017/7/12.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class WebUtils {

    public static void initWebView(WebView webView) {
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        final WebSettings setting = webView.getSettings();
        if (CommUtils.isNetworkAvailable()) {
            setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        setting.setAllowFileAccess(true);
        setting.setJavaScriptEnabled(true);
        setting.setSupportZoom(true);
        setting.setUseWideViewPort(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setLoadWithOverviewMode(true);
        setting.setAllowUniversalAccessFromFileURLs(true);

        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setAllowFileAccess(true);
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        setting.setAppCacheEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.requestFocus();
    }

}
