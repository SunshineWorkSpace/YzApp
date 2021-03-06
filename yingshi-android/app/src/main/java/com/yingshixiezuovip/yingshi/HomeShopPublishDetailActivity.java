package com.yingshixiezuovip.yingshi;

import android.content.Intent;
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
import com.yingshixiezuovip.yingshi.model.ShopDetailTypeModel;
import com.yingshixiezuovip.yingshi.model.ShopDetailUpDataModel;
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
    private EditText et_title,et_single_price,et_num,et_type;
    private TextView tv_new,tv_area,et_area_detail,et_phone;
    private SelectWindow mCitySelectWindow,mNewOldWindow;
    private PlaceModel mPlaceModel;
    private boolean isFirst=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_publish);
        pushActivity(this);
        isFirst=getIntent().getBooleanExtra("isfirst",true);
        et_title=(EditText) findViewById(R.id.et_title);
        et_single_price=(EditText)findViewById(R.id.et_single_price);
        et_num=(EditText)findViewById(R.id.et_num);
        tv_new=(TextView)findViewById(R.id.tv_new);
        tv_new.setOnClickListener(this);
        et_type=(EditText)findViewById(R.id.et_type);
        et_phone=(TextView)findViewById(R.id.et_phone);
        et_phone.setOnClickListener(this);
        tv_area=(TextView)findViewById(R.id.tv_area);
        tv_area.setOnClickListener(this);
        et_area_detail=(TextView)findViewById(R.id.et_area_detail);
        et_area_detail.setOnClickListener(this);
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
//     initView();
     if(!isFirst){
         getDate();
     }
    }

    private void getDate() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id", getIntent().getStringExtra("id"));
        HttpUtils.doPost(TaskType.TASK_SHOP_DETAIL_DATE, params, this);
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
              /*  if (mPlaceModel == null || mPlaceModel.data == null || mPlaceModel.data.size() == 0) {
                    showMessage("城市数据获取失败，请稍后重试");
                    return;
                }
                mCitySelectWindow.show(mPlaceModel, "请选择地区");*/
            case R.id.et_area_detail:
            case R.id.et_phone:
                Intent addAddress=new Intent(this,
                        UserAddressListActivity.class);
                addAddress.putExtra("type",1);
                startActivityForResult(addAddress,110);
                break;
            case R.id.right_btn_submit:
                checkDate();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            tv_area.setText(data.getStringExtra("city"));
            et_area_detail.setText(data.getStringExtra("address"));
            et_phone.setText(data.getStringExtra("telphone"));
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
        Intent it=new Intent(this,HomeShopPublishDetialNextActivitiy.class);
        it.putExtra("title",et_title.getText().toString());
        it.putExtra("singleprice",et_single_price.getText().toString());
        it.putExtra("num",et_num.getText().toString());
        it.putExtra("new",tv_new.getText().toString());
        it.putExtra("phone",et_phone.getText().toString());
        it.putExtra("area",tv_area.getText().toString());
        it.putExtra("areadetail",et_area_detail.getText().toString());
        it.putExtra("type",et_type.getText().toString());
        //========
        it.putExtra("isFirst",isFirst);
        it.putExtra("shopDetailUpDataModel",shopDetailUpDataModel);
        startActivity(it);
    }
    ShopDetailUpDataModel shopDetailUpDataModel;
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
            case TASK_SHOP_DETAIL_DATE:
                shopDetailUpDataModel= GsonUtil.fromJson(result.toString(), ShopDetailUpDataModel.class);
                et_title.setText(shopDetailUpDataModel.data.title);
                et_single_price.setText(shopDetailUpDataModel.data.price);
                et_num.setText(shopDetailUpDataModel.data.num);
                tv_new.setText(shopDetailUpDataModel.data.isnew);
                et_phone.setText(shopDetailUpDataModel.data.constans);
                tv_area.setText(shopDetailUpDataModel.data.city);
                et_area_detail.setText(shopDetailUpDataModel.data.address);
                break;
        }
    }
}
