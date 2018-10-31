package com.yingshixiezuovip.yingshi.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.UserWorks;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 2017/9/14.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CoverIndexFragment extends BaseFragment {
    private UserWorks mUserWorks;
    private int mPagePosition = -1;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BaseFragment mContent;
    private List<BaseFragment> baseFragments;
    private int mUserid;


    public static CoverIndexFragment getInstance(UserWorks userWorks, int userid) {
        CoverIndexFragment coverFragment = new CoverIndexFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user_works", userWorks);
        bundle.putInt("userid", userid);
        coverFragment.setArguments(bundle);
        return coverFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_cover_userinfo_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        mUserWorks = (UserWorks) getArguments().getSerializable("user_works");
        mUserid = getArguments().getInt("userid");

        initView();
    }

    private void initView() {
        PictureManager.displayHead(mUserWorks.head, (ImageView) findViewById(R.id.cover_iv_head));
        ((TextView) findViewById(R.id.cover_tv_nickname)).setText(mUserWorks.nickname);
        ((TextView) findViewById(R.id.cover_btn_follow)).setText(mUserWorks.isfollow == 1 ? "已关注" : "+关注");
        mUserInfo = SPUtils.getUserInfo(getContext());
        findViewById(R.id.cover_btn_follow).setVisibility(mUserid == mUserInfo.id ? View.GONE : View.VISIBLE);

        findViewById(R.id.userinfo_btn_works).setOnClickListener(this);
        findViewById(R.id.userinfo_btn_info).setOnClickListener(this);
        findViewById(R.id.cover_btn_follow).setOnClickListener(this);

        baseFragments = new ArrayList<>();
        baseFragments.add(CoverWorksFragment.getInstance(mUserWorks.id));
        baseFragments.add(CoverInfoFragment.getInstance(mUserWorks));


        fragmentManager = getFragmentManager();
        switchIndicator(0);
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.userinfo_btn_works:
                switchIndicator(0);
                break;
            case R.id.userinfo_btn_info:
                switchIndicator(1);
                break;
            case R.id.cover_btn_follow:
                mUserWorks.isfollow = mUserWorks.isfollow == 1 ? 0 : 1;
                ((TextView) findViewById(R.id.cover_btn_follow)).setText(mUserWorks.isfollow == 1 ? "已关注" : "+关注");
                onFollowClick(mUserWorks.id, mUserWorks.isfollow);
                break;
        }
    }

    private void switchIndicator(int index) {
        if (mPagePosition == index) {
            return;
        }
        mPagePosition = index;
        int indicatorId;
        for (int i = 0; i < 2; i++) {
            indicatorId = getResources().getIdentifier("view_indicator_" + i, "id", getContext().getPackageName());
            findViewById(indicatorId).setVisibility(index == i ? View.VISIBLE : View.INVISIBLE);
        }
        switchFragment(baseFragments.get(index));
    }

    public void onFollowClick(int userid, int follow) {
        HashMap localHashMap = new HashMap();
        localHashMap.put("followid", userid);
        localHashMap.put("token", mUserInfo.token);
        HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_HOME_FOLLOW : TaskType.TASK_TYPE_HOME_CLEAR_FOLLOW, localHashMap, this);
    }

    private void switchFragment(BaseFragment fragment) {
        if (mContent != fragment) {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (mContent != null) {
                fragmentTransaction.hide(mContent);
            }
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment).commit();
            } else {
                fragmentTransaction.add(R.id.userinfo_mainlayout, fragment).commit();
            }
            mContent = fragment;
        }
    }
}
