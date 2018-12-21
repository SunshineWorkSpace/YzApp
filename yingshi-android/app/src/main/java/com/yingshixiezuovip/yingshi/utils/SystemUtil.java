package com.yingshixiezuovip.yingshi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zd on 16/5/18.
 */
public class SystemUtil {


    public static int dpToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static DisplayImageOptions getImageLoaderDisplayImageOptions() {
        return getImageLoaderDisplayImageOptions(-1, -1, -1);
    }

    public static DisplayImageOptions getImageLoaderDisplayImageOptions(int lodingResId) {
        return getImageLoaderDisplayImageOptions(lodingResId, -1, -1);
    }

    public static DisplayImageOptions getImageLoaderDisplayImageOptions(int lodingResId, int emptyResId) {
        return getImageLoaderDisplayImageOptions(lodingResId, emptyResId, -1);
    }

    public static DisplayImageOptions getImageLoaderDisplayImageOptions(int lodingResId, int emptyResId, int failResId, int cornerRadiusPixels) {
        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
        if (lodingResId != -1)
            options.showImageOnLoading(lodingResId);
        if (emptyResId != -1)
            options.showImageForEmptyUri(emptyResId);

        if (failResId != -1)
            options.showImageOnFail(failResId);

        options.cacheInMemory(true);
        options.cacheOnDisk(true);
        options.imageScaleType(ImageScaleType.NONE);
        options.bitmapConfig(Bitmap.Config.RGB_565);//设置为RGB565比起默认的ARGB_8888要节省大量的内存
        options.delayBeforeLoading(100);//载入图片前稍做延时可以提高整体滑动的流畅度
        if (cornerRadiusPixels > 0)
            options.displayer(new RoundedBitmapDisplayer(cornerRadiusPixels));//圆角图片
        return options.build();
    }

    public static DisplayImageOptions getImageLoaderDisplayImageOptions(int lodingResId, int emptyResId, int failResId) {
        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
        if (lodingResId != -1)
            options.showImageOnLoading(lodingResId);
        if (emptyResId != -1)
            options.showImageForEmptyUri(emptyResId);

        if (failResId != -1)
            options.showImageOnFail(failResId);

        options.cacheInMemory(true);
        options.cacheOnDisk(true);
        options.imageScaleType(ImageScaleType.NONE);
        options.bitmapConfig(Bitmap.Config.RGB_565);//设置为RGB565比起默认的ARGB_8888要节省大量的内存
        options.delayBeforeLoading(100);//载入图片前稍做延时可以提高整体滑动的流畅度
        return options.build();
    }

    public static int getImageWidth(Context context,String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return px2dip(context,options.outWidth==0?400:options.outWidth);
    }
    public static int getImageHeight(Context context,String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return px2dip(context,options.outHeight==0?300:options.outHeight);
    }
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
