package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.fragment.UserInfoFragment;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.model.UserModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 2017/5/8.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class UserInfoActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BaseFragment mContent;
    private int mUserId;
    private String mUserName;
    private int isfollow;
    private UserInfo mOtherUserInfo;
    private List<BaseFragment> baseFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info, -1);


        mUserId = getIntent().getIntExtra("user_id", -1);
        mUserName = getIntent().getStringExtra("user_name");
        if (mUserId < 0) {
            showMessage("参数异常，请稍后重试");
            return;
        }

        initView();
        loadData();
    }


    private void initView() {
        fragmentManager = getSupportFragmentManager();
        ((TextView) findViewById(R.id.userinfo_tv_title)).setText(mUserName + "的个人页");
        findViewById(R.id.userinfo_btn_back).setOnClickListener(this);
        findViewById(R.id.userinfo_btn_profile).setOnClickListener(this);
        findViewById(R.id.userinfo_btn_works).setOnClickListener(this);
        findViewById(R.id.userinfo_btn_follow).setOnClickListener(this);
    }

    private void loadData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userid", mUserId);
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_OTHER_USERINFO, params, this);
    }

    private void initFragment() {
        baseFragments = new ArrayList<>();
        baseFragments.add(UserInfoFragment.newInstance(UserInfoFragment.TYPE_PROFILE, mOtherUserInfo));
        baseFragments.add(UserInfoFragment.newInstance(UserInfoFragment.TYPE_WORKS, mOtherUserInfo));
        baseFragments.add(UserInfoFragment.newInstance(UserInfoFragment.TYPE_FOLLOW, mOtherUserInfo));
        switchIndicator(0);
    }

    private void inflateData() {
        PictureManager.displayImage(TextUtils.isEmpty(mOtherUserInfo.background) ? mOtherUserInfo.head : mOtherUserInfo.background, (ImageView) findViewById(R.id.userinfo_iv_background));
        PictureManager.displayHead(mOtherUserInfo.head, (RoundedImageView) findViewById(R.id.userinfo_iv_head));
        ((TextView) findViewById(R.id.userinfo_tv_name)).setText(mOtherUserInfo.nickname);
        ((TextView) findViewById(R.id.userinfo_tv_info)).setText("关注：" + mOtherUserInfo.guanzhu + "人 | 推荐人：" + (TextUtils.isEmpty(mOtherUserInfo.invite) ? "暂无" : mOtherUserInfo.invite));
        final CheckBox followCheckBox = (CheckBox) findViewById(R.id.userinfo_cb_follow);
        followCheckBox.setChecked(isfollow == 0);
        followCheckBox.setText(isfollow == 0 ? "+关注" : "已关注");
        followCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isfollow = isfollow == 0 ? 1 : 0;
                followCheckBox.setText(isChecked ? "+关注" : "已关注");
                submitFollow(isfollow);
            }
        });
        followCheckBox.setVisibility(View.VISIBLE);
    }

    private void submitFollow(int follow) {
        HashMap localHashMap = new HashMap();
        localHashMap.put("followid", mUserId);
        localHashMap.put("token", mUserInfo.token);
        HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_HOME_FOLLOW : TaskType.TASK_TYPE_HOME_CLEAR_FOLLOW, localHashMap, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.userinfo_btn_back:
                onBackPressed();
                break;
            case R.id.userinfo_btn_profile:
                switchIndicator(0);
                break;
            case R.id.userinfo_btn_works:
                switchIndicator(1);
                break;
            case R.id.userinfo_btn_follow:
                switchIndicator(2);
                break;
        }
    }

    private void switchIndicator(int index) {
        int indicatorId;
        for (int i = 0; i < 3; i++) {
            indicatorId = getResources().getIdentifier("view_indicator_" + i, "id", getPackageName());
            findViewById(indicatorId).setVisibility(index == i ? View.VISIBLE : View.INVISIBLE);
        }
        switchFragment(baseFragments.get(index));
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

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_OTHER_USERINFO:
                UserModel userModel = GsonUtil.fromJson(result.toString(), UserModel.class);
                if (userModel != null && userModel.data != null) {
                    mOtherUserInfo = userModel.data;
                    isfollow = mOtherUserInfo.isguanzhu;
                    inflateData();
                    initFragment();
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
        }
    }

}
