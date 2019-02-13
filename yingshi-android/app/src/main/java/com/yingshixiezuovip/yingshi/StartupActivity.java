package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.yingshixiezuovip.yingshi.adapter.GuideAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.NavigationView;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.download.DownloadTask;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import org.json.JSONObject;

/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class StartupActivity extends BaseActivity implements Runnable, ViewPager.OnPageChangeListener {
    private Handler mHandler;
    private long launchTime = 0;
    private ViewPager mViewPager;
    private int[] mGuideImageIds = {
            R.mipmap.guide_1,
            R.mipmap.guide_2,
            R.mipmap.guide_3
    };
    private NavigationView mNavigationView;
    private GuideAdapter mGuideAdapter;
    private Intent mMainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup, -1, false);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        mMainIntent = new Intent(this, MainActivity.class);

        mHandler = new Handler();
        HttpUtils.doPost(TaskType.TASK_TYPE_STARTUP, null, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            mHandler.postDelayed(this, launchTime);
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_STARTUP:
//                if (((JSONObject) result).has("data")) {
//                    JSONObject dataObject = ((JSONObject) result).optJSONObject("data");
//                    if (dataObject.has("image")) {
//                        new DownloadTask(null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataObject.optString("image"));
//                    }
//                }
                mHandler.postDelayed(this, launchTime);
                break;
        }
    }

    @Override
    public void run() {
        String flag = SPUtils.getString(false, this, "startup_flag", "0");
        if ("0".equalsIgnoreCase(flag)) {
            startGuide();
        } else {
            launchAPP();
        }
    }

    private void startGuide() {
        findViewById(R.id.start_init_layout).setVisibility(View.GONE);
        findViewById(R.id.start_guide_layout).setVisibility(View.VISIBLE);
        mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        mGuideAdapter = new GuideAdapter(this, mGuideImageIds);
        mGuideAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putString(false, StartupActivity.this, "startup_flag", "1");
                launchAPP();
            }
        });
        mViewPager.setAdapter(mGuideAdapter);
        mViewPager.addOnPageChangeListener(this);
        mNavigationView = (NavigationView) findViewById(R.id.guide_naviegationview);
        mNavigationView.setCountPosition(mGuideImageIds.length);
    }

    private void launchAPP() {
        Intent intent;
        UserInfo userInfo = SPUtils.getUserInfo(this);
        if (userInfo != null) {
            intent = mMainIntent;
        } else {
            intent = new Intent(this, StartupLoginNewActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        YingApplication.getInstance().startActivity(intent);
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mNavigationView.setSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
