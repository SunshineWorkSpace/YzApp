package com.yingshixiezuovip.yingshi.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.ResumeAdapter;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.custom.PhotoViewWindow;
import com.yingshixiezuovip.yingshi.minterface.WebInterface;
import com.yingshixiezuovip.yingshi.model.ListDetailsModel;
import com.yingshixiezuovip.yingshi.model.ResumeModel;
import com.yingshixiezuovip.yingshi.model.UserWorks;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.WebUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 2017/9/14.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CoverInfoFragment extends BaseFragment {
    private UserWorks mUserWorks;
    private PhotoViewWindow mPhotoViewWindow;
    private List<ListDetailsModel.DetailsImageItem> mImageItems;
    private WebView mWebView;

    public static CoverInfoFragment getInstance(UserWorks userWorks) {
        CoverInfoFragment infoFragment = new CoverInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user_works", userWorks);
        infoFragment.setArguments(bundle);
        return infoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.fragment_cover_info_layout, null);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        mUserWorks = (UserWorks) getArguments().getSerializable("user_works");
        initView();
    }

    private void initView() {
        mWebView = (WebView) getView().findViewById(R.id.coverinfo_webview);
        WebUtils.initWebView(mWebView);
        mWebView.addJavascriptInterface(new WebInterface(getActivity()), "WebInterface");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                String js = loadJS();
                L.d("loadJs ==== " + js);
                mWebView.loadUrl("javascript:" + js);
            }
        });
        mWebView.loadUrl(mUserWorks.resume_url);
    }

    private String loadJS() {
        try {
            InputStream inStream = getActivity().getAssets().open("js.txt");
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, len);
            }
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }
}
