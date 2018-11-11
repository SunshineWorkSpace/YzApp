package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.AddressListModel;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.HashMap;

/**
 * Created by yuhua.gou on 2018/11/11.
 */

public class HomeShopAddAddressAcivity extends BaseActivity {
    private LinearLayout ll_city;
    private TextView tv_city,tv_city_choice;
    private EditText edt_receiver,edt_phone,edt_address;
    private SelectWindow mCitySelectWindow;
    private PlaceModel mPlaceModel;
    protected UserInfo mUserInfo;
    private AddressListModel.AddressModel addressModel;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_addaddress,
                R.string.title_activity_home_shop_addaddress);
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.right_btn_name)).setText("保存");
        ((TextView) findViewById(R.id.right_btn_name)).setTextColor(
                Color.parseColor("#75C7CC"));
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        ll_city=(LinearLayout) findViewById(R.id.ll_city);
        ll_city.setOnClickListener(this);
        tv_city_choice=(TextView) findViewById(R.id.tv_city_choice);
        tv_city=(TextView) findViewById(R.id.tv_city);

        edt_receiver= (EditText) findViewById(R.id.edt_receiver);
        edt_phone=(EditText) findViewById(R.id.edt_phone);
        edt_address=(EditText) findViewById(R.id.edt_address);
        mCitySelectWindow = new SelectWindow(this, 3);
        mCitySelectWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                tv_city.setText(selectContent);
                tv_city_choice.setVisibility(View.GONE);
            }
        });
        mUserInfo = SPUtils.getUserInfo(this);
        loadData();
        initAddressView();
    }

    private void loadData(){
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_CITY_INFO, new HashMap<String, Object>(), this);

    }

    private void initAddressView(){
        addressModel=(AddressListModel.AddressModel)
                getIntent().getSerializableExtra("address");
        if(null!=addressModel){
            id=addressModel.id;
            if(!TextUtils.isEmpty(addressModel.city)){
                tv_city.setText(addressModel.city);
                tv_city_choice.setVisibility(View.GONE);
            }
            edt_receiver.setText(addressModel.revcname);
            edt_phone.setText(addressModel.telphone);
            edt_address.setText(addressModel.address);
        }
    }

    private void addAddress(){
        if(TextUtils.isEmpty(edt_receiver.getText().toString().trim())){
            showMessage("请输入签收人姓名");
            return;
        }
        if(TextUtils.isEmpty(edt_phone.getText().toString().trim())){
            showMessage("请输入签收人联系电话");
            return;
        }

        if(TextUtils.isEmpty(tv_city.getText().toString().trim())){
            showMessage("请选择地区");
            return;
        }

        if(TextUtils.isEmpty(edt_address.getText().toString().trim())){
            showMessage("请输入详细地址");
            return;
        }
        mLoadWindow.show(R.string.text_request);
        mUserInfo = SPUtils.getUserInfo(this);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("revcname", edt_receiver.getText().toString().trim());
        params.put("telphone", edt_phone.getText().toString().trim());
        params.put("city", tv_city.getText().toString().trim());
        params.put("address", edt_address.getText().toString().trim());
        HttpUtils.doPost(TaskType.TASK_TYPE_ADDADDRESS, params, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.right_btn_submit:
                submmiteAddress();
                break;
            case R.id.ll_city:
                if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");
                break;
        }
    }
    private void submmiteAddress(){
        if(TextUtils.isEmpty(id)){
            addAddress();
        }else {
            editAddress();
        }
    }

    public void editAddress(){
        if(TextUtils.isEmpty(edt_receiver.getText().toString().trim())){
            showMessage("请输入签收人姓名");
            return;
        }
        if(TextUtils.isEmpty(edt_phone.getText().toString().trim())){
            showMessage("请输入签收人联系电话");
            return;
        }

        if(TextUtils.isEmpty(tv_city.getText().toString().trim())){
            showMessage("请选择地区");
            return;
        }

        if(TextUtils.isEmpty(edt_address.getText().toString().trim())){
            showMessage("请输入详细地址");
            return;
        }
        mLoadWindow.show(R.string.text_request);
        mUserInfo = SPUtils.getUserInfo(this);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id",id);
        params.put("revcname", edt_receiver.getText().toString().trim());
        params.put("telphone", edt_phone.getText().toString().trim());
        params.put("city", tv_city.getText().toString().trim());
        params.put("address", edt_address.getText().toString().trim());
        HttpUtils.doPost(TaskType.TASK_TYPE_EIDT_ADDRESS, params, this);
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
                } catch (Exception e) {
                    mLoadWindow.cancel();
                    mPlaceModel = null;
                    showMessage("城市数据获取失败，请稍后重试");
                }
                break;

            case TASK_TYPE_ADDADDRESS:
                try {
                    BaseResp baseResp= GsonUtil.fromJson(result.toString(), PlaceModel.class);
                    Intent intent=new Intent();
                    intent.putExtra("address",edt_address.getText().toString().trim());
                    intent.putExtra("city",tv_city.getText().toString().trim());
                    setResult(RESULT_OK,intent);
                    finish();
                    System.out.println("添加收货地址："+result.toString());
                    if(200==baseResp.result.code){
                        showMessage("添加成功");
                    }
                } catch (Exception e) {
                    mLoadWindow.cancel();
                    showMessage("添加失败");
                }
                break;
            case TASK_TYPE_EIDT_ADDRESS:
                try {
                    BaseResp baseResp= GsonUtil.fromJson(result.toString(), PlaceModel.class);
                    Intent intent=new Intent();
                    intent.putExtra("address",edt_address.getText().toString().trim());
                    intent.putExtra("city",tv_city.getText().toString().trim());
                    intent.putExtra("name",edt_receiver.getText().toString().trim());
                    intent.putExtra("phone",edt_phone.getText().toString().trim());
                    intent.putExtra("id",id);
                    setResult(RESULT_OK,intent);
                    finish();
                    System.out.println("修改收货地址："+result.toString());
                    if(200==baseResp.result.code){
                        showMessage("修改成功");
                    }
                } catch (Exception e) {
                    mLoadWindow.cancel();
                    showMessage("修改失败");
                }
                break;
        }
    }
}
