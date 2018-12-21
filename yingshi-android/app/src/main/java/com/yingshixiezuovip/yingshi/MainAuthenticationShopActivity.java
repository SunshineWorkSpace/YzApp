package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yingshixiezuovip.yingshi.adapter.ConsumerPriceAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.AlOssImgModel;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.custom.SexSelectWindow;
import com.yingshixiezuovip.yingshi.datautils.CommonThreadPool;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ConsumerPriceModel;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.model.VideoModel;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.SaveImg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类名称:MainAuthenticationShopActivity
 * 类描述:企业认证
 * 创建时间: 2018-11-13-22:00
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class MainAuthenticationShopActivity extends BaseActivity implements PictureManager.OnPictureCallbackListener, ConsumerPriceAdapter.OnItemClickListener {
    private SexSelectWindow sexSelectWindow;
    private SelectWindow mBirthSelectWindow;
    private SelectWindow mCitySelectWindow;
    private PlaceModel mPlaceModel;
    private PictureManager mPictureManager;
    private int mImageId;
    private String mPhotoStr1, mPhotoStr2, mPhotoStr3;
    private boolean isDay = true;
    private SelectWindow mSchoolSelectWindow;
    private AlertWindow mResumeWindow;

    private int mDataId;

    private RecyclerView rv_list;
    private ConsumerPriceAdapter marketDetailNewsAdapter;
    private List<ConsumerPriceModel.ConsumerPriceDetailModel> mList = new ArrayList<>();
    private AlOssImgModel alOssImgModel;
    OSS oss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_rz);
        setActivityTitle("商铺会员认证");

        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
        ((TextView) findViewById(R.id.right_btn_name)).setTextColor(ContextCompat.getColor(this,R.color.colorBlue));

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        initView();
        initOss();
    }
    private void initOss() {
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";

// 在移动端建议使用STS方式初始化OSSClient。
// 更多信息可查看sample 中 sts 使用方式(https://github.com/aliyun/aliyun-oss-android-sdk/tree/master/app/src/main/java/com/alibaba/sdk/android/oss/app)
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(Configs.accessKeyId, Configs.SecretKey);

//该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(100 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(100 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(10); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();

        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }

    private void initView() {
        findViewById(R.id.school_tv_sex).setOnClickListener(this);
        findViewById(R.id.school_tv_city).setOnClickListener(this);
        findViewById(R.id.school_tv_birth).setOnClickListener(this);
//        findViewById(R.id.school_tv_starttime).setOnClickListener(this);
//        findViewById(R.id.school_tv_endtime).setOnClickListener(this);
        findViewById(R.id.school_iv_photo_1).setOnClickListener(this);
        findViewById(R.id.school_iv_photo_2).setOnClickListener(this);
        findViewById(R.id.school_iv_photo_3).setOnClickListener(this);
        findViewById(R.id.school_btn_day).setOnClickListener(this);
        findViewById(R.id.school_btn_project).setOnClickListener(this);
        findViewById(R.id.school_btn_submit).setOnClickListener(this);

        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(linearLayoutManager);
        marketDetailNewsAdapter = new ConsumerPriceAdapter(this, mList);
        rv_list.setAdapter(marketDetailNewsAdapter);
        marketDetailNewsAdapter.setOnItemClickListener(this);
        initWindow();

        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);
    }

    private void initWindow() {
        mPictureManager = new PictureManager(this, this);

        sexSelectWindow = new SexSelectWindow(this);
        sexSelectWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sex_btn_man) {
                    ((TextView) findViewById(R.id.school_tv_sex)).setText("男");
                } else {
                    ((TextView) findViewById(R.id.school_tv_sex)).setText("女");
                }

                sexSelectWindow.cancel();
            }
        });

        mBirthSelectWindow = new SelectWindow(this, 1);
        mBirthSelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.school_tv_birth)).setText(selectContent);
            }
        });

        mSchoolSelectWindow = new SelectWindow(this, 4);
        mSchoolSelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                if (mDataId == 1) {
                    ((TextView) findViewById(R.id.school_tv_starttime)).setText(selectContent);
                } else {
                    ((TextView) findViewById(R.id.school_tv_endtime)).setText(selectContent);
                }
            }
        });


        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.school_tv_city)).setText(selectContent);
            }
        });

        mResumeWindow = new AlertWindow(this,false);
        mResumeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.alert_btn_cancel){
                    onBackPressed();
                } else {
                    startActivity(new Intent(MainAuthenticationShopActivity.this, MainCompanyResumeActivity.class));
                    finish();
                }
            }
        });
    }

    private String getValue(TextView textView) {
        try {
            return (textView.getText().toString() + "").trim();
        } catch (Exception e) {
            return null;
        }
    }

    private void doSubmit() {
        final String name = getValue((TextView) findViewById(R.id.school_tv_name));
        if (TextUtils.isEmpty(name)) {
            showMessage("请输入你的真实姓名");
            return;
        }
        final String sex = getValue((TextView) findViewById(R.id.school_tv_sex));
        if (TextUtils.isEmpty(sex)) {
            showMessage("请选择你的性别");
            return;
        }
        final String schoolName = getValue((TextView) findViewById(R.id.school_tv_school));
        if (TextUtils.isEmpty(schoolName)) {
            showMessage("请输入你的院校名称");
            return;
        }
        final String position = getValue((TextView) findViewById(R.id.school_tv_position));
        if (TextUtils.isEmpty(position)) {
            showMessage("请输入你的专业方向");
            return;
        }
        final String city = getValue((TextView) findViewById(R.id.school_tv_city));
        if (TextUtils.isEmpty(city)) {
            showMessage("请选择你的当前城市");
            return;
        }
        final String birth = getValue((TextView) findViewById(R.id.school_tv_birth));
        if (TextUtils.isEmpty(birth)) {
            showMessage("请输入你的生日");
            return;
        }
        final String startTime = getValue((TextView) findViewById(R.id.school_tv_starttime));
        if (TextUtils.isEmpty(startTime)) {
            showMessage("请输入你的店铺类型");
            return;
        }
        final String endTime = getValue((TextView) findViewById(R.id.school_tv_endtime));
        if (TextUtils.isEmpty(endTime)) {
            showMessage("请输入你的联系电话");
            return;
        }
        if (TextUtils.isEmpty(mPhotoStr1)) {
            showMessage("请上传的你的身份证正面照片");
            return;
        }
        if (TextUtils.isEmpty(mPhotoStr2)) {
            showMessage("请上传的你的身份证反面照片");
            return;
        }
        if (TextUtils.isEmpty(mPhotoStr3)) {
            showMessage("请上传你的学生证照片");
            return;
        }
        final String price = getValue((TextView) findViewById(R.id.school_et_price));
        if (TextUtils.isEmpty(price)) {
            showMessage("请输入你的薪资");
            return;
        }
        boolean isGoNext=false;
        String vip="";
        String xbjprice="";
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).isClick){
                isGoNext=true;
                vip=mList.get(i).vip;
                xbjprice=mList.get(i).price;
            }
        }
        if(isGoNext){
            Intent it=new Intent(this,ShopAuthorPayActivity.class);
            it.putExtra("vip",vip);
            it.putExtra("xbjprice",xbjprice);
            it.putExtra("truename", name);
            it.putExtra("sex", sex);
            it.putExtra("school", schoolName);
            it.putExtra("major", position);
            it.putExtra("city", city);
            it.putExtra("birth", birth);
            it.putExtra("shoptype", startTime);
            it.putExtra("constansphone", endTime);
            it.putExtra("identity_card", alOssImgModel.data.get(0).createDir);
            it.putExtra("side_card", alOssImgModel.data.get(1).createDir);
            it.putExtra("license", alOssImgModel.data.get(2).createDir);
            it.putExtra("price", price);
            it.putExtra("unit", isDay ? "天" : "项目");
            startActivity(it);
        }else {
            showMessage("请选择消保金");
        }
       /* CommonThreadPool.getThreadPool().addCachedTask(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("token", mUserInfo.token);
                    params.put("truename", name);
                    params.put("sex", sex);
                    params.put("school", schoolName);
                    params.put("major", position);
                    params.put("city", city);
                    params.put("birth", birth);
                    params.put("shoptype", startTime);
                    params.put("constansphone", endTime);
                    params.put("identity_card", PictureManager.getBase64(mPhotoStr1));
                    params.put("side_card", PictureManager.getBase64(mPhotoStr2));
                    params.put("license", PictureManager.getBase64(mPhotoStr3));
                    params.put("price", price);
                    params.put("unit", isDay ? "天" : "项目");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.show(R.string.text_request);
                        }
                    });
                    HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_UPDATA, params, MainAuthenticationShopActivity.this);
                } catch (Exception e) {
                    showMessage("图片信息编码失败，请重新选择");
                }
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_tv_sex:
                sexSelectWindow.show();
                break;
            case R.id.school_tv_city:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");
                break;
            case R.id.school_tv_birth:
                mBirthSelectWindow.show();
                break;
            case R.id.school_tv_starttime:
                mDataId = 1;
                mSchoolSelectWindow.show();
                break;
            case R.id.school_tv_endtime:
                mDataId = 2;
                mSchoolSelectWindow.show();
                break;
            case R.id.school_iv_photo_1:
                mImageId = 1;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
            case R.id.school_iv_photo_2:
                mImageId = 2;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
            case R.id.school_iv_photo_3:
                mImageId = 3;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
            case R.id.school_btn_day:
                onClickCheckBox(1);
                break;
            case R.id.school_btn_project:
                onClickCheckBox(2);
                break;
            case R.id.right_btn_submit:
                doSubmit();
                break;
            default:
                break;
        }
    }

    private void onClickCheckBox(int index) {
        ((CheckBox) findViewById(R.id.school_cb_day)).setChecked(index == 1);
        ((CheckBox) findViewById(R.id.school_cb_project)).setChecked(index != 1);
        isDay = index == 1;
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_CITY_INFO:
                try {
                    mPlaceModel = GsonUtil.fromJson(result.toString(), PlaceModel.class);
                } catch (Exception e) {
                    mPlaceModel = null;
                    showMessage("城市数据获取失败，请稍后重试");
                }
                loadList();
                break;
            case TASK_TYPE_STUDENT_AUTH:
                mUserInfo.type = 5;
                mUserInfo.iswanshan = 1;
                mUserInfo.isrenzhen = 1;
                mUserInfo.isbzj = 1;
                SPUtils.saveUserInfo(mUserInfo);
                showMessage("提交成功，请耐心等待审核");
                mResumeWindow.show("","是否继续编辑简历？","以后填写","继续填写");
                break;
            case TASK_TYPE_SHOP_VIP:
                ConsumerPriceModel alOssVideoModel = GsonUtil.fromJson(result.toString(), ConsumerPriceModel.class);
                if (alOssVideoModel != null) {
                    mList = alOssVideoModel.data;
                    marketDetailNewsAdapter.refreshItem(mList);
                } else {
                    showMessage(R.string.data_load_failed);
                }
                sendPicDate();
                break;
            case TASK_TYPE_OSS_IMG:
                alOssImgModel = GsonUtil.fromJson(result.toString(), AlOssImgModel.class);
                if (alOssImgModel != null) {

                } else {
                    showMessage(R.string.data_load_failed);
                }

                break;
            default:
                break;

        }
    }

    private void loadList() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_VIP, params, this);
    }

    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        switch (mImageId) {
            case 1:
                mPhotoStr1 = uri.getPath();
                ((ImageView) findViewById(R.id.school_iv_photo_1)).setImageURI(uri);
                Glide.with(this).load(mPhotoStr1).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            ((ImageView)findViewById(R.id.iv_test)).setImageBitmap(resource);
//                            sendVideoImgOss(alOssVideoImgModelOne.data.get(0).createDir,Bitmap2Bytes(resource), "video");
                        sendVideoImgOssNew(alOssImgModel.data.get(0).createDir,SaveImg.saveImg(resource,"iccardfront.png",MainAuthenticationShopActivity.this).toString());
                    }
                });
                break;
            case 2:
                mPhotoStr2 = uri.getPath();
                ((ImageView) findViewById(R.id.school_iv_photo_2)).setImageURI(uri);
                Glide.with(this).load(mPhotoStr2).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            ((ImageView)findViewById(R.id.iv_test)).setImageBitmap(resource);
//                            sendVideoImgOss(alOssVideoImgModelOne.data.get(0).createDir,Bitmap2Bytes(resource), "video");
                        sendVideoImgOssNew(alOssImgModel.data.get(1).createDir,SaveImg.saveImg(resource,"iccardback.png",MainAuthenticationShopActivity.this).toString());
                    }
                });
                break;
            case 3:
                mPhotoStr3 = uri.getPath();
                ((ImageView) findViewById(R.id.school_iv_photo_3)).setImageURI(uri);
                Glide.with(this).load(mPhotoStr3).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            ((ImageView)findViewById(R.id.iv_test)).setImageBitmap(resource);
//                            sendVideoImgOss(alOssVideoImgModelOne.data.get(0).createDir,Bitmap2Bytes(resource), "video");
                        sendVideoImgOssNew(alOssImgModel.data.get(2).createDir,SaveImg.saveImg(resource,"shopcard.png",MainAuthenticationShopActivity.this).toString());
                    }
                });
                break;
        }
    }

    @Override
    public void onPictureUpload(int status, String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPictureManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(View view, int position) {
        for(int i=0;i<mList.size();i++){
            ConsumerPriceModel.ConsumerPriceDetailModel model=new ConsumerPriceModel.ConsumerPriceDetailModel();
            if(i!=position){
                model.price=mList.get(i).price;
                model.vip=mList.get(i).vip;
                model.isClick=false;
            }else {
                model.price=mList.get(i).price;
                model.vip=mList.get(i).vip;
                model.isClick=true;
            }
            mList.set(i,model);
            marketDetailNewsAdapter.refreshItem(mList);
        }
    }
    private void sendPicDate() {
        HashMap<String, Object> params = new HashMap<>();
            params.put("count", 3);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_OSS_IMG, params, this);
    }

    private void sendVideoImgOssNew(String serverUrl, String path) {

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
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    showMessage("上传图片失败");
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    showMessage("上传图片失败");
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
}
