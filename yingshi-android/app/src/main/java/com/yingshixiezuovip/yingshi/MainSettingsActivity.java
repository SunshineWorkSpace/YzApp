package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.FileUtils;
import com.yingshixiezuovip.yingshi.utils.LoginUtils;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.io.File;

/**
 * * Created by Resmic on 2017/5/4.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class MainSettingsActivity extends BaseActivity {
    private File mCacheFile;
    private AlertWindow mCacheWindow;
    private AlertWindow mCallWindow;
    private ShareWindow mShareWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings, R.string.title_activity_main_settings);

        initView();
        initWindow();
    }

    private void initView() {
        findViewById(R.id.settings_controller_layout).setVisibility(mUserInfo.iswanshan == 1 ? View.VISIBLE : View.GONE);
        findViewById(R.id.settings_btn_price).setVisibility(mUserInfo.type == 2 ? View.VISIBLE : View.GONE);
        findViewById(R.id.settings_btn_price_line).setVisibility(mUserInfo.type == 2 ? View.VISIBLE : View.GONE);

        findViewById(R.id.settings_btn_agreement).setOnClickListener(this);
        findViewById(R.id.settings_btn_share).setOnClickListener(this);
        findViewById(R.id.settings_btn_clear).setOnClickListener(this);
        findViewById(R.id.settings_btn_update).setOnClickListener(this);
        findViewById(R.id.settings_btn_invite).setOnClickListener(this);
        findViewById(R.id.settings_btn_price).setOnClickListener(this);
        findViewById(R.id.settings_btn_service).setOnClickListener(this);

        findViewById(R.id.settings_btn_loginout).setOnClickListener(this);
        PictureManager.displayHead(mUserInfo.head, (RoundedImageView) findViewById(R.id.settings_iv_head));
        ((TextView) findViewById(R.id.settings_tv_name)).setText(mUserInfo.nickname + "");

        initCache();
    }

    private void initWindow() {
        mAlertWindow = new AlertWindow(this, false);
        mAlertWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    SPUtils.saveUserInfo(null);
                    LoginUtils.doLogout();
                    Intent intent = new Intent(MainSettingsActivity.this, StartupLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    YingApplication.getInstance().startActivity(intent);
                }
            }
        });
        mCacheWindow = new AlertWindow(this, true);
        mCacheWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    clearCache();
                }
            }
        });
        mCallWindow = new AlertWindow(this, false);
        mCallWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    if (v.getId() == R.id.alert_btn_submit) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Configs.SERVICE_PHONE));
                        startActivity(intent);
                    }
                }
            }
        });
        mShareWindow = new ShareWindow(this);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_REFRESH_USER_PRICE:
                finish();
                break;
        }
    }

    @Override
    protected void onSingleClick(View v) {
        Intent intent = null;
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.settings_btn_agreement:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.CURL, Configs.AgreementURL);
                break;
            case R.id.settings_btn_share:
                if (mUserInfo.type == 1) {
                    showMessage("普通用户无法邀请，谢谢");
                    return;
                }
                ShareModel.ShareItem shareItem = new ShareModel.ShareItem();
                shareItem.title = mUserInfo.share_title;
                shareItem.photo = mUserInfo.share_photo;
                shareItem.url = mUserInfo.share_url;
                mShareWindow.show(shareItem, this);
                break;
            case R.id.settings_btn_clear:
                mCacheWindow.show("缓存清理", "有" + ((TextView) findViewById(R.id.settings_tv_cacheSize)).getText().toString() + "缓存，确定清除吗？", "取消", "确定");
                break;
            case R.id.settings_btn_update:
                intent = new Intent(this, UserInfoSettingsActivity.class);
                intent.putExtra("user_type", UserInfoSettingsActivity.TYPE_INFO);
                break;
            case R.id.settings_btn_invite:
                break;
            case R.id.settings_btn_price:
                break;
            case R.id.settings_btn_service:
                mCallWindow.show("", "客服电话：+86 " + Configs.SERVICE_PHONE, "取消", "立即拨打");
                break;
            case R.id.settings_btn_loginout:
                mAlertWindow.show("", "确定退出登录？", "取消", "确定");
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void initCache() {
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                long fileSize = (long) msg.obj;
                float fSize = fileSize * 1.0f / 1024 / 1024;
                ((TextView) findViewById(R.id.settings_tv_cacheSize)).setText(String.format("%.2fMB", fSize));
            }
        };
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                long tempSize = 0;
                Message message = Message.obtain();
                if (Configs.CacheDirectory != null && Configs.CacheDirectory.length > 0) {
                    for (String tempFileName : Configs.CacheDirectory) {
                        mCacheFile = FileUtils.getPath(tempFileName);
                        tempSize += getTotalSizeOfFilesInDir(mCacheFile);
                    }
                }
                message.obj = tempSize;
                mHandler.sendMessage(message);
            }
        };
        mHandler.post(mRunnable);
    }

    public void clearCache() {
        ((TextView) findViewById(R.id.settings_tv_cacheSize)).setText("正在清理...");
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                initCache();
                showMessage("清理完成");
            }
        };
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (Configs.CacheDirectory != null && Configs.CacheDirectory.length > 0) {
                    for (String tempFileName : Configs.CacheDirectory) {
                        mCacheFile = FileUtils.getPath(tempFileName);
                        clearFiles(mCacheFile);
                    }
                }
                mHandler.sendEmptyMessage(10);
            }
        };
        mHandler.post(mRunnable);
    }

    private void clearFiles(File file) {
        if (file.exists()) {
            deleteFile(file);
        }
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }


    private long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
}
