package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;

/**
 * 类名称:PublishNoteActivity
 * 类描述:发布资料补充页
 * 创建时间: 2018-11-05-16:12
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class PublishNoteActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_publish);
        setActivityTitle("发布资料补充");
        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);

    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.right_btn_submit:
                break;
        }
    }
}
