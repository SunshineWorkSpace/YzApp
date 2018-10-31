package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.custom.SexSelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class UserInfoSettingsActivity extends BaseActivity {
    public static final int TYPE_INFO = 1, TYPE_PRICE = 2, TYPE_INVATE = 3;
    public int mType;
    private boolean isDay = true;
    private SelectWindow mBirthSelectWindow;
    private SelectWindow mCitySelectWindow;
    private PlaceModel mPlaceModel;
    private SexSelectWindow sexSelectWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_settings);


        mType = getIntent().getIntExtra("user_type", TYPE_INFO);
        initView();
    }

    private void initView() {
        setActivityTitle(mType == TYPE_INFO ? "修改资料" : mType == TYPE_PRICE ? "修改薪资" : "填写邀请码");
        ((TextView) findViewById(R.id.right_btn_name)).setText("保存");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        if (mType == TYPE_INFO) {
            initUserInfo();
        } else if (mType == TYPE_PRICE) {
            initPriceInfo();
        } else {
            findViewById(R.id.right_btn_submit).setVisibility(View.GONE);
            findViewById(R.id.uinfo_invite_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.uinfo_invite_btn_submit).setOnClickListener(this);
        }
    }


    private void initUserInfo() {
        findViewById(R.id.uinfo_base_layout).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.uinfo_et_nickname)).setText(mUserInfo.nickname);
        ((TextView) findViewById(R.id.uinfo_tv_sex)).setText(mUserInfo.sex);
        ((EditText) findViewById(R.id.uinfo_et_position)).setText(mUserInfo.position);
        ((EditText) findViewById(R.id.uinfo_et_school)).setText(mUserInfo.school);
        ((TextView) findViewById(R.id.uinfo_tv_city)).setText(mUserInfo.city);
        ((TextView) findViewById(R.id.uinfo_tv_birth)).setText(mUserInfo.birth);
        ((EditText) findViewById(R.id.uinfo_et_phone)).setText(mUserInfo.telphone);
        findViewById(R.id.uinfo_tv_sex).setOnClickListener(this);
        findViewById(R.id.uinfo_tv_city).setOnClickListener(this);
        findViewById(R.id.uinfo_tv_birth).setOnClickListener(this);
        initWindow();
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);
    }

    private void initWindow() {
        mBirthSelectWindow = new SelectWindow(this, 1);
        mBirthSelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.uinfo_tv_birth)).setText(selectContent);
            }
        });
        sexSelectWindow = new SexSelectWindow(this);
        sexSelectWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sex_btn_man) {
                    ((TextView) findViewById(R.id.uinfo_tv_sex)).setText("男");
                } else {
                    ((TextView) findViewById(R.id.uinfo_tv_sex)).setText("女");
                }
                sexSelectWindow.cancel();
            }
        });

        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.uinfo_tv_city)).setText(selectContent);
            }
        });
    }

    private void initPriceInfo() {
        findViewById(R.id.uinfo_price_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.uinfo_btn_day).setOnClickListener(this);
        findViewById(R.id.uinfo_btn_project).setOnClickListener(this);
        ((TextView) findViewById(R.id.uinfo_tv_price)).setText("原始薪酬：" + mUserInfo.price + "元 / " + mUserInfo.unit);
    }

    private void doPriceSubmit() {
        double price;
        String priceStr = ((EditText) findViewById(R.id.uinfo_et_price)).getText().toString();
        if (TextUtils.isEmpty(priceStr)) {
            showMessage("请输入修改的价格");
            return;
        }
        if (priceStr.startsWith(".")) {
            showMessage("请输入正确的价格");
            return;
        }
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            showMessage("请输入正确的价格");
            return;
        }
        mLoadWindow.show(R.string.text_request);
        HashMap<String, Object> priceParams = new HashMap<>();
        priceParams.put("token", mUserInfo.token);
        priceParams.put("price", price);
        priceParams.put("unit", isDay ? "天" : "项目");
        HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_USER_PRICE, priceParams, this);
    }


    private void doInfoSubmit() {
        boolean isChange = false;
        String nickname = ((EditText) findViewById(R.id.uinfo_et_nickname)).getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            showMessage("请输入姓名");
            return;
        }
        if (!nickname.equals(mUserInfo.nickname)) {
            isChange = true;
        }
        String sex = ((TextView) findViewById(R.id.uinfo_tv_sex)).getText().toString();
        if (!sex.equals(mUserInfo.sex)) {
            isChange = true;
        }
        String position = ((EditText) findViewById(R.id.uinfo_et_position)).getText().toString();
        if (TextUtils.isEmpty(position)) {
            showMessage("请输入职位信息");
            return;
        }
        if (!position.equals(mUserInfo.position)) {
            isChange = true;
        }
        String school = ((EditText) findViewById(R.id.uinfo_et_school)).getText().toString();
        if (!school.equals(mUserInfo.school)) {
            isChange = true;
        }
        String city = ((TextView) findViewById(R.id.uinfo_tv_city)).getText().toString();
        if (!city.equals(mUserInfo.city)) {
            isChange = true;
        }
        String birth = ((TextView) findViewById(R.id.uinfo_tv_birth)).getText().toString();
        if (!birth.equals(mUserInfo.birth)) {
            isChange = true;
        }
        String phone = ((TextView) findViewById(R.id.uinfo_et_phone)).getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showMessage("请输入手机号码");
            return;
        }
        if (!phone.equals(mUserInfo.telphone)) {
            isChange = true;
        }
        if (!isChange) {
            showMessage("你没有修改任何内容");
            return;
        }

        HashMap<String, Object> infoParams = new HashMap<>();
        infoParams.put("token", mUserInfo.token);
        infoParams.put("nickname", nickname);
        infoParams.put("head", "");
        infoParams.put("sex", sex);
        infoParams.put("position", position);
        infoParams.put("school", school);
        infoParams.put("city", city);
        infoParams.put("birth", birth);
        infoParams.put("telphone", phone);
        infoParams.put("photoArr", new ArrayList<>());
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_USER_INFO, infoParams, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.uinfo_btn_day:
                onClickCheckBox(1);
                break;
            case R.id.uinfo_btn_project:
                onClickCheckBox(2);
                break;
            case R.id.right_btn_submit:
                if (mType == TYPE_INFO) {
                    doInfoSubmit();
                } else {
                    doPriceSubmit();
                }
                break;
            case R.id.uinfo_tv_sex:
                sexSelectWindow.show();
                break;
            case R.id.uinfo_tv_city:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, ((TextView) findViewById(R.id.uinfo_tv_city)).getText().toString());
                break;
            case R.id.uinfo_tv_birth:
                mBirthSelectWindow.show();
                break;
            case R.id.uinfo_invite_btn_submit:
                String inviteStr = ((EditText) findViewById(R.id.uinfo_et_invite)).getText().toString();
                if (TextUtils.isEmpty(inviteStr)) {
                    showMessage("请输入邀请码");
                    return;
                }
                HashMap<String, Object> inviteParams = new HashMap<>();
                inviteParams.put("token", mUserInfo.token);
                inviteParams.put("invite", inviteStr);
                mLoadWindow.show(R.string.text_request);
                HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_INVITE_CODE, inviteParams, this);
                break;
        }
    }


    private void onClickCheckBox(int index) {
        ((CheckBox) findViewById(R.id.uinfo_cb_day)).setChecked(index == 1);
        ((CheckBox) findViewById(R.id.uinfo_cb_project)).setChecked(index != 1);
        isDay = index == 1;
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            if (result instanceof BaseThrowable) {
                switch (((BaseThrowable) result).getErrorCode()) {
                    case 205:
                        startActivity(new Intent(this, MainAuthenticationInfoActivity.class));
                        showMessage("推荐码失效或者不存在");
                        break;
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_UPDATE_USER_PRICE:
            case TASK_TYPE_UPDATE_USER_INFO:
            case TASK_TYPE_UPDATE_INVITE_CODE:
                showMessage("信息更新成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_USER_PRICE);
                if (mType == TYPE_INVATE) {
                    startActivity(new Intent(this, MainAuthenticationInfoActivity.class));
                } else {
                    onBackPressed();
                }
                break;
            case TASK_TYPE_QRY_CITY_INFO:
                try {
                    mPlaceModel = GsonUtil.fromJson(result.toString(), PlaceModel.class);
                } catch (Exception e) {
                    mPlaceModel = null;
                    showMessage("城市数据获取失败，请稍后重试");
                }
                break;
        }
    }
}
