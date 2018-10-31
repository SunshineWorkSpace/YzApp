package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.umeng.socialize.media.Base;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.custom.UrlWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.model.CompanyInfoModel;
import com.yingshixiezuovip.yingshi.model.CompanyStatusModel;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import org.json.JSONObject;

import java.util.HashMap;

public class MainCompanyBaseInfoActivity extends BaseActivity {
    private CompanyInfoModel mCompanyInfoModel;
    private SelectWindow mCitySelectWindow;
    private PlaceModel mPlaceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company_base_info, R.string.activity_main_company_base_info_title);

        initView();
        initWindow();
        loadData();
    }

    private void initView() {
        findViewById(R.id.company_btn_name).setOnClickListener(this);
        findViewById(R.id.company_btn_legalname).setOnClickListener(this);
        findViewById(R.id.company_btn_license).setOnClickListener(this);
        findViewById(R.id.company_tv_city).setOnClickListener(this);
    }

    private void initWindow() {
        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.company_tv_city)).setText(selectContent);
            }
        });
    }


    private void inflateData() {
        ((TextView) findViewById(R.id.company_tv_name)).setText(mCompanyInfoModel.company);
        ((TextView) findViewById(R.id.company_tv_legalname)).setText(mCompanyInfoModel.legalperson);
        ((TextView) findViewById(R.id.company_tv_license)).setText(mCompanyInfoModel.businesslicense_code);
        ((TextView) findViewById(R.id.company_et_comtype)).setText(mCompanyInfoModel.company_type);
        ((TextView) findViewById(R.id.company_tv_city)).setText(mCompanyInfoModel.lanlat_address);
        ((TextView) findViewById(R.id.company_et_address)).setText(mCompanyInfoModel.detail_address);

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("保存");

        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);
    }

    @Override
    protected void onSingleClick(View v) {
        if (mCompanyInfoModel == null) {
            showMessage("企业信息加载失败，请稍后重试");
            return;
        }
        switch (v.getId()) {
            case R.id.company_btn_name:
            case R.id.company_btn_legalname:
            case R.id.company_btn_license:
                showMessage("企业资料实名认证，法人姓名和营业执照不可修改");
                break;
            case R.id.company_tv_city:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");
                break;
            case R.id.right_btn_submit:
                doSave();
                break;
        }
    }

    private void doSave() {
        String comType = ((TextView) findViewById(R.id.company_et_comtype)).getText().toString();
        if (TextUtils.isEmpty(comType)) {
            showMessage("请输入公司类型");
            return;
        }
        String cityStr = ((TextView) findViewById(R.id.company_tv_city)).getText().toString();
        if (TextUtils.isEmpty(cityStr)) {
            showMessage("请输入公司所在城市");
            return;
        }
        String addressStr = ((TextView) findViewById(R.id.company_et_address)).getText().toString();
        if (TextUtils.isEmpty(addressStr)) {
            showMessage("请输入公司详细地址");
            return;
        }
        mLoadWindow.show(R.string.waiting);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", mUserInfo.token);
        hashMap.put("company_type", comType);
        hashMap.put("lanlat_address", cityStr);
        hashMap.put("detail_address", addressStr);
        HttpUtils.doPost(TaskType.TASK_TYPE_UPDADE_COMPANY_INFO, hashMap, this);
    }

    private void loadData() {
        mLoadWindow.show(R.string.text_loading);
        HashMap<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_COMPANY_INFO, tokenParams, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            mLoadWindow.cancel();
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_CITY_INFO:
                mLoadWindow.cancel();
                try {
                    mPlaceModel = GsonUtil.fromJson(result.toString(), PlaceModel.class);
                } catch (Exception e) {
                    mPlaceModel = null;
                    showMessage("城市数据获取失败，请稍后重试");
                }
                break;
            case TASK_TYPE_QRY_COMPANY_INFO:
                if (((JSONObject) result).has("data")) {
                    try {
                        mCompanyInfoModel = GsonUtil.fromJson(((JSONObject) result).optString("data"), CompanyInfoModel.class);
                        inflateData();
                    } catch (Exception e) {
                        L.d("Exception = " + ThrowableUtils.getThrowableDetailsMessage(e));
                        mCompanyInfoModel = null;
                    }
                } else {
                    mCompanyInfoModel = null;
                }
                if (mCompanyInfoModel == null) {
                    showMessage("企业信息加载失败，请稍后重试");
                }
                break;
            case TASK_TYPE_UPDADE_COMPANY_INFO:
                showMessage("保存成功");
                onBackPressed();
                break;
        }
    }

}
