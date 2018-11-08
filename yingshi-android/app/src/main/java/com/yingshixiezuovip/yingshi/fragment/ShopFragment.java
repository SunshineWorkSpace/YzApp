package com.yingshixiezuovip.yingshi.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.ShopFragmentPagerAdapter;
import com.yingshixiezuovip.yingshi.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuhua.gou on 2018/11/7.
 */

public class ShopFragment extends BaseFragment implements TabLayout.OnTabSelectedListener,View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] titles=new String[]{"关注","逛逛","全新","精选"};
    private List<Fragment> fragmentList;
    private ShopFragmentPagerAdapter pageAdapter;
    private LinearLayout ll_back_shop;
    private int  currentPosition=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_shop, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    public void initView(){
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        ll_back_shop=(LinearLayout) findViewById(R.id.ll_back_shop);
        ll_back_shop.setOnClickListener(this);
        initTab();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.ll_back_shop:
                getActivity().finish();
                break;
        }
    }

    private void initTab(){
        fragmentList=getChildFragmentList();
        viewPager.setOffscreenPageLimit(1);
        pageAdapter = new ShopFragmentPagerAdapter(this.getChildFragmentManager(),
                fragmentList, titles, getActivity());
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setTabView();
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(currentPosition);
        tabLayout.getTabAt(currentPosition).getCustomView().setSelected(true);

    }
    private List<Fragment> getChildFragmentList() {
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            String type="1";
            if("关注".equals(titles[i])){
                type="1";
            }else if("逛逛".equals(titles[i])){
                type="2";
            }else if("全新".equals(titles[i])){
                type="3";
            }else if("精选".equals(titles[i])){
                type="4";
            }
            Bundle bundle = new Bundle();
            ShopTabFragment featuredFragment = new ShopTabFragment();
            bundle.putString("type", type);
            featuredFragment.setArguments(bundle);
            fragmentList.add(featuredFragment);

        }
        return fragmentList;
    }
    public void setTabView(){
        for (int i = 0; i<tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
    }

    public View getTabView(int position){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tab_mall_item, null);

        view.setLayoutParams(new LinearLayout.LayoutParams(
                100,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        TextView tv_tab_day=(TextView) view.findViewById(R.id.tv_tab_mall);
        tv_tab_day.setText(titles[position]);

        return view;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        currentPosition = tab.getPosition();
        viewPager.setCurrentItem(currentPosition);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
