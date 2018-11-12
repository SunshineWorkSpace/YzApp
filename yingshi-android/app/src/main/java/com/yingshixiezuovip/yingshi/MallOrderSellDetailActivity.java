package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.fragment.MallOrderBuyFragment;
import com.yingshixiezuovip.yingshi.fragment.MallOrderSellFragment;

/**
 * 类名称:MallOrderSellDetailActivity
 * 类描述:商家订单信息
 * 创建时间: 2018-11-12-18:18
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class MallOrderSellDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mallbuy);
        setActivityTitle("商家订单中心");

        if (savedInstanceState == null && getIntent() != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            MallOrderSellFragment kc = new MallOrderSellFragment();
            kc.setArguments(getIntent().getExtras());
            ft.add(R.id.fragment_shop, kc);
            ft.commitAllowingStateLoss();
        }
    }
}
