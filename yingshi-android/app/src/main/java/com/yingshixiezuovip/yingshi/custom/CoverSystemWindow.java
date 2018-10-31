package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.SysCoverAdapter;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskListener;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.CoverModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/9/13.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CoverSystemWindow extends BasePopupWindow implements TaskListener, SysCoverAdapter.OnCoverSelectedListener {
    private RecyclerView mRecyclerView;
    private SysCoverAdapter mSysCoverAdapter;
    private String mToken;
    private SysCoverAdapter.OnCoverSelectedListener onCoverSelectedListener;

    public CoverSystemWindow(Context mContext, String token) {
        super(mContext);
        mRecyclerView = (RecyclerView) findViewById(R.id.cover_recyclerview);
        findViewById(R.id.cover_submit).setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mToken = token;
        loadData();
    }

    private void loadData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", mToken);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_SYS_COVER, hashMap, this);
    }

    @Override
    public void show() {
        if (mSysCoverAdapter == null) {
            loadData();
        }
        super.show();
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_cover_system_layout, null);
    }

    @Override
    public void taskStarted(TaskType type) {
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
        }
        if (type == TaskType.TASK_TYPE_QRY_SYS_COVER) {
            CoverModel coverModel;
            try {
                coverModel = GsonUtil.fromJson(result.toString(), CoverModel.class);
                mSysCoverAdapter = new SysCoverAdapter(mContext, coverModel.data);
                mSysCoverAdapter.setOnCoverSelectedListener(this);
                mRecyclerView.setAdapter(mSysCoverAdapter);
            } catch (Exception e) {
                coverModel = null;
            }
            if (coverModel == null && mDialog.isShowing()) {
                showMessage("系统背景图片加载失败");
            }
        }
    }

    public void setOnCoverSelectedListener(SysCoverAdapter.OnCoverSelectedListener onCoverSelectedListener) {
        this.onCoverSelectedListener = onCoverSelectedListener;
    }

    @Override
    public void taskIsCanceled(TaskType type) {
    }

    @Override
    public void onCoverSelected(CoverModel.CoverItem coverItem) {
        if (onCoverSelectedListener != null) {
            onCoverSelectedListener.onCoverSelected(coverItem);
        }
    }
}
