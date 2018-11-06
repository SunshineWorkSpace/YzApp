package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 类名称:HomeShopPublishDetailActivity
 * 类描述:商品发布页
 * 创建时间: 2018-11-04-22:42
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class HomeShopPublishDetailActivity extends BaseActivity {
    private EditText et_title,et_single_price,et_num,et_type,et_phone,et_area_detail;
    private TextView tv_new,tv_area;
    private SelectWindow mCitySelectWindow,mNewOldWindow;
    private PlaceModel mPlaceModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_publish);
        et_title=(EditText) findViewById(R.id.et_title);
        et_single_price=(EditText)findViewById(R.id.et_single_price);
        et_num=(EditText)findViewById(R.id.et_num);
        tv_new=(TextView)findViewById(R.id.tv_new);
        tv_new.setOnClickListener(this);
        et_type=(EditText)findViewById(R.id.et_type);
        et_phone=(EditText)findViewById(R.id.et_phone);
        tv_area=(TextView)findViewById(R.id.tv_area);
        tv_area.setOnClickListener(this);
        et_area_detail=(EditText)findViewById(R.id.et_area_detail);
        setActivityTitle("发布页(1/3)");
        ((TextView) findViewById(R.id.right_btn_name)).setText("下一步");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                tv_area.setText(selectContent);
//                        ((TextView) findViewById(R.id.school_tv_city)).setText(selectContent);
            }
        });
        mNewOldWindow=new SelectWindow(this,5);
        mNewOldWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                tv_new.setText(selectContent);
            }
        });
     initView();
    }

    private void initView() {
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);
    }


    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.tv_new:
                mNewOldWindow.show(1,"");
                break;
            case R.id.tv_area:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");

                break;
            case R.id.right_btn_submit:
                checkDate();
                break;
        }
    }

    private void checkDate() {
        if(TextUtils.isEmpty(et_title.getText().toString())){
            showMessage("请输入标题");
            return;
        }
        if(TextUtils.isEmpty(et_single_price.getText().toString())){
            showMessage("请输入单价价格");
            return;
        }
        if(TextUtils.isEmpty(et_num.getText().toString())){
            showMessage("请输入数量");
            return;
        }
        if(TextUtils.isEmpty(tv_new.getText().toString())){
            showMessage("请选择新旧");
            return;
        }
        if(TextUtils.isEmpty(et_phone.getText().toString())){
            showMessage("请输入联系方式");
            return;
        }
        if(TextUtils.isEmpty(tv_area.getText().toString())){
            showMessage("请选择地址");
            return;
        }
        if(TextUtils.isEmpty(et_area_detail.getText().toString())){
            showMessage("请输入详细地址");
            return;
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
                    HttpUtils.doPost(TaskType.TASK_TYPE_QRY_STUDENT_INTO, params, this);
                } catch (Exception e) {
                    mLoadWindow.cancel();
                    mPlaceModel = null;
                    showMessage("城市数据获取失败，请稍后重试");
                }
                break;
        }
    }
}
