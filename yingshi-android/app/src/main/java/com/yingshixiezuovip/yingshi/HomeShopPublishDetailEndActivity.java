package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.AlOssImgModel;
import com.yingshixiezuovip.yingshi.custom.AlOssVideoModel;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.model.VideoModel;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.media.MediaOptions;
import com.yingshixiezuovip.yingshi.quote.media.activities.MediaPickerActivity;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 类名称:HomeShopPublishDetailEndActivity
 * 类描述:第三步
 * 创建时间: 2018-11-06-16:24
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class HomeShopPublishDetailEndActivity extends BaseActivity {
    private ImageView iv_video;
    public static final int REQUEST_MEDIA = 0x1003;
    private List<MediaItem> mMediaSelectedList;

    ArrayList<String> mImgs = new ArrayList<String>();
    private AlOssImgModel alOssImgModel;
    private int videoTime = 0;
    private AlOssVideoModel alOssVideoModel;
    OSS oss;
    private String videoFile = "";
    private byte[] videoImgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_detail_end);
        setActivityTitle("发布页(3/3)");

        ((TextView) findViewById(R.id.right_btn_name)).setText("下一步");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        iv_video = (ImageView) findViewById(R.id.iv_video);
        iv_video.setOnClickListener(this);
        getIntentDate();
        initOss();
    }

    private void getIntentDate() {

        mImgs = getIntent().getStringArrayListExtra("infoList");
    }

    private void initOss() {
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";

// 在移动端建议使用STS方式初始化OSSClient。
// 更多信息可查看sample 中 sts 使用方式(https://github.com/aliyun/aliyun-oss-android-sdk/tree/master/app/src/main/java/com/alibaba/sdk/android/oss/app)
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(Configs.accessKeyId, Configs.SecretKey);

//该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

//开启可以在控制台看到日志，并且会支持写入手机sd卡中的一份日志文件位置在SDCard_path\OSSLog\logs.csv  默认不开启
//日志会记录oss操作行为中的请求数据，返回数据，异常信息
//例如requestId,response header等
//android_version：5.1  android版本
//mobile_model：XT1085  android手机型号
//network_state：connected  网络状况
//network_type：WIFI 网络连接类型
//具体的操作行为信息:
//[2017-09-05 16:54:52] - Encounter local execpiton: //java.lang.IllegalArgumentException: The bucket name is invalid.
//A bucket name must:
//1) be comprised of lower-case characters, numbers or dash(-);
//2) start with lower case or numbers;
//3) be between 3-63 characters long.
//------>end of log
        OSSLog.enableLog();

        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.right_btn_submit:
                sendPicDate();
                break;
            case R.id.iv_video:
                MediaPickerActivity.open(this, REQUEST_MEDIA, new MediaOptions.Builder().selectVideo().build());
                break;
        }
    }

    private void sendPicDate() {
        HashMap<String, Object> params = new HashMap<>();
        if (videoTime != 0) {
            if(mImgs.size()==9){
                if(!mImgs.get(mImgs.size()-1).equals("添加")){
                    params.put("count", mImgs.size()+1);
                }else {
                    params.put("count", mImgs.size());
                }

            }else {
                params.put("count", mImgs.size());
            }

        }else {
            if(mImgs.size()==9){
                if(!mImgs.get(mImgs.size()-1).equals("添加")){
                    params.put("count", mImgs.size());
                }else {
                    params.put("count", mImgs.size()-1);
                }

            }else {
                params.put("count", mImgs.size()-1);
            }

        }
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_OSS_IMG, params, this);
    }

    private void sendVideoDate() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("count", 1);
        HttpUtils.doPost(TaskType.TASK_TYPE_OSS_VIDEO, params, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_MEDIA) {
            mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
            if (mMediaSelectedList != null) {
                for (MediaItem mediaItem : mMediaSelectedList) {
//                    PictureManager.displayImage(mediaItem.getUriOrigin()+"",iv_video);
                    iv_video.setBackground(null);
                    Matrix matrix = new Matrix();
                    matrix.setScale(1f, 1f);
                    Bitmap bitmap = Bitmap.createBitmap(getVideoThumbnail(getPath(mediaItem.getUriOrigin().toString()) + ""), 0, 0, 1000, 1000, matrix, false);
                    iv_video.setImageBitmap(bitmap);
                    videoFile = getPath(mediaItem.getUriOrigin().toString()) + "";
                    videoImgFile=Bitmap2Bytes(bitmap);
//                    mImgs.add(videoFile);
                    videoTime = CommUtils.getDuration(getPath(mediaItem.getUriOrigin().toString()) + "");
                }
            } else {
            }
        }
    }
    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public File getPath(String uris) {
        Uri uri = Uri.parse(uris);


        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = this.managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();


        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);
        return file;
//        Uri fileUri = Uri.fromFile(file);
    }

    // 获取视频缩略图
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap b = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    //负责所有的界面更新

    //OSS的上传下载
    private String picturePath = "";

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String FILE_DIR = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "oss/";
    private static final String FILE_PATH = FILE_DIR + "wangwang.zip";

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            if(type==TaskType.TASK_TYPE_ASIDE_SEND){
                mLoadWindow.cancel();
            }
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_OSS_IMG:
                alOssImgModel = GsonUtil.fromJson(result.toString(), AlOssImgModel.class);
                if (alOssImgModel != null) {
                    if (videoTime != 0) {
                        sendVideoDate();
                        for (int i = 0; i < mImgs.size(); i++) {
                            if(!mImgs.get(i).equals("添加")){
                                sendPicOss(i, alOssImgModel.data.get(i).createDir, mImgs.get(i));
                            }

                        }
                        sendPicOssByte();
                    }else {
                        for (int i = 0; i < mImgs.size(); i++) {
                            if(!mImgs.get(i).equals("添加")){
                                sendPicOss(i, alOssImgModel.data.get(i).createDir, mImgs.get(i));
                            }
                        }
                    }

                } else {
                    showMessage(R.string.data_load_failed);
                }

                break;
            case TASK_TYPE_OSS_VIDEO:
                alOssVideoModel = GsonUtil.fromJson(result.toString(), AlOssVideoModel.class);
                if (alOssVideoModel != null) {
                    sendPicOss(100, alOssVideoModel.data.get(0).createDir,videoFile);
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
            case TASK_TYPE_ASIDE_SEND:
                mLoadWindow.cancel();
                Intent it=new Intent(this,MainActivity.class);
                startActivity(it);
                break;
        }
    }

    private void sendPicOssByte() {
        new Random().nextBytes(videoImgFile);
        int i=0;
        // 构造上传请求
        if(mImgs.size()==9){
            if(!mImgs.get(mImgs.size()-1).equals("添加")){
          i= mImgs.size();
            }else {
             i =mImgs.size()-1;
            }

        }else {
            i =mImgs.size()-1;
        }

        PutObjectRequest put = new PutObjectRequest(Configs.bucket, alOssImgModel.data.get(i).toString(), videoImgFile);

// 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    private void sendPicOss(final int i, String serverUrl, String path) {
// 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Configs.bucket, serverUrl, path);

// 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                if (videoTime != 0&&i==100) {
                    sendPublish();
                }
                if(mImgs.size()==9){
                    if(mImgs.get(mImgs.size()-1).equals("添加")){
                        if (i == mImgs.size()-2&&videoTime==0) {
                            sendPublish();
                        }
                    }else {
                        if (i == mImgs.size()-1&&videoTime==0) {
                            sendPublish();
                        }
                    }

                }else {
                    if (i == mImgs.size()-2&&videoTime==0) {
                        sendPublish();
                    }
                }

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

// task.cancel(); // 可以取消任务
// task.waitUntilFinished(); // 可以等待任务完成
    }

    private void sendPublish() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> params = new HashMap<>();
                params.put("token", mUserInfo.token);
                params.put("price", getIntent().getStringExtra("singleprice"));
                params.put("title", getIntent().getStringExtra("title"));
                params.put("num", getIntent().getStringExtra("num"));
                params.put("isnew", getIntent().getStringExtra("new"));
                params.put("typename", getIntent().getStringExtra("type"));
                params.put("constans", getIntent().getStringExtra("phone"));
                params.put("city", getIntent().getStringExtra("area"));
                params.put("address", getIntent().getStringExtra("areadetail"));
                params.put("content", getIntent().getStringExtra("content"));
                if(videoTime!=0){
                    params.put("video", alOssVideoModel.data.get(0).createDir);
                    if(mImgs.size()==9){
                        if(!mImgs.get(mImgs.size()-1).equals("添加")){
                            params.put("videofm", alOssImgModel.data.get(mImgs.size()).createDir);
                        }else {
                            params.put("videofm", alOssImgModel.data.get(mImgs.size()-1).createDir);
                        }

                    }else {
                        params.put("videofm", alOssImgModel.data.get(mImgs.size()-1).createDir);
                    }
//                    params.put("videofm", alOssImgModel.data.get(mImgs.size()-1).createDir);
                    params.put("videotimer", videoTime+"");

                    if(mImgs.size()==9){
                        if(!mImgs.get(mImgs.size()-1).equals("添加")){
                            alOssImgModel.data.remove(mImgs.size());
                        }else {
                            alOssImgModel.data.remove(mImgs.size()-1);
                        }

                    }else {
                        alOssImgModel.data.remove(mImgs.size()-1);
                    }
//                    alOssImgModel.data.remove(mImgs.size()-1);
                }else {
                    params.put("video", "");
                    params.put("videofm", "");
                    params.put("videotimer", "");
                }
                List<HashMap<String, Object>> mediaParams = new ArrayList<>();
                if (alOssImgModel.data != null && alOssImgModel.data.size() > 0) {
                    HashMap<String, Object> mediaParam;
                    VideoModel mVideoModel;

                    for (AlOssImgModel.AlOssImgDetailModel mediaIten : alOssImgModel.data) {
                        mediaParam = new HashMap<>();
                        mediaParam.put("photo",mediaIten.createDir);
                        mediaParams.add(mediaParam);
                    }
                }
                params.put("list",mediaParams);
                HttpUtils.doPost(TaskType.TASK_TYPE_ASIDE_SEND, params, HomeShopPublishDetailEndActivity.this,true);
            }  }).start();

    }
}
