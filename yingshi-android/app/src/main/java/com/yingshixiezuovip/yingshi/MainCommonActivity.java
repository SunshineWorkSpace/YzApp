package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.yingshixiezuovip.yingshi.adapter.BuyerAdapter;
import com.yingshixiezuovip.yingshi.adapter.CommVideoAdapter;
import com.yingshixiezuovip.yingshi.adapter.FollowAdapter;
import com.yingshixiezuovip.yingshi.adapter.HomeListAdapter;
import com.yingshixiezuovip.yingshi.adapter.MineCommentAdapter;
import com.yingshixiezuovip.yingshi.adapter.MineWorkAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.custom.VideoModel;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.BuyerModel;
import com.yingshixiezuovip.yingshi.model.CommentModel;
import com.yingshixiezuovip.yingshi.model.FollowModel;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayer;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;

import java.util.HashMap;


/**
 * Created by Resmic on 2017/5/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class MainCommonActivity extends BaseActivity implements OnAdapterClickListener {
    public final static int TYPE_HOME = 2, TYPE_VIDEO = 3, TYPE_WORKS = 4, TYPE_SELLER_ORDER = 5, TYPE_BUYER_ORDER = 6,
            TYPE_FOLLOW = 7, TYPE_MINE_WORK = 8, TYPE_MINE_COMMENT = 9, TYPE_MINE_FOLLOW = 10;
    private int mType = TYPE_HOME;
    private int mTid;
    private int mUserId;
    private String mTitle;
    private PullToRefreshListView mListView;
    private MBaseAdapter mBaseAdapter;
    private boolean isMore;
    private int mPage = 0;
    private ShareWindow mShareWindow;
    private ShareModel.ShareItem mShareItem;
    private boolean showBottom = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pulltorefreshlistview_layout);

        Intent intent = getIntent();
        mType = intent.getIntExtra("item_type", -1);
        mTid = intent.getIntExtra("item_tid", -1);
        mTitle = intent.getStringExtra("item_title");
        mUserId = intent.getIntExtra("user_id", -1);
        showBottom = intent.getBooleanExtra("show_bottom", true);

        setActivityTitle(mTitle);
        initView();
    }

    private PullToRefreshBase.OnRefreshListener2 onRefreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainCommonActivity.this, "common_" + mType + "_" + mTid, currentTime);
            mPage = 0;
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            if (mType == TYPE_WORKS || mType == TYPE_FOLLOW) {
                loadWorksData();
            } else {
                loadData();
            }
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainCommonActivity.this, "common_" + mType + "_" + mTid, currentTime);
            mPage++;
            isMore = true;
            if (mType == TYPE_WORKS || mType == TYPE_FOLLOW) {
                loadWorksData();
            } else {
                loadData();
            }
        }
    };

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.common_listview);
        mListView.setShowIndicator(false);
        mListView.setOnRefreshListener(onRefreshListener);
        mShareWindow = new ShareWindow(this);

        if (mType == TYPE_VIDEO) {
            mBaseAdapter = new CommVideoAdapter(this);
            mListView.setAdapter(mBaseAdapter);
        } else if (mType == TYPE_BUYER_ORDER || mType == TYPE_SELLER_ORDER) {
            mBaseAdapter = new BuyerAdapter(this, mType);
        } else if (mType == TYPE_FOLLOW || mType == TYPE_MINE_FOLLOW) {
            mBaseAdapter = new FollowAdapter(this);
        } else if (mType == TYPE_MINE_WORK) {
            mShareItem = new ShareModel.ShareItem();
            mShareItem.photo = mUserInfo.share_samplereels_photo;
            mShareItem.title = mUserInfo.share_samplereels_title;
            mShareItem.url = mUserInfo.share_samplereels_url;
            mShareItem.content = mUserInfo.share_samplereels_content;

            findViewById(R.id.right_btn_name).setVisibility(View.GONE);
            findViewById(R.id.right_iv_more).setVisibility(View.VISIBLE);
            findViewById(R.id.right_btn_submit).setVisibility(mTitle.equals("作品集")?View.GONE:View.VISIBLE);

            mBaseAdapter = new MineWorkAdapter(this);
        } else if (mType == TYPE_MINE_COMMENT) {
            mBaseAdapter = new MineCommentAdapter(this);
        } else {
            mBaseAdapter = new HomeListAdapter(this, 2);
        }
        if (mBaseAdapter != null) {
            mBaseAdapter.setOnAdapterClickListener(this);
            mListView.setAdapter(mBaseAdapter);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setRefreshing();
            }
        }, 100L);
    }

    private void loadData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        findViewById(R.id.notdata_layout).setVisibility(View.GONE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("start", mPage);
        params.put("qb", 1);
        if (mType == TYPE_HOME) {
            params.put("tid", mTid);
            HttpUtils.doPost(TaskType.TASK_TYPE_HOME_LIST, params, this);
        } else if (mType == TYPE_BUYER_ORDER) {
            HttpUtils.doPost(TaskType.TASK_TYPE_MSG_BUYER_ORDER, params, this);
        } else if (mType == TYPE_SELLER_ORDER) {
            HttpUtils.doPost(TaskType.TASK_TYPE_MSG_SELLER_ORDER, params, this);
        } else if (mType == TYPE_MINE_WORK) {
            HttpUtils.doPost(TaskType.TASK_TYPE_MINE_WORK_LIST, params, this);
        } else if (mType == TYPE_MINE_COMMENT) {
            HttpUtils.doPost(TaskType.TASK_TYPE_MINE_COMMENT_LIST, params, this);
        } else if (mType == TYPE_MINE_FOLLOW) {
            HttpUtils.doPost(TaskType.TASK_TYPE_MINE_FOLLOW_LIST, params, this);
        } else {
            HttpUtils.doPost(TaskType.TASK_TYPE_QRY_HEART_VIDEO, params, this);
        }
    }

    private void loadWorksData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        if (mType == TYPE_WORKS) {
            params.put("userid", mUserId);
        }
        params.put("start", mPage);
        HttpUtils.doPost(mType == TYPE_WORKS ? TaskType.TASK_TYPE_QRY_OTHER_WORKS : TaskType.TASK_TYPE_MSG_FANS_LIST, params, this);
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
            case TASK_TYPE_HOME_LIST:
            case TASK_TYPE_QRY_OTHER_WORKS:
            case TASK_TYPE_MINE_WORK_LIST:
                HomeListModel mHomeListModel = GsonUtil.fromJson(result.toString(), HomeListModel.class);
                if (mHomeListModel != null) {
                    if (!isMore) {
                        mBaseAdapter.setDatas(mHomeListModel.data);
                    } else {
                        isMore = false;
                        mBaseAdapter.addDatas(mHomeListModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_QRY_HEART_VIDEO:
                VideoModel mVideoModel = GsonUtil.fromJson(result.toString(), VideoModel.class);
                if (mVideoModel != null) {
                    if (!isMore) {
                        mBaseAdapter.setDatas(mVideoModel.data);
                    } else {
                        isMore = false;
                        mBaseAdapter.addDatas(mVideoModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_MSG_SELLER_ORDER:
            case TASK_TYPE_MSG_BUYER_ORDER:
                BuyerModel mBuyerModel = GsonUtil.fromJson(result.toString(), BuyerModel.class);
                if (mBuyerModel != null) {
                    if (!isMore) {
                        mBaseAdapter.setDatas(mBuyerModel.data);
                    } else {
                        isMore = false;
                        mBaseAdapter.addDatas(mBuyerModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_MSG_FANS_LIST:
            case TASK_TYPE_MINE_FOLLOW_LIST:
                FollowModel mFollowModel = GsonUtil.fromJson(result.toString(), FollowModel.class);
                if (mFollowModel != null && mFollowModel.data != null) {
                    if (isMore) {
                        isMore = false;
                        mBaseAdapter.addDatas(mFollowModel.data);
                    } else {
                        mBaseAdapter.setDatas(mFollowModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_MINE_COMMENT_LIST:
                CommentModel commentModel = GsonUtil.fromJson(result.toString(), CommentModel.class);
                if (commentModel != null && commentModel.data != null) {
                    if (isMore) {
                        isMore = false;
                        mBaseAdapter.addDatas(commentModel.data);
                    } else {
                        mBaseAdapter.setDatas(commentModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mBaseAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
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
        Intent intent = new Intent(this, MainCommentActivity.class);
        intent.putExtra("list_id", id);
        startActivity(intent);
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn_submit:
                if (mShareItem != null) {
                    mShareWindow.show(mShareItem, this);
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = null;
        if (mType == TYPE_VIDEO) {
            intent = new Intent(this, MainCommonActivity.class);
            VideoModel.VideoItem videoItem = (VideoModel.VideoItem) mBaseAdapter.getItem(position);
            intent.putExtra("item_type", MainCommonActivity.TYPE_WORKS);
            intent.putExtra("user_id", videoItem.userid);
            intent.putExtra("item_title", "作品页");
        } else if (mType == TYPE_SELLER_ORDER || mType == TYPE_BUYER_ORDER) {
            BuyerModel.BuyerItem buyerItem = (BuyerModel.BuyerItem) mBaseAdapter.getItem(position);
            if (buyerItem.status > 4) {
                return;
            }
            if (mType == TYPE_BUYER_ORDER && buyerItem.status == 3) {
                intent = new Intent(this, MainAuthenticationMoneyActivity.class);
            } else {
                intent = new Intent(this, OrderDetailsActivity.class);
                intent.putExtra("item_user_type", mType == TYPE_SELLER_ORDER ? OrderDetailsActivity.TYPE_USER_SELLER : OrderDetailsActivity.TYPE_USER_BUYER);
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("item_data", buyerItem);
            intent.putExtras(bundle);
        } else if (mType == TYPE_MINE_COMMENT) {
            intent = new Intent(this, MainCommentActivity.class);
            CommentModel.CommentItem commentItem = (CommentModel.CommentItem) mBaseAdapter.getItem(position);
            intent.putExtra("list_id", commentItem.id);
        } else {
            intent = new Intent(this, MainDetailsActivity.class);
            HomeListModel.HomeListItem listItem = (HomeListModel.HomeListItem) mBaseAdapter.getItem(position);
            intent.putExtra("item_id", listItem.id);
            intent.putExtra("item_name", listItem.typename);
            intent.putExtra("buttom_show", mType != TYPE_MINE_WORK && showBottom);
        }
        if (intent != null) {
            startActivityForResult(intent, 1000);
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

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_CLOSE_DETAILS:
            case EVENT_TYPE_REFRESH_PRICE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setRefreshing();
                    }
                }, 100L);
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.setRefreshing();
                }
            }, 100L);
        }
    }
}
