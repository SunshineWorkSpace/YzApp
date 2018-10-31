package com.yingshixiezuovip.yingshi.base;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.CrashHandler;
import com.yingshixiezuovip.yingshi.model.BaseEaseUser;
import com.yingshixiezuovip.yingshi.model.VersionModel;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayer;
import com.yingshixiezuovip.yingshi.utils.ActivityManager;
import com.yingshixiezuovip.yingshi.utils.FileUtils;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.List;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class YingApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static YingApplication instance;
    public DisplayImageOptions mHeadOption, mImageOption;
    private VersionModel versionModel;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        JPushInterface.setDebugMode(Configs.isDebug);
        JPushInterface.init(this);

        CrashHandler.getInstance().init(this);
        initImageLoader();
        initUMengPlatform();

        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setRequireAck(true);
        options.setAutoLogin(false);
        options.setRequireDeliveryAck(false);
        boolean isSuc = EaseUI.getInstance().init(this, options);
        if(isSuc) {
            EMClient.getInstance().chatManager().addMessageListener(listener);
        }
        JCVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JCVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        registerActivityLifecycleCallbacks(this);
    }


    private EMMessageListener listener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            dealMessage(list);
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            dealMessage(list);
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
        }

        private void dealMessage(List<EMMessage> list) {
            EMMessage emMessage = list.get(0);
            BaseEaseUser easeUser = SPUtils.getBaseEaseUser(instance, emMessage.getFrom());
            if (easeUser != null) {
                emMessage.setFrom(easeUser.nickname);
            } else {
                emMessage.setFrom("你收到一条消息，点击查看");
            }
            EaseUI.getInstance().getNotifier().onNewMsg(emMessage);

        }
    };

    public VersionModel getVersionModel() {
        return versionModel;
    }

    public void setVersionModel(VersionModel versionModel) {
        this.versionModel = versionModel;
    }

    private void initUMengPlatform() {
        PlatformConfig.setSinaWeibo(Configs.SINA_APPKEY, Configs.SINA_SECRET, Configs.SINA_CALLBACK);
        PlatformConfig.setWeixin(Configs.WECHAT_APPID, Configs.WECHAT_SECRET);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);
    }

    private void initImageLoader() {
        mHeadOption = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new SimpleBitmapDisplayer())
                .handler(new Handler(getMainLooper()))
                .build();
        mImageOption = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(false)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .handler(new Handler(getMainLooper()))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCache(new UnlimitedDiskCache(FileUtils.getCachePath()))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static YingApplication getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ActivityManager.getInstance().addActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ActivityManager.getInstance().removeActivity(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
