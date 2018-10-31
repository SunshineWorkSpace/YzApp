package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.common.SysOSUtil;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.utils.EventUtils;

import java.util.HashMap;

public class MainDetailsChangeActivity extends BaseActivity {
    private int mWorkId;
    private String mWorkPrice;
    private String mWorkUnit;
    private AlertWindow mDeleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_details_change, R.string.title_activity_main_details_change);
        mWorkId = getIntent().getIntExtra("works_id", -1);
        mWorkPrice = getIntent().getStringExtra("works_price");
        mWorkUnit = getIntent().getStringExtra("works_unit");
        if (mWorkId < 0) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        initView();
        initWindow();
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_REFRESH_PRICE:
                finish();
                break;
        }
    }

    private void initView() {
        findViewById(R.id.workedit_btn_reedit).setOnClickListener(this);
        findViewById(R.id.workedit_btn_delete).setOnClickListener(this);
        findViewById(R.id.workedit_btn_edit).setOnClickListener(this);
        findViewById(R.id.workedit_btn_edit).setVisibility(mUserInfo.type == 1 ? View.GONE : View.VISIBLE);
    }

    private void initWindow() {
        mDeleteWindow = new AlertWindow(this, false);
        mDeleteWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    doDelete();
                }
            }
        });
    }

    private void doDelete() {
        HashMap<String, Object> deleteParams = new HashMap<>();
        deleteParams.put("token", mUserInfo.token);
        deleteParams.put("id", mWorkId);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_DELETE_WORKS, deleteParams, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.workedit_btn_delete:
                mDeleteWindow.show("", "确认删除该作品？", "取消", "确认");
                break;
            case R.id.workedit_btn_edit:
                Intent intent = new Intent(this, MainDetailsChangePriceActivity.class);
                intent.putExtra("works_id", mWorkId);
                intent.putExtra("works_price", mWorkPrice);
                intent.putExtra("works_unit", mWorkUnit);
                startActivityForResult(intent, 1000);
                break;
            case R.id.workedit_btn_reedit:
                intent = new Intent(this, MainDetailsEditActivity.class);
                intent.putExtra("works_id", mWorkId);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_DELETE_WORKS:
                showMessage("删除成功");
                setResult(Activity.RESULT_OK);
                EventUtils.doPostEvent(EventType.EVENT_TYPE_CLOSE_DETAILS);
                onBackPressed();
                break;
        }
    }
}
