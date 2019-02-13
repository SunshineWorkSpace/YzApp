package com.yingshixiezuovip.yingshi;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yingshixiezuovip.yingshi.base.BaseActivity;

/**
 * 类名称:PayWebActivity
 * 类描述:
 * 创建时间: 2019-01-18-14:23
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class PayWebActivity extends BaseActivity {
    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_webview);
        wv=(WebView) findViewById(R.id.wv);
        webviewAdd();
    }
    private void webviewAdd() {
        WebSettings webSettings = wv.getSettings();
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
/*// 设置可以支持缩放
        wv_coin.getSettings().setSupportZoom(true);

// 设置出现缩放工具
        wv_coin.getSettings().setBuiltInZoomControls(true);
//扩大比例的缩放
        webSettings.setUseWideViewPort(true);
//自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        wv_coin.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wv_coin.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            wv_coin.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wv_coin.setWebChromeClient(new WebChromeClient());
        wv_coin.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        wv_coin.getSettings().setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        wv_coin.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);*/
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                // super.onReceivedSslError(view, handler, error);

                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler.proceed();
//                super.onReceivedSslError(view, handler, error);
            }
        });

        wv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {

                ((WebView) v).requestDisallowInterceptTouchEvent(true);


                return false;
            }
        });
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

            }
        });
        wv.loadUrl(getIntent().getExtras().getString("url"));
    }

}
