package com.yingshixiezuovip.yingshi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yingshixiezuovip.yingshi.MainCommentActivity;
import com.yingshixiezuovip.yingshi.MainDetailsActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.UserInfo2Activity;
import com.yingshixiezuovip.yingshi.adapter.HomeListAdapter;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/5/3.
 */

public class DepartFragment extends BaseFragment implements OnAdapterClickListener {
    private PullToRefreshListView mListView;
    private int mPage = 0;
    private boolean isMore = false;
    private HomeListAdapter mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_depart, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mListView = ((PullToRefreshListView) findViewById(R.id.common_listview));
        mListView.setOnRefreshListener(onRefreshListener);
        mListView.setShowIndicator(false);
        mListAdapter = new HomeListAdapter(getActivity(), 2);
        mListAdapter.setOnAdapterClickListener(this);
        mListView.setAdapter(mListAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setRefreshing();
            }
        }, 100L);
    }

    private PullToRefreshBase.OnRefreshListener2 onRefreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(getActivity(), "depart", currentTime);
            mPage = 0;
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            loadData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(getActivity(), "depart", currentTime);
            mPage++;
            isMore = true;
            loadData();
        }
    };

    private void loadData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("tid", 22);
        params.put("start", mPage);
        params.put("qb", 1);
        HttpUtils.doPost(TaskType.TASK_TYPE_HOME_LIST, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mListView.onRefreshComplete();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_HOME_LIST:
                HomeListModel homeListModel = GsonUtil.fromJson(result.toString(), HomeListModel.class);
                if (homeListModel != null) {
                    if (!isMore) {
                        mListAdapter.setDatas(homeListModel.data);
                    } else {
                        isMore = false;
                        mListAdapter.addDatas(homeListModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mListAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_HOME_CLEAR_FOLLOW:
            case TASK_TYPE_HOME_FOLLOW:
                break;
        }
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
        HomeListModel.HomeListItem listItem = mListAdapter.getItem(position);
        intent.putExtra("item_id", listItem.id);
        intent.putExtra("item_name", listItem.typename);
        startActivity(intent);
    }

    @Override
    public void onBookClick(int id) {
    }

    @Override
    public void onHeadClick(int userid, String username) {
        Intent intent = new Intent(getActivity(), UserInfo2Activity.class);
        intent.putExtra("user_id", userid);
        intent.putExtra("user_name", username);
        startActivity(intent);
    }
}
