package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.yingshixiezuovip.yingshi.adapter.ReviewAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnSimpleAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.ReviewModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class MainDetailsCommentActivity extends BaseActivity {
    public static final int TYPE_LIST = 1, TYPE_DETAILS = 2; //1、留言列表，2、留言详情
    private int mType;
    private int mItemID;
    private int mItemRID;
    private String mCommentAuthorName;
    private boolean isMore = false;
    private int mPage = 0;
    private MBaseAdapter mBaseAdapter;

    private PullToRefreshListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_details_comment);

        mType = getIntent().getIntExtra("item_type", -1);
        mItemID = getIntent().getIntExtra("item_id", -1);
        mItemRID = getIntent().getIntExtra("item_rid", -1);
        mCommentAuthorName = getIntent().getStringExtra("item_name");
        if (mItemID < 0 || mType < 0) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        initView();
    }

    private void initView() {
        setActivityTitle(mType == TYPE_LIST ? "留言列表" : "评论回复");
        findViewById(R.id.review_btn_submit).setOnClickListener(this);
        mListView = (PullToRefreshListView) findViewById(R.id.common_listview);
        mListView.setShowIndicator(false);
        ((EditText) findViewById(R.id.review_et_input)).setHint(mType == TYPE_LIST ? "对作品回复..." : "对 " + mCommentAuthorName + " 用户回复...");
        mListView.setOnRefreshListener(onRefreshListener);
        mBaseAdapter = new ReviewAdapter(this);
        mBaseAdapter.setOnAdapterClickListener(new OnSimpleAdapterClickListener() {
            @Override
            public void onHeadClick(int userid, String username) {
                Intent intent = new Intent(MainDetailsCommentActivity.this, UserInfo2Activity.class);
                intent.putExtra("user_id", userid);
                intent.putExtra("user_name", username);
                startActivity(intent);
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainDetailsCommentActivity.this, MainDetailsCommentActivity.class);
                ReviewModel.ReviewItem reviewItem = (ReviewModel.ReviewItem) mBaseAdapter.getItem(position);
                intent.putExtra("item_id", mItemID);
                intent.putExtra("item_rid", reviewItem.rid);
                intent.putExtra("item_type", MainDetailsCommentActivity.TYPE_DETAILS);
                intent.putExtra("item_name", reviewItem.nickname);
                startActivity(intent);
            }

            @Override
            public void onFollowClick(int userid, int follow) {
                HashMap localHashMap = new HashMap();
                localHashMap.put("rid", userid);
                localHashMap.put("token", mUserInfo.token);
                HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_REVIEW_ZAN : TaskType.TASK_TYPE_REVIEW_CANCELZAN, localHashMap, MainDetailsCommentActivity.this);
            }
        });
        mListView.setAdapter(mBaseAdapter);
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
            SPUtils.saveRefreshTime(MainDetailsCommentActivity.this, "comment_" + mType + "_" + mItemID, currentTime);
            mPage = 0;
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            loadData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainDetailsCommentActivity.this, "comment_" + mType + "_" + mItemID, currentTime);
            mPage++;
            isMore = true;
            loadData();
        }
    };

    private void loadData() {
        mListView.setMode(mType == TYPE_LIST ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
        HashMap<String, Object> commParams = new HashMap<>();
        commParams.put("token", mUserInfo.token);
        commParams.put("start", mPage);
        commParams.put(mType == TYPE_LIST ? "id" : "rid", mType == TYPE_LIST ? mItemID : mItemRID);
        HttpUtils.doPost(mType == TYPE_LIST ? TaskType.TASK_TYPE_QRY_COMMENT_LIST : TaskType.TASK_TYPE_QRY_COMMENT_DETAILS, commParams, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.review_btn_submit:
                doRecive();
                break;
        }
    }

    private void doRecive() {
        String reciveStr = ((EditText) findViewById(R.id.review_et_input)).getText().toString();
        if (TextUtils.isEmpty(reciveStr)) {
            showMessage("请输入回复内容");
            return;
        }
        HashMap<String, Object> commParams = new HashMap<>();
        commParams.put("token", mUserInfo.token);
        commParams.put("content", reciveStr);
        commParams.put("id", mItemID);
        commParams.put("rid", mItemRID);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(mType == TYPE_LIST ? TaskType.TASK_TYPE_DO_REVIEW : TaskType.TASK_TYPE_DO_REPLY, commParams, this);
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
            case TASK_TYPE_QRY_COMMENT_LIST:
                ReviewModel mReviewModel = GsonUtil.fromJson(result.toString(), ReviewModel.class);
                if (mReviewModel != null) {
                    if (!isMore) {
                        mBaseAdapter.setDatas(mReviewModel.data);
                    } else {
                        isMore = false;
                        mBaseAdapter.addDatas(mReviewModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_QRY_COMMENT_DETAILS:
                ReviewModel.ReviewItem reviewItem = GsonUtil.fromJson(((JSONObject) result).optString("data"), ReviewModel.ReviewItem.class);
                if (reviewItem != null) {
                    mBaseAdapter.setData(reviewItem);
                    findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_DO_REVIEW:
            case TASK_TYPE_DO_REPLY:
                showMessage("回复成功");
                ((EditText) findViewById(R.id.review_et_input)).setText(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setRefreshing();
                    }
                }, 100L);
                break;
        }
    }
}
