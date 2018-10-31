package com.yingshixiezuovip.yingshi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.yingshixiezuovip.yingshi.MainCommentActivity;
import com.yingshixiezuovip.yingshi.MainDetailsActivity;
import com.yingshixiezuovip.yingshi.MainDetailsBookActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.WorksListAdapter;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by Resmic on 2017/9/14.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CoverWorksFragment extends BaseFragment implements OnAdapterClickListener {
    private PullToRefreshListView mListView;
    private int mPage = 0;
    private boolean isMore = false;
    private WorksListAdapter mWorksListAdapter;
    private int mUserid;

    public static CoverWorksFragment getInstance(int userid) {
        CoverWorksFragment worksFragment = new CoverWorksFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userid", userid);
        worksFragment.setArguments(bundle);
        return worksFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.view_pulltorefreshlistview_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserid = getArguments().getInt("userid");
        mUserInfo = SPUtils.getUserInfo(getContext());
        initView();
    }


    private void initView() {
        mListView = ((PullToRefreshListView) findViewById(R.id.common_listview));
        mListView.setOnRefreshListener(onRefreshListener);
        mListView.setShowIndicator(false);
        mWorksListAdapter = new WorksListAdapter(getActivity());
        mWorksListAdapter.setOnAdapterClickListener(this);
        mListView.setAdapter(mWorksListAdapter);
        loadData();
    }

    private PullToRefreshBase.OnRefreshListener2 onRefreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            mListView.getLoadingLayoutProxy().setPullLabel("下拉,返回封面");
            mListView.getLoadingLayoutProxy().setRefreshingLabel("下拉,返回封面");
            mListView.getLoadingLayoutProxy().setReleaseLabel("下拉,返回封面");
            EventBus.getDefault().post(new BaseEvent(EventType.EVENT_TYPE_BACK_COVER));
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.onRefreshComplete();
                }
            }, 500);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(getActivity(), "works_user", currentTime);
            mPage++;
            isMore = true;
            loadData();
        }
    };

    private void loadData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("userid", mUserid);
        params.put("start", mPage);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_OTHER_WORKS, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mListView.onRefreshComplete();
        super.taskFinished(type, result, isHistory);
        switch (type) {
            case TASK_TYPE_QRY_OTHER_WORKS:
                HomeListModel mHomeListModel = GsonUtil.fromJson(result.toString(), HomeListModel.class);
                if (mHomeListModel != null) {
                    if (!isMore) {
                        mWorksListAdapter.setDatas(mHomeListModel.data);
                    } else {
                        isMore = false;
                        mWorksListAdapter.addDatas(mHomeListModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mWorksListAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
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
        HomeListModel.HomeListItem listItem = mWorksListAdapter.getItem(position);
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
    }

}
