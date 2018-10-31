package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.adapter.CommentListAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.custom.CommentWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnSimpleAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.CommentModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/5/5.
 */

public class MainCommentActivity extends BaseActivity {
    public static final int TYPE_COMMON = 1, TYPE_NOTICE = 2;
    private int mType;
    private boolean isMore = false;
    private HashMap<Integer, String> mCommentErrorMsg;
    private CommentListAdapter mCommentListAdapter;
    private CommentModel mCommentModel;
    private CommentWindow mCommentWindow;
    private int mListID;
    private PullToRefreshListView mListView;
    private int mPage = 0;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.view_pulltorefreshlistview_layout);
        mListID = getIntent().getIntExtra("list_id", -1);
        mType = getIntent().getIntExtra("item_type", TYPE_COMMON);
        if (mType == TYPE_COMMON) {
            setActivityTitle(R.string.title_activity_main_comment);
            findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        } else {
            setActivityTitle("背书");
            findViewById(R.id.right_btn_submit).setVisibility(View.GONE);
        }
        initView();
        initWindow();
        initMap();
    }

    private void initMap() {
        mCommentErrorMsg = new HashMap();
        mCommentErrorMsg.put(Integer.valueOf(202), "已经背过书了亲");
        mCommentErrorMsg.put(Integer.valueOf(400), "参数错误");
        mCommentErrorMsg.put(Integer.valueOf(401), "亲，请先登录再操作");
        mCommentErrorMsg.put(Integer.valueOf(502), "点赞失败，请稍后重试");
        mCommentErrorMsg.put(Integer.valueOf(500), "服务器异常，请稍后重试");
    }

    private PullToRefreshBase.OnRefreshListener2 onRefreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainCommentActivity.this, "comment_" + mListID, currentTime);
            mPage = 0;
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            loadData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainCommentActivity.this, "comment_" + mListID, currentTime);
            mPage++;
            isMore = true;
            mListView.setMode(PullToRefreshBase.Mode.BOTH);
            loadData();
        }
    };

    private void initView() {
        mListView = ((PullToRefreshListView) findViewById(R.id.common_listview));
        mListView.setOnRefreshListener(onRefreshListener);
        mListView.setShowIndicator(false);
        findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("背书");
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(SPUtils.getRefreshTime(this, new StringBuilder().append("comment_").append(mListID).toString())));
        mCommentListAdapter = new CommentListAdapter(this, mType);
        mCommentListAdapter.setOnAdapterClickListener(new OnSimpleAdapterClickListener() {
            @Override
            public void onHeadClick(int userid, String username) {
                Intent intent = new Intent(MainCommentActivity.this, UserInfo2Activity.class);
                intent.putExtra("user_id", userid);
                intent.putExtra("user_name", username);
                startActivity(intent);
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainCommentActivity.this, MainCommentActivity.class);
                intent.putExtra("list_id", position);
                startActivity(intent);
            }
        });
        mListView.setAdapter(mCommentListAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setRefreshing();
            }
        }, 100L);
    }

    private void initWindow() {
        mCommentWindow = new CommentWindow(this);
        mCommentWindow.setOnCommentClickListener(new CommentWindow.OnCommentClickListener() {
            public void onCommentClick(String paramAnonymousString) {
                HashMap localHashMap = new HashMap();
                localHashMap.put("token", mUserInfo.token);
                localHashMap.put("id", Integer.valueOf(mListID));
                localHashMap.put("content", paramAnonymousString);
                mLoadWindow.show(R.string.text_submiting);
                HttpUtils.doPost(TaskType.TASK_TYPE_DO_COMMENT, localHashMap, MainCommentActivity.this);
            }
        });
    }

    private void loadData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        HashMap localHashMap = new HashMap();
        localHashMap.put("start", Integer.valueOf(mPage));
        if (mType == TYPE_COMMON) {
            localHashMap.put("id", Integer.valueOf(mListID));
            HttpUtils.doPost(TaskType.TASK_TYPE_COMMENT_LIST, localHashMap, this);
        } else {
            localHashMap.put("token", mUserInfo.token);
            HttpUtils.doPost(TaskType.TASK_TYPE_MSG_COMMENT_LIST, localHashMap, this);
        }
    }


    protected void onSingleClick(View paramView) {
        super.onSingleClick(paramView);
        switch (paramView.getId()) {
            case R.id.right_btn_submit:
                mCommentWindow.show();
                break;
            default:
                return;
        }
    }

    public void taskFinished(TaskType taskType, Object result, boolean paramBoolean) {
        mLoadWindow.cancel();
        mListView.onRefreshComplete();
        mCommentWindow.cancel();
        if ((result instanceof Throwable)) {
            if (((result instanceof BaseThrowable)) && (mCommentErrorMsg.containsKey(((BaseThrowable) result).getErrorCode()))) {
                showMessage(mCommentErrorMsg.get(((BaseThrowable) result).getErrorCode()));
                return;
            }
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (taskType) {
            case TASK_TYPE_MSG_COMMENT_LIST:
            case TASK_TYPE_COMMENT_LIST:
                mCommentModel = GsonUtil.fromJson(result.toString(), CommentModel.class);
                if (mCommentModel != null) {
                    if (!isMore) {
                        mCommentListAdapter.setDatas(mCommentModel.data);
                    } else {
                        isMore = false;
                        mCommentListAdapter.addDatas(mCommentModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mCommentListAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);
                } else {
                    showMessage("数据加载异常");
                }
                return;
            case TASK_TYPE_DO_COMMENT:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setRefreshing();
                    }
                }, 100L);
                break;
            default:
                return;
        }
    }
}