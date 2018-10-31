package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.model.PushObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Resmic on 2017/8/15.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class NoticeMangerActivity extends BaseActivity {
    private PushObject mPushObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_manager, -1, false);
        mPushObject = (PushObject) getIntent().getSerializableExtra("push_object");

        if (mPushObject != null) {
            initView();
        } else {
            showMessage("推送消息解析异常");
            onBackPressed();
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.alert_message)).setText(mPushObject.message);
        if (mPushObject.data.type == 5) {
            ((TextView) findViewById(R.id.alert_title)).setText("推荐最新的相关作品—直接阅读");
            ((TextView) findViewById(R.id.alert_btn_submit)).setText("立即查看");
        } if(mPushObject.data.type == 2){
            ((TextView) findViewById(R.id.alert_title)).setText("尊敬的用户,你已经认证通过成为会员");
            ((TextView) findViewById(R.id.alert_btn_submit)).setText("确定");
        }else {
            ((TextView) findViewById(R.id.alert_title)).setText("消息提示");
            ((TextView) findViewById(R.id.alert_btn_submit)).setText("确定");
        }

        findViewById(R.id.alert_btn_cancel).setOnClickListener(this);
        findViewById(R.id.alert_btn_submit).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.alert_btn_cancel:
                onBackPressed();
                break;
            case R.id.alert_btn_submit:
                onBackPressed();
                if (mPushObject.data.type == 5) {
                    Intent intent = new Intent(this, MainDetailsActivity.class);
                    intent.putExtra("item_id", mPushObject.data.id);
                    intent.putExtra("item_name", mPushObject.message);
                    startActivity(intent);
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        onSuperBackPressed();
        JPushInterface.clearAllNotifications(this);
        overridePendingTransition(R.anim.activity_nomove_anim, R.anim.activity_nomove_anim);
    }
}
