package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;


import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.L;

import java.util.Map;

/**
 * Created by Resmic on 2017/5/3.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class WebViewActivity extends BaseActivity {
    public final static String CURL = "cust_url";
    private WebView mWebView;
    private String mUrl;
    private String fromSource;
    private String mCurrentUrl;
    private Map<String, String> mUrlParamMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        try {
            mUrl = getIntent().getStringExtra(CURL);
            fromSource = getIntent().getStringExtra("from_source");
        } catch (Exception e) {
            return;
        }

        initWebView();
        if (!TextUtils.isEmpty(mUrl)) {
            if (CommUtils.isNetworkAvailable()) {
                mCurrentUrl = mUrl;
                mWebView.loadUrl(mUrl);
            } else {
                findViewById(R.id.lin_page_error_layout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.webview_tv_errinfo)).setText(R.string.network_error);
            }
        } else {
            findViewById(R.id.lin_page_error_layout).setVisibility(View.VISIBLE);
        }
    }

    private void initWebView() {
        mWebView = ((WebView) findViewById(R.id.webview));
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setSupportZoom(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setLoadWithOverviewMode(true);
        setting.setUseWideViewPort(true);//设定支持viewport
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Toast.makeText(WebViewActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.d("WebViewTAG", "shouldOverrideUrlLoading::" + url);
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    mCurrentUrl = url;
                    view.loadUrl(url);
                    return false;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                L.d("WebViewTAG", "error(：" + errorCode + "," + description + "," + failingUrl + ")");
                findViewById(R.id.lin_page_error_layout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.webview_tv_errinfo)).setText(description);
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                url = url.toLowerCase();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return super.shouldInterceptRequest(view, url);
                } else {
                    return new WebResourceResponse(null, null, null);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setActivityTitle(title);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                onBackPressed();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();

    }
}

