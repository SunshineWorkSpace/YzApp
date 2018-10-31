package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.HashMap;

/**
 * * Created by Resmic on 2017/5/4.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class MainInviteActivity extends BaseActivity implements UMShareListener {
    private ShareWindow mShareWindow;
    private ShareModel mShareModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_invite, -1, false);

        initView();
        initShareWindow();
    }

    private void initShareWindow() {
        mShareWindow = new ShareWindow(this);
    }

    private void initView() {
        findViewById(R.id.invite_btn_back).setOnClickListener(this);
        findViewById(R.id.invite_btn_create).setOnClickListener(this);
        findViewById(R.id.invite_btn_apply).setOnClickListener(this);
    }

    private void loadData() {
        mLoadWindow.show(R.string.text_request);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_SHARE_CONTENT, params, this);
    }


    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.invite_btn_back:
                onBackPressed();
                break;
            case R.id.invite_btn_create:
            case R.id.invite_btn_apply:
                loadData();
                break;
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
            case TASK_TYPE_QRY_SHARE_CONTENT:
                mShareModel = GsonUtil.fromJson(result.toString(), ShareModel.class);
                if (mShareModel == null || mShareModel.data == null) {
                    showMessage("分享链接获取失败，请稍后再试");
                    return;
                } else {
                    mShareWindow.show(mShareModel.data, this);
                }
                break;
        }
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        mLoadWindow.show(R.string.umeng_socialize_text_waitting_share);
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        mLoadWindow.cancel();
        showMessage("分享成功");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        mLoadWindow.cancel();
        showMessage("分享失败，请稍后重试");
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        mLoadWindow.cancel();
        showMessage("取消分享");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
