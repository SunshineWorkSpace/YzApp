package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.model.ShopDetailTypeModel;

/**
 * 订单详情
 * Created by yuhua.gou on 2018/11/10.
 */

public class HomeShopOderDetailActivity extends BaseActivity {
    private TextView tv_shop_owner,tv_total_num,tv_price,
            tv_name_newsdegree,tv_buy_num,tv_freight_info,
            tv_freight,tv_total_fee,tv_total_info;
    private TextView tv_add,tv_add_address;
    private LinearLayout ll_add_address;
    private ShopDetailTypeModel.ShopDetailType mShopDetail;
    private final static int REQUEST_ADD_ADDRESS=110;
    private String cityName,addressName,id,telphone,revcname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_oderdetail,
                R.string.title_activity_home_shop_orderdetail);
        initView();
    }

    private void initView(){
        tv_shop_owner=(TextView) findViewById(R.id.tv_shop_owner);
        tv_total_num=(TextView) findViewById(R.id.tv_total_num);
        tv_price=(TextView) findViewById(R.id.tv_price);
        tv_name_newsdegree= (TextView) findViewById(R.id.tv_name_newsdegree);
        tv_buy_num= (TextView) findViewById(R.id.tv_buy_num);
        tv_freight_info= (TextView) findViewById(R.id.tv_freight_info);
        tv_freight=(TextView) findViewById(R.id.tv_freight);
        tv_total_fee= (TextView) findViewById(R.id.tv_total_fee);
        tv_total_info= (TextView) findViewById(R.id.tv_total_info);
        tv_add= (TextView) findViewById(R.id.tv_add);
        tv_add.setOnClickListener(this);
        tv_add_address= (TextView) findViewById(R.id.tv_add_address);
        ll_add_address=(LinearLayout) findViewById(R.id.ll_add_address);
        ll_add_address.setOnClickListener(this);
        initDataView();
    }

    private void initDataView(){
        mShopDetail=(ShopDetailTypeModel.ShopDetailType)
                getIntent().getSerializableExtra("orderDetail");
        if(null!=mShopDetail){
            tv_shop_owner.setText(mShopDetail.nickname);
            tv_total_num.setText("总共"+mShopDetail.num+"件商品");
            tv_name_newsdegree.setText(mShopDetail.title+"("+mShopDetail.isnew+")");
            tv_buy_num.setText("X"+mShopDetail.num);
        }
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.ll_add_address:
                Intent addAddress=new Intent(HomeShopOderDetailActivity.this,
                        UserAddressListActivity.class);
                startActivityForResult(addAddress,REQUEST_ADD_ADDRESS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            switch (requestCode){
                case REQUEST_ADD_ADDRESS:
                    cityName=data.getStringExtra("city");
                    addressName=data.getStringExtra("address");
                    id=data.getStringExtra("id");
                    telphone=data.getStringExtra("telphone");
                    revcname=data.getStringExtra("revcname");
                    tv_add_address.setText(cityName+" "+addressName);
                    break;
            }
        }
    }
}
