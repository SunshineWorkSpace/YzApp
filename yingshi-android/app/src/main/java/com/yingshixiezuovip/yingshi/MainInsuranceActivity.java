package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.model.ShareModel;

public class MainInsuranceActivity extends BaseActivity {

    private ShareWindow mShareWindow;
    private ShareModel.ShareItem mShareItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_insurance);
        setActivityTitle("影者影视保险");
        initView();
    }

    private void initView() {
        findViewById(R.id.right_btn_name).setVisibility(View.GONE);
        findViewById(R.id.right_iv_more).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.insurance_btn_one).setOnClickListener(this);
        findViewById(R.id.insurance_btn_two).setOnClickListener(this);
        findViewById(R.id.insurance_btn_three).setOnClickListener(this);

        mShareItem = new ShareModel.ShareItem();
        mShareItem.url = Configs.ServerUrl + "/share/insurancelist?from=singlemessage&isappinstalled=1";
        mShareItem.content = "《影视保》是【影者APP联合平安保险专门为各类演艺影视工作者研发的保险.适合所有非危险戏份参演相关的工作人员.参加人员 0-50周岁均可投保,极大扩展了演职人员的保障范围。";
        mShareItem.title = "影视保|影视APP";
        mShareWindow = new ShareWindow(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        Intent intent = new Intent(this, MainInsuranceDetailsActivity.class);
        switch (v.getId()) {
            case R.id.insurance_btn_one:
                intent.putExtra("insurance_type", 1);
                break;
            case R.id.insurance_btn_two:
                intent.putExtra("insurance_type", 2);
                break;
            case R.id.insurance_btn_three:
                intent.putExtra("insurance_type", 3);
                break;
            case R.id.right_btn_submit:
                intent = null;
                mShareWindow.show(mShareItem, this);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
