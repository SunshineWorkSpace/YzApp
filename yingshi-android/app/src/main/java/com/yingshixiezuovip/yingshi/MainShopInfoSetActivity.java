package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.custom.SexSelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 类名称:MainShopInfoSetActivity
 * 类描述:商铺修改
 * 创建时间: 2018-11-14-17:18
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class MainShopInfoSetActivity extends BaseActivity {
    public int mType;
    private SelectWindow mBirthSelectWindow;
    private SelectWindow mCitySelectWindow;
    private PlaceModel mPlaceModel;
    private SexSelectWindow sexSelectWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_set);
        setActivityTitle("商户修改资料");
        ((TextView) findViewById(R.id.right_btn_name)).setText("修改");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        sexSelectWindow = new SexSelectWindow(this);
        sexSelectWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sex_btn_man) {
                    ((TextView) findViewById(R.id.sinfo_tv_sex)).setText("男");
                } else {
                    ((TextView) findViewById(R.id.sinfo_tv_sex)).setText("女");
                }

                sexSelectWindow.cancel();
            }
        });

        mBirthSelectWindow = new SelectWindow(this, 1);
        mBirthSelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.sinfo_tv_birth)).setText(selectContent);
            }
        });

        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                ((TextView) findViewById(R.id.school_tv_city)).setText(selectContent);
            }
        });
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.right_btn_submit:
                doSubmitInfo();
                break;
            case R.id.sinfo_tv_sex:
                sexSelectWindow.show();
                break;
            case R.id.sinfo_tv_city:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");
                break;
            case R.id.sinfo_tv_birth:
                mBirthSelectWindow.show();
                break;
            case R.id.sinfo_tv_startime:
                showMessage("学生资料入学时间不可修改！");
                break;
            case R.id.sinfo_tv_endtime:
                showMessage("学生资料毕业时间不可修改！");
                break;
            default:
                break;
        }
    }

    private void doSubmitInfo() {
        final String name = getValue((TextView) findViewById(R.id.sinfo_et_nickname));
        if (TextUtils.isEmpty(name)) {
            showMessage("请输入你的真实姓名");
            return;
        }
        final String sex = getValue((TextView) findViewById(R.id.sinfo_tv_sex));
        if (TextUtils.isEmpty(sex)) {
            showMessage("请选择你的性别");
            return;
        }
        final String zy = getValue((TextView) findViewById(R.id.zy_et_school));
        if (TextUtils.isEmpty(zy)) {
            showMessage("请输入你的专业名称");
            return;
        }
        final String schoolName = getValue((TextView) findViewById(R.id.sinfo_et_school));
        if (TextUtils.isEmpty(schoolName)) {
            showMessage("请输入你的院校名称");
            return;
        }


        final String city = getValue((TextView) findViewById(R.id.sinfo_tv_city));
        if (TextUtils.isEmpty(city)) {
            showMessage("请选择你的当前城市");
            return;
        }
        final String birth = getValue((TextView) findViewById(R.id.sinfo_tv_birth));
        if (TextUtils.isEmpty(birth)) {
            showMessage("请输入你的生日");
            return;
        }
        final String startTime = getValue((TextView) findViewById(R.id.sinfo_tv_startime));
        if (TextUtils.isEmpty(startTime)) {
            showMessage("请输入你的联系方式");
            return;
        }
        final String endTime = getValue((TextView) findViewById(R.id.sinfo_tv_endtime));
        if (TextUtils.isEmpty(endTime)) {
            showMessage("请输入你的店铺类型");
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", mUserInfo.token);
        hashMap.put("truename", name);
        hashMap.put("sex", sex);
        hashMap.put("major",zy);
        hashMap.put("school", schoolName);
        hashMap.put("city", city);
        hashMap.put("birth", birth);
        hashMap.put("constansphone", startTime);
        hashMap.put("shoptype", endTime);
        mLoadWindow.show();
        HttpUtils.doPost(TaskType.TASK_TYPE_UDDATA_BUSINESS_SUBMIT, hashMap, this);
    }
    @Nullable
    private String getValue(TextView textView) {
        try {
            return (textView.getText().toString() + "").trim();
        } catch (Exception e) {
            return null;
        }
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
                try {
                    mPlaceModel = GsonUtil.fromJson(result.toString(), PlaceModel.class);
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("token", mUserInfo.token);
                    HttpUtils.doPost(TaskType.TASK_TYPE_UPDATA_BUSINESS, params, this);
                } catch (Exception e) {
                    mLoadWindow.cancel();
                    mPlaceModel = null;
                    showMessage("城市数据获取失败，请稍后重试");
                }
                break;
            case TASK_TYPE_UPDATA_BUSINESS:
                mLoadWindow.cancel();
                inflateInfoLayout(((JSONObject) result).optJSONObject("data"));
                break;
            case TASK_TYPE_UDDATA_BUSINESS_SUBMIT:
                showMessage("信息更新成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_USER_PRICE);
                onBackPressed();
                break;
        }
    }
    private void inflateInfoLayout(JSONObject jsonObject) {
        findViewById(R.id.sinfo_tv_sex).setOnClickListener(this);
        findViewById(R.id.sinfo_tv_city).setOnClickListener(this);
        findViewById(R.id.sinfo_tv_birth).setOnClickListener(this);
  /*      findViewById(R.id.sinfo_tv_startime).setOnClickListener(this);
        findViewById(R.id.sinfo_tv_endtime).setOnClickListener(this);*/

        ((TextView) findViewById(R.id.sinfo_et_nickname)).setText(jsonObject.optString("truename"));
        ((TextView) findViewById(R.id.sinfo_tv_sex)).setText(jsonObject.optString("sex"));
        ((TextView) findViewById(R.id.zy_et_school)).setText(jsonObject.optString("major"));
        ((TextView) findViewById(R.id.sinfo_et_school)).setText(jsonObject.optString("school"));
        ((TextView) findViewById(R.id.sinfo_tv_city)).setText(jsonObject.optString("city"));
        ((TextView) findViewById(R.id.sinfo_tv_birth)).setText(jsonObject.optString("birth"));
        ((TextView) findViewById(R.id.sinfo_tv_startime)).setText(jsonObject.optString("constansphone"));
        ((TextView) findViewById(R.id.sinfo_tv_endtime)).setText(jsonObject.optString("shoptype"));
    }
}
