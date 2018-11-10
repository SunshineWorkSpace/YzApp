package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
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
    private ShopDetailTypeModel.ShopDetailType mShopDetail;
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
}
