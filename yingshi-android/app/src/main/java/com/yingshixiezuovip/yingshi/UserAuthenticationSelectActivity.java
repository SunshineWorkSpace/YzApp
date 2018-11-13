package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.EventType;

public class UserAuthenticationSelectActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication_select, R.string.title_activity_user_authentication_select);

        findViewById(R.id.authselect_btn_personal).setOnClickListener(this);
        findViewById(R.id.authselect_btn_company).setOnClickListener(this);
        findViewById(R.id.authselect_btn_school).setOnClickListener(this);
        findViewById(R.id.tv_shop_rz).setOnClickListener(this);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getEventType() == EventType.EVENT_TYPE_REFRESH_PRICE || event.getEventType() == EventType.EVENT_TYPE_REFRESH_USER_PRICE) {
            finish();
        }
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.authselect_btn_personal:
                startMActivity(new Intent(UserAuthenticationSelectActivity.this, MainAuthenticationActivity.class));
                break;
            case R.id.authselect_btn_company:
                startActivity(new Intent(UserAuthenticationSelectActivity.this, MainAuthenticationCompanyActivity.class));
                break;
            case R.id.authselect_btn_school:
                startActivity(new Intent(UserAuthenticationSelectActivity.this, MainAuthenticationSchoolActivity.class));
                break;
            case R.id.tv_shop_rz:
                startActivity(new Intent(UserAuthenticationSelectActivity.this, MainAuthenticationShopActivity.class));
                break;
            default:
                break;
        }
    }
}
