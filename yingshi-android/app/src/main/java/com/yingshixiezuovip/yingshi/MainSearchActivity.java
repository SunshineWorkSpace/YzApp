package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.adapter.HomeListAdapter;
import com.yingshixiezuovip.yingshi.adapter.SearchRecommendAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.HotModel;
import com.yingshixiezuovip.yingshi.model.RecommendModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;

import java.util.HashMap;

public class MainSearchActivity extends BaseActivity implements OnAdapterClickListener {
    private PullToRefreshListView mListView;
    private boolean isMore = false;
    private HotModel mHotModel;
    private int mPageNo = 1;
    private MBaseAdapter baseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search, -1, false);

        mHotModel = (HotModel) getIntent().getSerializableExtra("serial_params");
        initView();
    }

    private void initView() {
        ((EditText) findViewById(R.id.search_et_keyword)).setText(mHotModel.getKeywords());
        findViewById(R.id.search_btn_submit).setOnClickListener(this);
        findViewById(R.id.search_btn_back).setOnClickListener(this);

        mListView = (PullToRefreshListView) findViewById(R.id.common_listview);
        mListView.setShowIndicator(false);
        mListView.setOnRefreshListener(onRefreshListener);

        if (mHotModel.getType() == 1) {
            baseAdapter = new HomeListAdapter(this, 5);
        } else {
            baseAdapter = new SearchRecommendAdapter(this);
        }

        baseAdapter.setOnAdapterClickListener(this);

        mListView.setAdapter(baseAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setRefreshing();
            }
        }, 100);
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainSearchActivity.this, "search_" + mHotModel.getType(), currentTime);
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            mPageNo = 0;
            loadData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            isMore = true;
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainSearchActivity.this, "search_" + mHotModel.getType(), currentTime);
            mPageNo++;
            loadData();
        }
    };

    private void loadData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        HashMap<String, Object> searchParams = new HashMap<>();
        searchParams.put("token", mUserInfo.token);
        searchParams.put("start", mPageNo);
        searchParams.put("keywords", mHotModel.getKeywords());
        searchParams.put("type", mHotModel.getType());
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_HOT_SEARCH_DETAILS, searchParams, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.search_btn_back:
                onBackPressed();
                break;
            case R.id.search_btn_submit:
                mHotModel.setKeywords(((EditText) findViewById(R.id.search_et_keyword)).getText().toString());
                if (TextUtils.isEmpty(mHotModel.getKeywords())) {
                    return;
                }

                mPageNo = 0;

                mLoadWindow.show();
                loadData();
                break;

        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        mListView.onRefreshComplete();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_HOT_SEARCH_DETAILS:
                if (mHotModel.getType() == 1) {
                    HomeListModel mHomeListModel = GsonUtil.fromJson(result.toString(), HomeListModel.class);
                    if (mHomeListModel != null && mHomeListModel.data != null) {
                        if (!isMore) {
                            baseAdapter.setDatas(mHomeListModel.data);
                        } else {
                            isMore = false;
                            baseAdapter.addDatas(mHomeListModel.data);
                        }
                        findViewById(R.id.notdata_layout).setVisibility(baseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                    } else {
                        showMessage(R.string.data_load_failed);
                        findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                    }
                } else {
                    RecommendModel mRecommendModel = GsonUtil.fromJson(result.toString(), RecommendModel.class);
                    if (mRecommendModel != null && mRecommendModel.data != null) {
                        if (!isMore) {
                            baseAdapter.setDatas(mRecommendModel.data);
                        } else {
                            isMore = false;
                            baseAdapter.addDatas(mRecommendModel.data);
                        }
                        findViewById(R.id.notdata_layout).setVisibility(baseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                    } else {
                        showMessage(R.string.data_load_failed);
                        findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                    }
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
        Intent intent = new Intent(this, MainCommentActivity.class);
        intent.putExtra("list_id", id);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (baseAdapter instanceof HomeListAdapter) {
            Intent intent = new Intent(this, MainDetailsActivity.class);
            HomeListModel.HomeListItem listItem = (HomeListModel.HomeListItem) baseAdapter.getItem(position);
            intent.putExtra("item_id", listItem.id);
            intent.putExtra("item_name", listItem.typename);
            startActivity(intent);
        }
    }

    @Override
    public void onBookClick(int id) {
        Intent intent = new Intent(this, MainDetailsBookActivity.class);
        intent.putExtra("item_id", id);
        startActivity(intent);
    }

    @Override
    public void onHeadClick(int userid, String username) {
        Intent intent = new Intent(this, UserInfo2Activity.class);
        intent.putExtra("user_id", userid);
        intent.putExtra("user_name", username);
        startActivity(intent);
    }

}
