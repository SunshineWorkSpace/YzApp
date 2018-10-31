package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.utils.CommUtils;

public class MainAboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_about, R.string.title_activity_main_about);

        ((TextView) findViewById(R.id.about_tv_version)).setText("当前版本：" + CommUtils.getVersionName(this));
    }

}
