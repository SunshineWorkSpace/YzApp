package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.custom.SexSelectWindow;
import com.yingshixiezuovip.yingshi.datautils.CommonThreadPool;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.HashMap;

public class MainAuthenticationSchoolActivity extends BaseActivity implements PictureManager.OnPictureCallbackListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_authentication_school, R.string.auth_school);

        initView();
    }

    private void initView() {
        findViewById(R.id.school_tv_sex).setOnClickListener(this);
        findViewById(R.id.school_tv_city).setOnClickListener(this);
        findViewById(R.id.school_tv_birth).setOnClickListener(this);
        findViewById(R.id.school_tv_starttime).setOnClickListener(this);
        findViewById(R.id.school_tv_endtime).setOnClickListener(this);
        findViewById(R.id.school_iv_photo_1).setOnClickListener(this);
        findViewById(R.id.school_iv_photo_2).setOnClickListener(this);
        findViewById(R.id.school_iv_photo_3).setOnClickListener(this);
        findViewById(R.id.school_btn_day).setOnClickListener(this);
        findViewById(R.id.school_btn_project).setOnClickListener(this);
        findViewById(R.id.school_btn_submit).setOnClickListener(this);
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
                    startActivity(new Intent(MainAuthenticationSchoolActivity.this, MainCompanyResumeActivity.class));
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
            showMessage("请输入你的入学年份");
            return;
        }
        final String endTime = getValue((TextView) findViewById(R.id.school_tv_endtime));
        if (TextUtils.isEmpty(endTime)) {
            showMessage("请输入你的毕业年份");
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

        CommonThreadPool.getThreadPool().addCachedTask(new Runnable() {
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
                    params.put("enrollmentYear", startTime);
                    params.put("graduationYear", endTime);
                    params.put("identity_card", PictureManager.getBase64(mPhotoStr1));
                    params.put("side_card", PictureManager.getBase64(mPhotoStr2));
                    params.put("studentProve", PictureManager.getBase64(mPhotoStr3));
                    params.put("price", price);
                    params.put("unit", isDay ? "天" : "项目");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.show(R.string.text_request);
                        }
                    });
                    HttpUtils.doPost(TaskType.TASK_TYPE_STUDENT_AUTH, params, MainAuthenticationSchoolActivity.this);
                } catch (Exception e) {
                    showMessage("图片信息编码失败，请重新选择");
                }
            }
        });
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
            case R.id.school_btn_submit:
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
            default:
                break;

        }
    }

    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        switch (mImageId) {
            case 1:
                mPhotoStr1 = uri.getPath();
                ((ImageView) findViewById(R.id.school_iv_photo_1)).setImageURI(uri);
                break;
            case 2:
                mPhotoStr2 = uri.getPath();
                ((ImageView) findViewById(R.id.school_iv_photo_2)).setImageURI(uri);
                break;
            case 3:
                mPhotoStr3 = uri.getPath();
                ((ImageView) findViewById(R.id.school_iv_photo_3)).setImageURI(uri);
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
