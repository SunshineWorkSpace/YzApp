package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.custom.SexSelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainAuthenticationActivity extends BaseActivity implements PictureManager.OnPictureCallbackListener {
    private SelectWindow mBirthSelectWindow;
    private SelectWindow mCitySelectWindow;

    private PlaceModel mPlaceModel;
    private PictureManager mPictureManager;
    private int mImageId;
    private String mHeadStr, mPhotoStr1, mPhotoStr2, mPhotoStr3;
    private SexSelectWindow sexSelectWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_authentication, R.string.title_activity_main_authentication);

        ((TextView) findViewById(R.id.base_tv_title)).setTextColor(getWColor(R.color.colorLanse));

        initView();
        initWindow();
    }

    private void initView() {
        mPictureManager = new PictureManager(this, this);
        findViewById(R.id.auth_iv_head).setOnClickListener(this);
        findViewById(R.id.auth_tv_sex).setOnClickListener(this);
        findViewById(R.id.auth_tv_city).setOnClickListener(this);
        findViewById(R.id.auth_tv_birth).setOnClickListener(this);
        findViewById(R.id.auth_iv_photo_1).setOnClickListener(this);
        findViewById(R.id.auth_iv_photo_2).setOnClickListener(this);
        findViewById(R.id.auth_iv_photo_3).setOnClickListener(this);

        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
        ((TextView) findViewById(R.id.right_btn_name)).setTextColor(getWColor(R.color.colorLanse));

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);
    }

    private void initWindow() {
        mBirthSelectWindow = new SelectWindow(this, 1);
        mBirthSelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.auth_tv_birth)).setText(selectContent);
            }
        });
        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.auth_tv_city)).setText(selectContent);
            }
        });
        sexSelectWindow = new SexSelectWindow(this);
        sexSelectWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sex_btn_man) {
                    ((TextView) findViewById(R.id.auth_tv_sex)).setText("男");
                } else {
                    ((TextView) findViewById(R.id.auth_tv_sex)).setText("女");
                }
                sexSelectWindow.cancel();
            }
        });
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        CommUtils.closeKeyboard(this);
        switch (v.getId()) {
            case R.id.auth_iv_head:
                mImageId = 1;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_HEAD);
                break;
            case R.id.auth_tv_sex:
                sexSelectWindow.show();
                break;
            case R.id.auth_tv_city:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");
                break;
            case R.id.auth_tv_birth:
                mBirthSelectWindow.show();
                break;
            case R.id.right_btn_submit:
                doSubmit();
                break;
            case R.id.auth_iv_photo_1:
                mImageId = 2;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_HEAD);
                break;
            case R.id.auth_iv_photo_2:
                mImageId = 3;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_HEAD);
                break;
            case R.id.auth_iv_photo_3:
                mImageId = 4;
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_HEAD);
                break;
        }
    }

    private void doSubmit() {
        if (TextUtils.isEmpty(mHeadStr)) {
            showMessage("请上传你的头像照片");
            return;
        }
        final String sex = ((TextView) findViewById(R.id.auth_tv_sex)).getText().toString();
        if (TextUtils.isEmpty(sex)) {
            showMessage("请选择你的性别");
            return;
        }
        final String position = ((EditText) findViewById(R.id.auth_tv_position)).getText().toString();
        if (TextUtils.isEmpty(position)) {
            showMessage("请输入你的职位");
            return;
        }
        final String school = ((EditText) findViewById(R.id.auth_tv_school)).getText().toString();
        if (TextUtils.isEmpty(school)) {
            showMessage("请输入你的毕业院校名称");
            return;
        }
        final String city = ((TextView) findViewById(R.id.auth_tv_city)).getText().toString();
        if (TextUtils.isEmpty(city)) {
            showMessage("请选择你当前所在城市");
            return;
        }
        final String birth = ((TextView) findViewById(R.id.auth_tv_birth)).getText().toString();
        if (TextUtils.isEmpty(birth)) {
            showMessage("请选择你的出生日期");
            return;
        }
        final String phone = ((TextView) findViewById(R.id.auth_tv_phone)).getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showMessage("请输入你的手机号码");
            return;
        }
        final String nickname = ((TextView) findViewById(R.id.auth_tv_nickname)).getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            showMessage("请输入你的姓名");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.show(R.string.text_request);
                        }
                    });
                    HashMap<String, Object> infoParams = new HashMap<>();
                    infoParams.put("token", mUserInfo.token);
                    infoParams.put("nickname", nickname);
                    infoParams.put("head", PictureManager.getBase64(mHeadStr));
                    infoParams.put("sex", sex);
                    infoParams.put("position", position);
                    infoParams.put("school", school);
                    infoParams.put("city", city);
                    infoParams.put("birth", birth);
                    infoParams.put("telphone", phone);
                    infoParams.put("photoArr", new ArrayList<>());
                    List<HashMap<String, Object>> mPicArrs = new ArrayList<>();
                    HashMap<String, Object> picItemParams;
                    if (!TextUtils.isEmpty(mPhotoStr1)) {
                        picItemParams = new HashMap<>();
                        picItemParams.put("pid", 0);
                        picItemParams.put("photo", PictureManager.getBase64(mPhotoStr1));
                        mPicArrs.add(picItemParams);
                    }
                    if (!TextUtils.isEmpty(mPhotoStr2)) {
                        picItemParams = new HashMap<>();
                        picItemParams.put("pid", 0);
                        picItemParams.put("photo", PictureManager.getBase64(mPhotoStr2));
                        mPicArrs.add(picItemParams);
                    }
                    if (!TextUtils.isEmpty(mPhotoStr3)) {
                        picItemParams = new HashMap<>();
                        picItemParams.put("pid", 0);
                        picItemParams.put("photo", PictureManager.getBase64(mPhotoStr3));
                        mPicArrs.add(picItemParams);
                    }
                    infoParams.put("photoArr", mPicArrs);
                    HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_USER_INFO_NEW, infoParams, MainAuthenticationActivity.this, true);
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.cancel();
                        }
                    });

                    L.d("Exception::" + ThrowableUtils.getThrowableDetailsMessage(e));
                    showMessage("头像解析失败，请稍后重试");
                }
            }
        }).start();
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
                break;
            case TASK_TYPE_UPDATE_USER_INFO_NEW:
                showMessage("信息更新成功");
                startActivity(new Intent(this, MainCompanyResumeActivity.class));
                mUserInfo.iswanshan = 1;
                SPUtils.saveUserInfo(mUserInfo);
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_USER_PRICE);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressedToBottom();
    }

    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        switch (mImageId) {
            case 1:
                mHeadStr = uri.getPath();
                ((ImageView) findViewById(R.id.auth_iv_head)).setImageURI(uri);
                break;
            case 2:
                mPhotoStr1 = uri.getPath();
                ((ImageView) findViewById(R.id.auth_iv_photo_1)).setImageURI(uri);
                break;
            case 3:
                mPhotoStr2 = uri.getPath();
                ((ImageView) findViewById(R.id.auth_iv_photo_2)).setImageURI(uri);
                break;
            case 4:
                mPhotoStr3 = uri.getPath();
                ((ImageView) findViewById(R.id.auth_iv_photo_3)).setImageURI(uri);
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
}
