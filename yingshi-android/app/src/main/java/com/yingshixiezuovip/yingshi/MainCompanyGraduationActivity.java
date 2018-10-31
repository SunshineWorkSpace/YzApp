package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.datautils.CommonThreadPool;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.util.HashMap;

public class MainCompanyGraduationActivity extends BaseActivity implements PictureManager.OnPictureCallbackListener {
    private Uri mImageURI;
    private PictureManager mPictureManager;
    private AlertWindow mAlertWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company_graduation, R.string.title_activity_main_company_graduation);

        initView();
    }

    private void initView() {
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
        mPictureManager = new PictureManager(this, this);
        findViewById(R.id.graduation_iv_image).setOnClickListener(this);

        mAlertWindow = new AlertWindow(this, false);
        mAlertWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    doUploadPicture();
                }
            }
        });
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn_submit:
                if (mImageURI == null) {
                    showMessage("请先选择证书");
                    return;
                }
                mAlertWindow.show(" ", "确认提交？不可修改", "重新选择", "确定");
                break;
            case R.id.graduation_iv_image:
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
        }
    }

    private void doUploadPicture() {
        CommonThreadPool.getThreadPool().addCachedTask(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.show();
                        }
                    });
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("token", mUserInfo.token);
                    hashMap.put("photo", PictureManager.getBase64(mImageURI.getPath()));
                    HttpUtils.doPost(TaskType.TASK_TYPE_UPLOAD_BYZS, hashMap, MainCompanyGraduationActivity.this);
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.cancel();
                            showMessage("图片编码失败，请重新选择");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        showMessage("上传成功");
        onBackPressed();
    }

    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        mImageURI = uri;
        PictureManager.displayImage(uri.getPath(), (ImageView) findViewById(R.id.graduation_iv_image));
    }

    @Override
    public void onPictureUpload(int status, String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPictureManager.onActivityResult(requestCode, resultCode, data);
    }
}
