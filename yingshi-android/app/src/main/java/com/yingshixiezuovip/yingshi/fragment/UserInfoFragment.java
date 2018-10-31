package com.yingshixiezuovip.yingshi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.MainCommentActivity;
import com.yingshixiezuovip.yingshi.MainDetailsActivity;
import com.yingshixiezuovip.yingshi.MainDetailsBookActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.UserInfo2Activity;
import com.yingshixiezuovip.yingshi.adapter.FollowAdapter;
import com.yingshixiezuovip.yingshi.adapter.HomeListAdapter;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.FollowModel;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/5/8.
 */

public class UserInfoFragment extends BaseFragment implements OnAdapterClickListener {
    public final static int TYPE_PROFILE = 1, TYPE_WORKS = 2, TYPE_FOLLOW = 3;
    private UserInfo mOtherUserInfo;
    private int mType = TYPE_PROFILE;
    private PullToRefreshListView mListView;
    private boolean isMore = false;
    private int mPage = 0;
    private HomeListModel mWorksModel;
    private FollowModel mFollowModel;
    private MBaseAdapter mBaseAdapter;

    public static UserInfoFragment newInstance(int type, UserInfo userInfo) {
        UserInfoFragment webFragment = new UserInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putSerializable("userinfo", userInfo);
        webFragment.setArguments(bundle);
        return webFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_userinfo, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOtherUserInfo = (UserInfo) getArguments().getSerializable("userinfo");
        mType = getArguments().getInt("type");

        findViewById(R.id.fm_userinfo_profile_layout).setVisibility(mType == TYPE_PROFILE ? View.VISIBLE : View.GONE);
        findViewById(R.id.fm_userinfo_listview_layout).setVisibility(mType == TYPE_PROFILE ? View.GONE : View.VISIBLE);

        mListView = (PullToRefreshListView) findViewById(R.id.common_listview);
        mListView.setShowIndicator(false);
        mListView.setOnRefreshListener(onRefreshListener);
        if (mType == TYPE_PROFILE) {
            initProfileInfo();
        } else {
            if (mType == TYPE_WORKS) {
                mBaseAdapter = new HomeListAdapter(getActivity(), 3);
                mBaseAdapter.setOnAdapterClickListener(this);
            } else {
                mBaseAdapter = new FollowAdapter(getActivity());
                mBaseAdapter.setOnAdapterClickListener(this);
            }
            mListView.setAdapter(mBaseAdapter);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.setRefreshing(true);
                }
            }, 100L);
        }
    }

    private PullToRefreshBase.OnRefreshListener2 onRefreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            mPage = 0;
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            loadData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            mPage++;
            isMore = true;
            loadData();
        }
    };

    private void initProfileInfo() {
        ((TextView) findViewById(R.id.fmuserinf_tv_name)).setText(mOtherUserInfo.nickname);
        ((TextView) findViewById(R.id.fmuserinf_tv_sex)).setText(mOtherUserInfo.sex);
        ((TextView) findViewById(R.id.fmuserinf_tv_job)).setText(mOtherUserInfo.position);
        ((TextView) findViewById(R.id.fmuserinf_tv_birthday)).setText(mOtherUserInfo.birth);
        ((TextView) findViewById(R.id.fmuserinf_tv_city)).setText(mOtherUserInfo.city);
        ((TextView) findViewById(R.id.fmuserinf_tv_school)).setText(mOtherUserInfo.school);
    }

    private void loadData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("userid", mOtherUserInfo.id);
        params.put("start", mPage);
        HttpUtils.doPost(mType == TYPE_WORKS ? TaskType.TASK_TYPE_QRY_OTHER_WORKS : TaskType.TASK_TYPE_QRY_OTHER_FOLLOW_LIST, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mListView.onRefreshComplete();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_OTHER_WORKS:
                mWorksModel = GsonUtil.fromJson(result.toString(), HomeListModel.class);
                if (mWorksModel != null && mWorksModel.data != null) {
                    if (isMore) {
                        isMore = false;
                        mBaseAdapter.addDatas(mWorksModel.data);
                    } else {
                        mBaseAdapter.setDatas(mWorksModel.data);
                    }
                }
                break;
            case TASK_TYPE_QRY_OTHER_FOLLOW_LIST:
                mFollowModel = GsonUtil.fromJson(result.toString(), FollowModel.class);
                if (mFollowModel != null && mFollowModel.data != null) {
                    if (isMore) {
                        isMore = false;
                        mBaseAdapter.addDatas(mFollowModel.data);
                    } else {
                        mBaseAdapter.setDatas(mFollowModel.data);
                    }
                }
                break;
        }
        findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFollowClick(int userid, int follow) {
        HashMap localHashMap = new HashMap();
        localHashMap.put("followid", userid);
        localHashMap.put("token", mUserInfo.token);
        HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_HOME_FOLLOW : TaskType.TASK_TYPE_HOME_CLEAR_FOLLOW, localHashMap, this);
    }

    @Override
    public void onZanClick(int id) {
        Intent intent = new Intent(getActivity(), MainCommentActivity.class);
        intent.putExtra("list_id", id);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), MainDetailsActivity.class);
        HomeListModel.HomeListItem listItem = (HomeListModel.HomeListItem) mBaseAdapter.getItem(position);
        intent.putExtra("item_id", listItem.id);
        intent.putExtra("item_name", listItem.typename);
        startActivity(intent);
    }

    @Override
    public void onBookClick(int id) {
        Intent intent = new Intent(getActivity(), MainDetailsBookActivity.class);
        intent.putExtra("item_id", id);
        startActivity(intent);
    }

    @Override
    public void onHeadClick(int userid, String username) {
        Intent intent = new Intent(getActivity(), UserInfo2Activity.class);
        intent.putExtra("user_id", userid);
        intent.putExtra("user_name", username);
        startActivity(intent);
    }
}
