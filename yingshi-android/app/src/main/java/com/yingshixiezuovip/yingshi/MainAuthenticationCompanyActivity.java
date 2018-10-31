package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 2017/8/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class MainAuthenticationCompanyActivity extends BaseActivity implements PictureManager.OnPictureCallbackListener {
    private int mDefaultSMSInterval = 60;
    private int mCurrentSMSInterval = mDefaultSMSInterval;
    private PlaceModel mPlaceModel;
    private SelectWindow mCitySelectWindow;
    private String mLicensePath;
    private String mIdCard1Path;
    private String mIdCard2Path;
    private String mCompany1Path;
    private String mCompany2Path;
    private String mCompany3Path;
    private int mPosition = -1;
    private PictureManager mPictureManager;
    private boolean isDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_authentication_company);

        initView();
        initWindow();
    }

    private void initWindow() {
        mPictureManager = new PictureManager(this, this);
        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.company_tv_city)).setText(selectContent);
            }
        });
    }

    private void initView() {
        findViewById(R.id.company_btn_step1).setOnClickListener(this);
        findViewById(R.id.company_btn_step2).setOnClickListener(this);
        findViewById(R.id.company_btn_next).setOnClickListener(this);
        findViewById(R.id.company_tv_city).setOnClickListener(this);
        findViewById(R.id.company_iv_license).setOnClickListener(this);
        findViewById(R.id.company_iv_idCard1).setOnClickListener(this);
        findViewById(R.id.company_iv_idCard2).setOnClickListener(this);
        findViewById(R.id.company_iv_cover1).setOnClickListener(this);
        findViewById(R.id.company_iv_cover2).setOnClickListener(this);
        findViewById(R.id.company_iv_cover3).setOnClickListener(this);
        findViewById(R.id.company_btn_day).setOnClickListener(this);
        findViewById(R.id.company_btn_project).setOnClickListener(this);
        findViewById(R.id.company_btn_sendcode).setOnClickListener(this);
        findViewById(R.id.company_btn_submit).setOnClickListener(this);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);
        ((TextView) findViewById(R.id.company_tv_sendTips)).setText("短信验证码将会发送至" + mUserInfo.phone);
        enterStepOne();
    }

    private void enterStepOne() {
        setActivityTitle("商户信息");
        ((TextView) findViewById(R.id.company_btn_step1)).setTextColor(getWColor(R.color.colorLanse));
        ((TextView) findViewById(R.id.company_btn_step2)).setTextColor(getWColor(R.color.colorBlack));
        findViewById(R.id.commpanyauth_first_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.commpanyauth_second_layout).setVisibility(View.GONE);
    }

    private void enterStepSecond(boolean needCheck) {
        if (!needCheck || (needCheck && checkFirstStepForm())) {
            setActivityTitle("上传照片");
            ((TextView) findViewById(R.id.company_btn_step1)).setTextColor(getWColor(R.color.colorBlack));
            ((TextView) findViewById(R.id.company_btn_step2)).setTextColor(getWColor(R.color.colorLanse));
            findViewById(R.id.commpanyauth_first_layout).setVisibility(View.GONE);
            findViewById(R.id.commpanyauth_second_layout).setVisibility(View.VISIBLE);
        }
    }

    private boolean checkFirstStepForm() {
        String tempStr = getTextByID(R.id.company_et_name);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请输入公司名称");
            return false;
        }
        tempStr = getTextByID(R.id.company_et_legalName);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请输入营业执照上的法人姓名");
            return false;
        }
        tempStr = getTextByID(R.id.company_et_idCardID);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("身份证上的15或18位号码");
            return false;
        }
        tempStr = getTextByID(R.id.company_et_licenseNo);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请填写营业执照号码");
            return false;
        }
        tempStr = getTextByID(R.id.company_et_licenseNo);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请填写营业执照号码");
            return false;
        }
        tempStr = getTextByID(R.id.company_tv_city);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请选择您所在的城市");
            return false;
        }
        tempStr = getTextByID(R.id.company_et_address);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请输入门牌详细地址");
            return false;
        }
        tempStr = getTextByID(R.id.company_et_type);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请输入您的公司类型");
            return false;
        }
        return true;
    }

    private boolean checkSecondStepForm() {
        if (TextUtils.isEmpty(mLicensePath)) {
            showMessage("请上传营业执照");
            return false;
        }
        if (TextUtils.isEmpty(mIdCard1Path)) {
            showMessage("请上传你的身份证照片（正面）");
            return false;
        }
        if (TextUtils.isEmpty(mIdCard2Path)) {
            showMessage("请上传你的身份证照片（反面）");
            return false;
        }
        String tempStr = getTextByID(R.id.company_et_price);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请输入薪资");
            return false;
        }
        double xinzi;
        try {
            xinzi = Double.parseDouble(tempStr);
        } catch (Exception e) {
            xinzi = 0;
        }
        if (xinzi == 0) {
            showMessage("薪资必须大于0，请重新输入");
            return false;
        }
        tempStr = getTextByID(R.id.company_et_verifiCode);
        if (TextUtils.isEmpty(tempStr)) {
            showMessage("请先获取验证码");
            return false;
        }
        return true;
    }

    private void doUploadInfo() {
        mLoadWindow.show(R.string.waiting);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("token", mUserInfo.token);
                    hashMap.put("company", getTextByID(R.id.company_et_name));
                    hashMap.put("legalperson", getTextByID(R.id.company_et_legalName));
                    hashMap.put("indencard", getTextByID(R.id.company_et_idCardID));
                    hashMap.put("inden_positive", PictureManager.getBase64(mIdCard1Path));
                    hashMap.put("inden_negative", PictureManager.getBase64(mIdCard2Path));
                    hashMap.put("businesslicense_code", getTextByID(R.id.company_et_licenseNo));
                    hashMap.put("businesslicense_photo", PictureManager.getBase64(mLicensePath));
                    hashMap.put("lanlat_address", getTextByID(R.id.company_tv_city));
                    hashMap.put("detail_address", getTextByID(R.id.company_et_address));
                    hashMap.put("company_type", getTextByID(R.id.company_et_type));
                    hashMap.put("price", getTextByID(R.id.company_et_price));
                    hashMap.put("unit", isDay ? "天" : "项目");
                    List<HashMap<String, Object>> companyList = new ArrayList<>();
                    HashMap<String, Object> companyItem;
                    if (!TextUtils.isEmpty(mCompany1Path)) {
                        companyItem = new HashMap<String, Object>();
                        companyItem.put("photo", PictureManager.getBase64(mCompany1Path));
                        companyList.add(companyItem);
                    }
                    if (!TextUtils.isEmpty(mCompany2Path)) {
                        companyItem = new HashMap<String, Object>();
                        companyItem.put("photo", PictureManager.getBase64(mCompany2Path));
                        companyList.add(companyItem);
                    }
                    if (!TextUtils.isEmpty(mCompany3Path)) {
                        companyItem = new HashMap<String, Object>();
                        companyItem.put("photo", PictureManager.getBase64(mCompany3Path));
                        companyList.add(companyItem);
                    }
                    hashMap.put("companylist", companyList);
                    HttpUtils.doPost(TaskType.TASK_TYPE_COMPANY_INFO_UPLOAD, hashMap, MainAuthenticationCompanyActivity.this);
                } catch (Exception e) {
                    showMessage("发布失败，请稍后重试");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.cancel();
                        }
                    });
                }
            }
        }).start();
    }

    private void doCheckVerifiCode() {
        HashMap<String, Object> bindParams = new HashMap<>();
        bindParams.put("token", mUserInfo.token);
        bindParams.put("phone", mUserInfo.phone);
        bindParams.put("code", getTextByID(R.id.company_et_verifiCode));
        HttpUtils.doPost(TaskType.TASK_TYPE_COMPANY_CHECK_VERIFICODE, bindParams, this);
    }

    private void doSendCode() {
        HashMap<String, Object> sendCodeParams = new HashMap<>();
        sendCodeParams.put("token", mUserInfo.token);
        sendCodeParams.put("phone", mUserInfo.phone);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(TaskType.TASK_TYPE_SendSMS, sendCodeParams, this);
    }

    public String getTextByID(int resid) {
        return ((TextView) findViewById(resid)).getText().toString();
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            if (result instanceof BaseThrowable) {
                if (((BaseThrowable) result).getErrorCode() == 406) {
                    showMessage("验证码不正确，请重新输入");
                } else {
                    showMessage(((BaseThrowable) result).getMessage());
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
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
            case TASK_TYPE_SendSMS:
                enterCountTime();
                break;
            case TASK_TYPE_COMPANY_CHECK_VERIFICODE:
                doUploadInfo();
                break;
            case TASK_TYPE_COMPANY_INFO_UPLOAD:
                mUserInfo.type = 3;
                mUserInfo.iswanshan = 1;
                mUserInfo.isrenzhen = 1;
                mUserInfo.isbzj = 0;
                SPUtils.saveUserInfo(mUserInfo);
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_PRICE);
                showMessage("资料提交成功，还需要缴纳保证金");
                Intent intent = new Intent(this, MainAuthenticationMoneyActivity.class);
                intent.putExtra("company_auth", true);
                startActivity(intent);
                delayFinish(200);
                break;
        }
    }

    private Handler mCountHandler = new Handler();
    private Runnable mCountRunnable = new Runnable() {
        @Override
        public void run() {
            mCurrentSMSInterval--;
            if (mCurrentSMSInterval > 0) {
                ((TextView) findViewById(R.id.company_btn_sendcode)).setText(mCurrentSMSInterval + "s倒计时");
                mCountHandler.postDelayed(this, 1000);
            } else {
                ((TextView) findViewById(R.id.company_btn_sendcode)).setText("获取验证码");
                findViewById(R.id.company_btn_sendcode).setEnabled(true);
            }
        }
    };

    private void enterCountTime() {
        mCurrentSMSInterval = mDefaultSMSInterval;
        mCountHandler.postDelayed(mCountRunnable, 1000);
        findViewById(R.id.company_btn_sendcode).setEnabled(false);
    }

    /***
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_btn_step1:
                enterStepOne();
                break;
            case R.id.company_btn_step2:
                enterStepSecond(false);
                break;
            case R.id.company_btn_next:
                enterStepSecond(true);
                break;
            case R.id.company_tv_city:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");
                break;
            case R.id.company_iv_license:
                showPictureWindow(1);
                break;
            case R.id.company_iv_idCard1:
                showPictureWindow(2);
                break;
            case R.id.company_iv_idCard2:
                showPictureWindow(3);
                break;
            case R.id.company_iv_cover1:
                showPictureWindow(4);
                break;
            case R.id.company_iv_cover2:
                showPictureWindow(5);
                break;
            case R.id.company_iv_cover3:
                showPictureWindow(6);
                break;
            case R.id.company_btn_day:
                isDay = true;
                refreshSalary();
                break;
            case R.id.company_btn_project:
                isDay = false;
                refreshSalary();
                break;
            case R.id.company_btn_sendcode:
                doSendCode();
                break;
            case R.id.company_btn_submit:
                if (checkFirstStepForm() && checkSecondStepForm()) {
                    doCheckVerifiCode();
                }
                break;
        }
    }

    private void refreshSalary() {
        ((CheckBox) findViewById(R.id.company_cb_day)).setChecked(isDay);
        ((CheckBox) findViewById(R.id.company_cb_project)).setChecked(!isDay);
    }

    private void showPictureWindow(int position) {
        mPosition = position;
        mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PictureManager.PM_REQUESTCODE_CROP || requestCode == PictureManager.PM_REQUESTCODE_CAMERA || requestCode == PictureManager.PM_REQUESTCODE_MEDIA) {
            mPictureManager.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        int resid = -1;
        switch (mPosition) {
            case 1:
                mLicensePath = uri.getPath();
                resid = R.id.company_iv_license;
                break;
            case 2:
                mIdCard1Path = uri.getPath();
                resid = R.id.company_iv_idCard1;
                break;
            case 3:
                mIdCard2Path = uri.getPath();
                resid = R.id.company_iv_idCard2;
                break;
            case 4:
                mCompany1Path = uri.getPath();
                resid = R.id.company_iv_cover1;
                break;
            case 5:
                mCompany2Path = uri.getPath();
                resid = R.id.company_iv_cover2;
                break;
            case 6:
                mCompany3Path = uri.getPath();
                resid = R.id.company_iv_cover3;
                break;
            default:
                resid = -1;
                break;
        }
        if (resid > 0) {
            PictureManager.displayImage(uri.getPath(), (ImageView) findViewById(resid));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountHandler.removeCallbacks(mCountRunnable);
    }

    @Override
    public void onPictureUpload(int status, String message) {
    }
}
