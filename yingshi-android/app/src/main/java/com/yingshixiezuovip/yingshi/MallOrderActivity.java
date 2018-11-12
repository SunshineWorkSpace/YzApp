package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.yingshixiezuovip.yingshi.base.BaseActivity;

/**
 * 类名称:MallOrderActivity
 * 类描述:商城交易订单
 * 创建时间: 2018-11-11-22:56
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class MallOrderActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_order);
        setActivityTitle("订单信息");
        ((LinearLayout)findViewById(R.id.lin_buy)).setOnClickListener(this);
        ((LinearLayout)findViewById(R.id.lin_sell)).setOnClickListener(this);

    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        Intent intent =null;
        switch (v.getId()){
            case R.id.lin_buy:
                intent=new Intent(this,MallOrderBuyDetailActiviity.class);
                break;
            case R.id.lin_sell:
                break;
        }
        if(null!=intent){
            startActivity(intent);
        }
    }
}
