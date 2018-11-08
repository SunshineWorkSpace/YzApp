package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yingshixiezuovip.yingshi.adapter.ShopFragmentPagerAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.fragment.ShopFragment;
import com.yingshixiezuovip.yingshi.fragment.ShopTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuhua.gou on 2018/11/6.
 */

public class HomeShopMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop);
        setActionBarVisibility(View.GONE);
        initView(savedInstanceState);
    }

    public void initView(Bundle savedInstanceState){
        if (savedInstanceState == null && getIntent() != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ShopFragment kc = new ShopFragment();
            kc.setArguments(getIntent().getExtras());
            ft.add(R.id.fragment_shop, kc);
            ft.commitAllowingStateLoss();
        }
    }

}
