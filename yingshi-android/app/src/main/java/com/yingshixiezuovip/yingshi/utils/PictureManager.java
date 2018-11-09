package com.yingshixiezuovip.yingshi.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.PhotoWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskListener;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.media.MediaOptions;
import com.yingshixiezuovip.yingshi.quote.media.activities.MediaPickerActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 2016/10/14.
 */

public class PictureManager {
    public static final int PM_REQUESTCODE_MEDIA = 0x2000;
    public static final int PM_REQUESTCODE_CAMERA = 0x2001;
    public static final int PM_REQUESTCODE_CROP = 0x2002;
    private PhotoWindow mPhotoWindow;
    private BaseActivity mActivity;
    private Uri mImageUri;
    private int mWindowType;
    public final static int WINDOW_TYPE_HEAD = 1, WINDOW_TYPE_COMMON = 2, WINDOW_TYPE_BACKGROUND = 3;

    public PictureManager(BaseActivity activity, OnPictureCallbackListener callbackListener) {
        this.mActivity = activity;
        this.callbackListener = callbackListener;
        initPhotoWindow();
    }

    private void initPhotoWindow() {
        mPhotoWindow = new PhotoWindow(mActivity);
        mPhotoWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()) {
                    case R.id.dialog_item1:
                        if (!CommUtils.cameraIsCanUse()) {
                            Toast.makeText(mActivity, R.string.camera_tips, Toast.LENGTH_SHORT).show();
                        } else {
                            File file = new File(FileUtils.getTakePhotoCachePath(), System.currentTimeMillis() + ".jpg");
                            mImageUri = Uri.fromFile(file);
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                            mActivity.startActivityForResult(intent, PM_REQUESTCODE_CAMERA);
                        }
                        break;
                    case R.id.dialog_item2:
                        MediaPickerActivity.open(mActivity, PM_REQUESTCODE_MEDIA, new MediaOptions.Builder().selectPhoto().build());
                        break;
                    case R.id.dialog_item3:
                        break;
                }
                mPhotoWindow.cancel();
            }
        });
    }

    public void showWindow(int windowType) {
        mWindowType = windowType;
        mPhotoWindow.show();
    }


    public void cropPictureByUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        uri = uri.getPath().startsWith("file://") ? uri : Uri.parse("file://" + uri.getPath());
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        if (mWindowType == WINDOW_TYPE_HEAD) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 180);
            intent.putExtra("outputY", 180);
        } else {
            intent.putExtra("aspectX", 16);
            intent.putExtra("aspectY", 9);
        }
        mImageUri = Uri.parse("file://" + FileUtils.getCachePath() + "/" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        mActivity.startMActivityForResult(intent, PM_REQUESTCODE_CROP);
    }


    /**
     * @param token
     * @param uri
     * @param type  1为头像，2为背景图片,3为自定义图片
     */
    public void doUploadPicture(String token, Uri uri, int type, TaskListener listener, int pid) {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("token", token);
            if (type == 1) {
                params.put("head", getBase64(uri.getPath()));
                HttpUtils.doPost(TaskType.TASK_TYPE_MINE_HEAD_UPLOAD, params, listener);
            } else if (type == 2) {
                params.put("background", getBase64(uri.getPath()));
                HttpUtils.doPost(TaskType.TASK_TYPE_MINE_BACKGROUND_UPLOAD, params, listener);
            } else if (type == 3) {
                params.put("photo", getBase64(uri.getPath()));
                params.put("pid", pid);
                HttpUtils.doPost(TaskType.TASK_TYPE_MINE_PHOTO, params, listener);
            }
        } catch (IOException e) {
            L.d("图片解析失败：" + ThrowableUtils.getThrowableDetailsMessage(e));
            if (callbackListener != null) {
                callbackListener.onPictureUpload(-1, "图片解析失败，上传失败");
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d("PictureManagerTAG", "onActivityResult");
        if (resultCode != Activity.RESULT_OK) { // 此处的 RESULT_OK 是系统自定义得一个常量
            return;
        }
        if (requestCode == PictureManager.PM_REQUESTCODE_MEDIA) {
            List<MediaItem> mMediaSelectedList = MediaPickerActivity
                    .getMediaItemSelected(data);
            if (mWindowType == WINDOW_TYPE_COMMON) {
                onResult(Uri.parse(mMediaSelectedList.get(0).getPathOrigin(mActivity)), data);
            } else {
                cropPictureByUri(Uri.parse(mMediaSelectedList.get(0).getPathOrigin(mActivity)));
            }
        } else if (requestCode == PictureManager.PM_REQUESTCODE_CAMERA) {
            if (mWindowType == WINDOW_TYPE_COMMON) {
                onResult(mImageUri, data);
            } else {
                cropPictureByUri(mImageUri);
            }
        } else if (requestCode == PictureManager.PM_REQUESTCODE_CROP) {
            onResult(mImageUri, data);
        }
    }

    private void onResult(Uri uri, Intent data) {
        L.d("PictureManager:onResult = " + uri.toString());
        if (callbackListener != null) {
            callbackListener.onPictureCallback(uri, data);
        }
    }

    public Uri getImageUri() {
        return mImageUri;
    }


    public interface OnPictureCallbackListener {
        void onPictureCallback(Uri uri, Intent data);

        void onPictureUpload(int status, String message);
    }

    private OnPictureCallbackListener callbackListener;

    public static void displayHead(String headUrl, ImageView imageView) {
        if (TextUtils.isEmpty(headUrl) || imageView == null)
            return;
        if (!headUrl.startsWith("http://") && !headUrl.startsWith("https://") && !headUrl.startsWith("file://") && !headUrl.startsWith("drawable://")) {
            headUrl = "file://" + headUrl;
        }

        if (headUrl.equals(imageView.getTag())) {
            return;
        }
        imageView.setTag(headUrl);
        ImageLoader.getInstance().displayImage(headUrl, imageView, YingApplication.getInstance().mHeadOption);
    }

    public static void displayHead(String headUrl, ImageView imageView, ImageLoadingListener listener) {
        if (TextUtils.isEmpty(headUrl) || imageView == null)
            return;
        if (!headUrl.startsWith("http://") && !headUrl.startsWith("https://") && !headUrl.startsWith("file://") && !headUrl.startsWith("drawable://")) {
            headUrl = "file://" + headUrl;
        }
        if (headUrl.equals(imageView.getTag())) {
            return;
        }
        imageView.setTag(headUrl);
        ImageLoader.getInstance().displayImage(headUrl, imageView, YingApplication.getInstance().mHeadOption, listener);
    }

    public static void displayImage(String imageUrl, ImageView imageView) {
        if (TextUtils.isEmpty(imageUrl))
            return;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://") && !imageUrl.startsWith("file://") && !imageUrl.startsWith("drawable://")) {
            imageUrl = "file://" + imageUrl;
        }

        if (!imageUrl.equals(imageView.getTag())) {
            imageView.setTag(imageUrl);
            ImageLoader.getInstance().displayImage(imageUrl, imageView, YingApplication.getInstance().mImageOption);
        }
    }

    public static void displayImage(String imageUrl, ImageView imageView, ImageLoadingListener listener) {
        if (TextUtils.isEmpty(imageUrl))
            return;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://") && !imageUrl.startsWith("file://") && !imageUrl.startsWith("drawable://")) {
            imageUrl = "file://" + imageUrl;
        }
        ImageLoader.getInstance().displayImage(imageUrl, imageView, YingApplication.getInstance().mImageOption, listener);
    }

//    public static String getBase64(String path) throws IOException {
//        L.d("getBase64::" + path);
//        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(in, null, options);
//        in.close();
//        File file = new File(path);
//        FileInputStream inputFile = new FileInputStream(file);
//        byte[] buffer = new byte[(int) file.length()];
//        inputFile.read(buffer);
//        inputFile.close();
//        return Base64.encodeToString(buffer, Base64.DEFAULT);
//    }

    public static String getBase64(String path) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        double oldSize = bitmap.getByteCount() * 1.0 / 1024;
        bitmap = zoomImg(bitmap, bitmap.getWidth() * 0.7, bitmap.getHeight() * 0.7);
        L.d("PicTAG", "compressImage oldSize = " + oldSize + " , nowSize = " + (bitmap.getByteCount() * 1.0 / 1024));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        baos.flush();
        baos.close();
        bitmap.recycle();
        byte[] byteArray = baos.toByteArray();
        // 转换为字符串
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public static byte[] getToBase64(String path) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        double oldSize = bitmap.getByteCount() * 1.0 / 1024;
        bitmap = zoomImg(bitmap, bitmap.getWidth() * 0.7, bitmap.getHeight() * 0.7);
        L.d("PicTAG", "compressImage oldSize = " + oldSize + " , nowSize = " + (bitmap.getByteCount() * 1.0 / 1024));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        baos.flush();
        baos.close();
        bitmap.recycle();
        byte[] byteArray = baos.toByteArray();
        // 转换为字符串
        return byteArray;
    }
    public static String getFileBase64(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1000];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        byte[] buffer = bos.toByteArray();
        // 转换为字符串
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }
    public static byte[] getFileToBase64(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1000];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        byte[] buffer = bos.toByteArray();
        // 转换为字符串
        return buffer;
    }
    public static Bitmap zoomImg(Bitmap bm, double newWidth, double newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressScale(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = 512f;
        float ww = 512f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        return bitmap;
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        int oldSize = baos.toByteArray().length;
//        while (baos.toByteArray().length > oldSize * 0.8) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
//            baos.reset(); // 重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
//            options -= 10;// 每次都减少10
//        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inPreferredConfig = Bitmap.Config.RGB_565;
        options2.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, options2);// 把ByteArrayInputStream数据生成图片
        L.d("PicTAG", "compressImage oldSize = " + (oldSize * 1.0 / 1024) + " , nowSize = " + (bitmap.getByteCount() * 1.0 / 1024));
        return bitmap;
    }

    public static String getVideoThumbnail(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        Bitmap bitmap = retriever.getFrameAtTime();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    public static byte[] getVideoThumbnailBitm(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        Bitmap bitmap = retriever.getFrameAtTime();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    public static Bitmap getVideoThumbnailBitmap(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        Bitmap bitmap = retriever.getFrameAtTime();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return bitmap;
    }
}
