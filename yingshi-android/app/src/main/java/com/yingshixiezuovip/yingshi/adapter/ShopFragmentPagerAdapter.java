package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by decheng.yang on 2017/10/13.
 * 动态刷新fragment数据
 */

public class ShopFragmentPagerAdapter extends FragmentPagerAdapter {


    private String[] titles;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Context context;

    private FragmentManager fm;


    public ShopFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList,
                                    String[] titles,
                                    Context context) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
        this.titles = titles;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }




    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        if (position == 0)
            removeFragment(container, position);
        return super.instantiateItem(container, position);
    }

    private void removeFragment(ViewGroup container, int index) {
        String tag = getFragmentTag(container.getId(), index);
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null)
            return;
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        //ft.commit();
        ft.commitAllowingStateLoss();
        ft = null;
        fm.executePendingTransactions();
    }

    private String getFragmentTag(int viewId, int index) {
        try {
            Class<FragmentPagerAdapter> cls = android.support.v4.app.FragmentPagerAdapter.class;
            Class<?>[] parameterTypes = {int.class, long.class};
            Method method = cls.getDeclaredMethod("makeFragmentName",
                    parameterTypes);
            method.setAccessible(true);
            String tag = (String) method.invoke(this, viewId, index);
            return tag;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
