package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.yingshixiezuovip.yingshi.adapter.UserInfoAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.fragment.CoverFragment;
import com.yingshixiezuovip.yingshi.fragment.CoverIndexFragment;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.model.UserWorks;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayer;
import com.yingshixiezuovip.yingshi.quote.viewpager.VerticalViewPager;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfo2Activity extends BaseActivity {
    private VerticalViewPager mViewPager;
    private List<BaseFragment> baseFragments;
    private int mUserId;
    private String mUserName;
    private UserWorks mUserWorks;
    private ShareWindow mShareWindow;
    private ShareModel.ShareItem mShareItem;
    private boolean isPaySuc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info2);

        mUserId = getIntent().getIntExtra("user_id", -1);
        mUserName = getIntent().getStringExtra("user_name");

        initView();
        loadData();
    }


    private void initView() {
        mViewPager = (VerticalViewPager) findViewById(R.id.vertical_viewpager);
        if(!TextUtils.isEmpty(mUserName)) {
            setActivityTitle(mUserName + "的作品集");
        } else {
            setActivityTitle("加载中...");
        }

        findViewById(R.id.right_btn_name).setVisibility(View.GONE);
        findViewById(R.id.right_iv_more).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        mShareWindow = new ShareWindow(this);
    }

    private void loadData() {
        mLoadWindow.show(R.string.waiting);
        HashMap<String, Object> params = new HashMap<>();
        params.put("userid", mUserId);
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_USER_WORKS, params, this);
    }


    private void inflateData() {
        mShareItem = new ShareModel.ShareItem();
        mShareItem.photo = mUserWorks.share_samplereels_photo;
        mShareItem.title = mUserWorks.share_samplereels_title;
        mShareItem.url = mUserWorks.share_samplereels_url;
        mShareItem.content = mUserWorks.share_samplereels_content;
        setActivityTitle(mUserWorks.nickname + "的作品集");


        baseFragments = new ArrayList<>();
        baseFragments.add(CoverFragment.getInstance(mUserWorks, mUserId));
        baseFragments.add(CoverIndexFragment.getInstance(mUserWorks, mUserId));
        mViewPager.setAdapter(new UserInfoAdapter(getSupportFragmentManager(), baseFragments));
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
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getEventType() == EventType.EVENT_TYPE_BACK_COVER) {
            mViewPager.setCurrentItem(0);
        } else if (event.getEventType() == EventType.EVENT_TYPE_PAY_SUCCESS) {
            isPaySuc = true;
            loadData();
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_USER_WORKS:
                try {
                    mUserWorks = GsonUtil.fromJson(((JSONObject) result).optString("data"), UserWorks.class);
                } catch (Exception e) {
                    mUserWorks = null;
                }
                if (mUserInfo == null) {
                    findViewById(R.id.userwork_datafail_layout).setVisibility(View.VISIBLE);
                } else {
                    if (!isPaySuc) {
                        inflateData();
                    } else {
                        isPaySuc = false;
                        EventUtils.doPostEvent(EventType.EVENT_TYPE_COVER_REFRESH, mUserWorks);
                    }
                }
                break;
        }
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
}
