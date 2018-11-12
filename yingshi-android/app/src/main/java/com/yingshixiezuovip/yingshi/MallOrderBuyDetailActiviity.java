package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.fragment.MallOrderBuyFragment;
import com.yingshixiezuovip.yingshi.fragment.ShopFragment;

/**
 * 类名称:MallOrderBuyDetailActiviity
 * 类描述:买家订单
 * 创建时间: 2018-11-11-23:31
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class MallOrderBuyDetailActiviity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mallbuy);
        setActivityTitle("买家订单信息");

        if (savedInstanceState == null && getIntent() != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            MallOrderBuyFragment kc = new MallOrderBuyFragment();
            kc.setArguments(getIntent().getExtras());
            ft.add(R.id.fragment_shop, kc);
            ft.commitAllowingStateLoss();
        }
    }

}
