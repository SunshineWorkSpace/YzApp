package com.yingshixiezuovip.yingshi.datautils.download;

import android.os.AsyncTask;

import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.utils.FileUtils;
import com.yingshixiezuovip.yingshi.utils.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Resmic on 2017/3/6.
 * 下载任务基类
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    private DownloadListener listener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;
    private String mDownloadURL;
    private File mFinalFile;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
        listener.onProgress(0);
    }

    /**
     * 这个方法中的所有代码都会在子线程中运行，我们应该在这里处理所有的耗时任务。
     *
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        long downloadLength = 0;   //记录已经下载的文件长度
        mDownloadURL = params[0];

        mFinalFile = FileUtils.getImageSaveCache(mDownloadURL);


        L.d("DownloadTask Task downloadUrl = " + mDownloadURL + " , localPath = " + mFinalFile.getPath());

        if (mFinalFile.exists()) {
            downloadLength = mFinalFile.length();
        }

        long contentLength = getContentLength(mDownloadURL);

        if (contentLength == 0) {
            return TYPE_FAILED;
        } else if (contentLength == downloadLength) {
            return TYPE_SUCCESS;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + downloadLength + "-")  //断点续传要用到的，指示下载的区间
                .url(mDownloadURL)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(mFinalFile, "rw");
                savedFile.seek(downloadLength);//跳过已经下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;

                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }

                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            L.d("DownloadTask Task failed = " + ThrowableUtils.getThrowableDetailsMessage(e));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (savedFile != null) {
                    savedFile.close();
                }

                if (isCanceled && mFinalFile != null) {
                    mFinalFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }


    /**
     * @param values
     */
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];

        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    /**
     * 当后台任务执行完毕并通过Return语句进行返回时，这个方法就很快被调用。返回的数据会作为参数
     * 传递到此方法中，可以利用返回的数据来进行一些UI操作。
     *
     * @param status
     */
    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                File tempFile = FileUtils.tempRealCache(mFinalFile.getPath());
                mFinalFile.delete();
                L.d("DownloadTask Task downloadUrl success = " + tempFile.getPath());
                listener.onSuccess(tempFile.getPath());
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    /**
     * 得到下载内容的大小
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();

        try {
            Response response = client.newCall(request).execute();

            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
