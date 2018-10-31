package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.yingshixiezuovip.yingshi.adapter.LableAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.quote.flowlayout.TagFlowLayout;

public class MainPublishLableActivity extends BaseActivity {
    private TagFlowLayout lableFlow;
    private LableAdapter mLableAdapter;
    private String mLableStrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_publish_lable, R.string.activity_main_publish_lable_title);

        initView();
    }

    private void initView() {
        mLableStrs = getIntent().getStringExtra("lables");
        lableFlow = (TagFlowLayout) findViewById(R.id.lable_flowlayout);

        mLableAdapter = new LableAdapter(this);
        lableFlow.setAdapter(mLableAdapter);

        if(!TextUtils.isEmpty(mLableStrs)){
            String [] labs = mLableStrs.split(",");
            for(String lab:labs){
                mLableAdapter.add(lab);
            }
        }

        findViewById(R.id.lable_btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lable_btn_submit) {
            addLable();
        }
    }

    private void addLable() {
        String lable = (((EditText) findViewById(R.id.lable_et_input)).getText().toString() + "").trim();
        if (TextUtils.isEmpty(lable)) {
            showMessage("请输入标签名");
            return;
        }

        if(mLableAdapter.getCount() >= 6){
            showMessage("最多允许6个标签，请先删除其他再添加");
            return;
        }

        mLableAdapter.add(lable);
        ((EditText) findViewById(R.id.lable_et_input)).setText(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        mLableStrs = mLableAdapter.getLables();
        intent.putExtra("lables", mLableStrs);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
