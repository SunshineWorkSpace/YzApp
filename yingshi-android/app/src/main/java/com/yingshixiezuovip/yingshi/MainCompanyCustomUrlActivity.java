package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.UrlWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.CompanyInfoModel;
import com.yingshixiezuovip.yingshi.model.CompanyStatusModel;

import java.util.HashMap;
import java.util.Objects;

public class MainCompanyCustomUrlActivity extends BaseActivity {
    private CompanyStatusModel mCompanyStatusModel;
    private String mCustomUrl;
    private UrlWindow mUrlWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company_custon_url, R.string.title_activity_main_company_custon_url);

        mCompanyStatusModel = (CompanyStatusModel) getIntent().getSerializableExtra("company_status");
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.custom_tv_default)).setText(mCompanyStatusModel.qianzhui);
        ((TextView) findViewById(R.id.custom_tv_old)).setText(mCompanyStatusModel.sysurl);

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("保存");

        mUrlWindow = new UrlWindow(this);
        mUrlWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrlWindow.cancel();
                if (v.getId() == R.id.dialog_item2) {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("token", mUserInfo.token);
                    hashMap.put("content", mCustomUrl);
                    mLoadWindow.show(R.string.waiting);
                    HttpUtils.doPost(TaskType.TASK_TYPE_CUSTOM_URL, hashMap, MainCompanyCustomUrlActivity.this);
                }
            }
        });
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        showMessage("修改成功");
        onBackPressed();
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn_submit:
                mCustomUrl = ((TextView) findViewById(R.id.custom_et_url)).getText().toString();
                if (TextUtils.isEmpty(mCustomUrl) && TextUtils.isEmpty(mCustomUrl.trim())) {
                    showMessage("请输入自定义地址");
                    return;
                }
                mUrlWindow.show("地址为：" + mCompanyStatusModel.qianzhui + mCustomUrl + "\n确定是你修改的作品分享集地址？");
                break;
        }
    }
}
