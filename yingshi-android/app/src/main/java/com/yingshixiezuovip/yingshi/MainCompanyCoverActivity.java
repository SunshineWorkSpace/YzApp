package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.adapter.SysCoverAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.CoverSystemWindow;
import com.yingshixiezuovip.yingshi.custom.CoverWindow;
import com.yingshixiezuovip.yingshi.datautils.CommonThreadPool;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.CompanyStatusModel;
import com.yingshixiezuovip.yingshi.model.CoverModel;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.media.MediaOptions;
import com.yingshixiezuovip.yingshi.quote.media.activities.MediaPickerActivity;
import com.yingshixiezuovip.yingshi.utils.FileUtils;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainCompanyCoverActivity extends BaseActivity {
    public static final int PM_REQUESTCODE_MEDIA = 0x2000;
    public static final int PM_REQUESTCODE_CROP = 0x2002;
    private Uri mImageUri;

    private CoverWindow mCoverWindow;
    private CoverSystemWindow mCoverSystemWindow;
    private CompanyStatusModel mCompanyStatusModel;
    //    private BlurringView mBlurringView;
    private CoverModel.CoverItem mCoverItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company_cover, R.string.activity_main_company_cover_title);

        mCompanyStatusModel = (CompanyStatusModel) getIntent().getSerializableExtra("company_status");

        initView();
        initWindow();
    }

    private void initView() {
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("修改");
//        mBlurringView = (BlurringView) findViewById(R.id.cover_blurringview);
        initCover();
    }

    private void initCover() {
        setCoverPath(mCompanyStatusModel.samplereels_fm);
        PictureManager.displayHead(mCompanyStatusModel.head, (ImageView) findViewById(R.id.cover_iv_head));
        ((TextView) findViewById(R.id.cover_tv_name)).setText(mCompanyStatusModel.nickname);
        ((TextView) findViewById(R.id.cover_tv_position)).setText(mCompanyStatusModel.position + " / " + mCompanyStatusModel.city);
    }

    private void setCoverPath(String path) {
        PictureManager.displayImage(path, (ImageView) findViewById(R.id.cover_iv_image));
    }


    private void initWindow() {
        mCoverWindow = new CoverWindow(this);
        mCoverWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoverWindow.cancel();
                if (v.getId() == R.id.dialog_item1) {
                    MediaPickerActivity.open(MainCompanyCoverActivity.this, PM_REQUESTCODE_MEDIA, new MediaOptions.Builder().selectPhoto().build());
                } else if (v.getId() == R.id.dialog_item2) {
                    mCoverSystemWindow.show();
                }
            }
        });
        mCoverSystemWindow = new CoverSystemWindow(this, mUserInfo.token);
        mCoverSystemWindow.setOnCoverSelectedListener(new SysCoverAdapter.OnCoverSelectedListener() {
            @Override
            public void onCoverSelected(CoverModel.CoverItem coverItem) {
                mCoverItem = coverItem;
                setCoverPath(mCoverItem.photononame);
            }
        });
        mCoverSystemWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoverSystemWindow.cancel();
                if (v.getId() == R.id.cover_submit && mCoverWindow != null) {
                    uploadBackground(mCoverItem.photononame, 2);
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
        setCoverPath(((JSONObject) result).optJSONObject("data").optString("samplereels_fm"));
    }

    private void uploadBackground(final String picPath, final int type) {
        CommonThreadPool.getThreadPool().addCachedTask(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadWindow.show(R.string.text_request);
                    }
                });
                try {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("token", mUserInfo.token);
                    hashMap.put("type", type);
                    if (type == 1) {
                        hashMap.put("photo", PictureManager.getBase64(picPath));
                    } else {
                        hashMap.put("photo", mCoverItem.uploadPhoto);
                    }
                    HttpUtils.doPost(TaskType.TASK_TYPE_UPLOAD_COVER, hashMap, MainCompanyCoverActivity.this);
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.cancel();
                            showMessage("图片解析失败，请重试");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn_submit:
                mCoverItem = null;
                mCoverWindow.show();
                break;
        }
    }

    public void cropPictureByUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        uri = uri.getPath().startsWith("file://") ? uri : Uri.parse("file://" + uri.getPath());
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 9);
        intent.putExtra("aspectY", 16);
        mImageUri = Uri.parse("file://" + FileUtils.getCachePath() + "/" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startMActivityForResult(intent, PM_REQUESTCODE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == PictureManager.PM_REQUESTCODE_MEDIA) {
            List<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
            cropPictureByUri(Uri.parse(mMediaSelectedList.get(0).getPathOrigin(this)));
        } else if (requestCode == PictureManager.PM_REQUESTCODE_CROP) {
            mCompanyStatusModel.samplereels_fm_isSys = 1;
            setCoverPath(mImageUri.getPath());
            uploadBackground(mImageUri.getPath(), 1);
        }
    }
}
